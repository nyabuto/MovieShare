package softikoda.com.movieshare;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
public class AddShareMovie extends AppCompatActivity{
    Spinner movieGenre, cameraOptions;
    Button addShare_btnSaveMovie;
    EditText addShare_MovieName,addShare_MovieCost;
    ImageView addShare_imageUpload;
    private static final int RESULT_LOAD_IMAGE = 1;
    Bitmap image = null;
    String encodedImage;
    String file_url = "";
    String user_url ="http://softikoda.com/movieshare/addMovie.php";
    String shop_url ="http://softikoda.com/movieshare/AddShopMovie.php";
    String movie_name = "";
    String userid="";
    ProgressDialog progressDialog;
    int TAKE_PHOTO_CODE = 0;
    Uri selectedImage;
    int counter = 0;
    String dir;
    File newdir;
    int rotations=0;
    String movie_genre="";
    int user_type=0;
    int cost=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_share_movie);
        Toolbar toolbar = (Toolbar) findViewById(R.id.addShareMovieToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//Get my user id
        Bundle getUser = getIntent().getExtras();
        userid=getUser.getString("user_id");
        user_type=getUser.getInt("user_type");

        dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
        newdir = new File(dir);
        newdir.mkdirs();

        movieGenre = (Spinner) findViewById(R.id.addShareMovie_genre);
        addShare_MovieCost = (EditText) findViewById(R.id.movie_cost);
        addShare_MovieName = (EditText) findViewById(R.id.addShare_MovieName);
        addShare_imageUpload = (ImageView) findViewById(R.id.addShare_imageUpload);
        cameraOptions = (Spinner) findViewById(R.id.addShare_camera_options);
        addShare_btnSaveMovie = (Button) findViewById(R.id.addShare_btnSaveMovie);

        if(user_type==1){
            addShare_MovieCost.setVisibility(EditText.VISIBLE);
            file_url=shop_url;
        }
        else{
            addShare_MovieCost.setVisibility(EditText.GONE);
            file_url=user_url;
        }

        cameraOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//             Item has been selected.

                if (position == 0) {
//                   No action selected
 }

                if (position == 1) {
               newImageUpload();
}

                else if (position == 2) {
//                    upload from gallery
                  existingImageUpload();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
//Nothing has been selected.
int position=parent.getLastVisiblePosition();
                Log.d("last visible pos",""+position);
                if (position == 0) {
//                   No action selected
                }

                if (position == 1) {
                    newImageUpload();
                }

                else if (position == 2) {
//                    upload from gallery
                    existingImageUpload();
                }
            }
        });

        addShare_btnSaveMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isInternetAvailable()) {
                    if (addShare_MovieName.getText().toString().trim() != null) {
                        movie_name = addShare_MovieName.getText().toString().trim();
                    } else {
                        movie_name = "";
                    }

                    if(movieGenre.getSelectedItemPosition()>0) {
                        movie_genre = ""+movieGenre.getSelectedItemPosition();
                        if (movie_name != null && !movie_name.equals("")) {
                            if(user_type==1) {
                                if (!addShare_MovieCost.getText().toString().trim().equals("")) {
                                    cost = Integer.parseInt(addShare_MovieCost.getText().toString().trim());
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),"Enter cost of the movie",Toast.LENGTH_SHORT).show();
                                }
                            }

                            if (image != null) {
                                progressDialog = new ProgressDialog(AddShareMovie.this);
                                progressDialog.setMessage("Saving data ...");
                                progressDialog.setCancelable(false);
                                progressDialog.show();

                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                image.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOutputStream);
                                encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                                Log.d("here : ", encodedImage);
                                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

                                StringRequest request = new StringRequest(Request.Method.POST, file_url, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.d("response data ", response);
//                                        if(!response.contains("Failed")){
//
//                                        }
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(),response, Toast.LENGTH_SHORT).show();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d("response error  ", "" + error);
                                     progressDialog.dismiss();
                                    }
                                }
                                ) {
                                    @Override
                                    protected Map<String, String> getParams() {
                                        Map<String, String> params = new HashMap<>();
                                        params.put("image_url", encodedImage);
                                        params.put("movie_name", movie_name);
                                        params.put("user_id", "" + userid);
                                        params.put("movie_genre", movie_genre);
                                        params.put("cost", ""+cost);
                                        params.put("actionSource", "shareMovie");
                                        return params;
                                    }

                                };
                                queue.add(request);
                            } else {
//                    missing image selection
                                Toast.makeText(getApplicationContext(), "Please Select an image from gallery", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Please select movie genre", Toast.LENGTH_SHORT).show();
                        }
                    }

                        else {
//                 missing movie name
                            Toast.makeText(getApplicationContext(), "Please enter movie name", Toast.LENGTH_SHORT).show();
                        }

                } else {
                    Toast.makeText(getApplicationContext(), "Limited or no Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImage = data.getData();
        }
        else if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {

        }
        addShare_imageUpload.setImageURI(selectedImage);
Log.d("Image uri",selectedImage.getPath());
        image = ((BitmapDrawable) addShare_imageUpload.getDrawable()).getBitmap();



    }

    private boolean isInternetAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo!=null && activeNetworkInfo.isConnected();
    }

    private void newImageUpload(){
        counter++;
        String file = dir + "toUpload" + counter + ".jpg";
        File newfile = new File(file);
        try {
            newfile.createNewFile();
        } catch (IOException e) {
        }
        selectedImage = Uri.fromFile(newfile);
//                    take picture
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImage);
        startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);

    }

    private void existingImageUpload(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
}
    private void rotateImage(){
        addShare_imageUpload.setRotation(90);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_share_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_rotateImage) {
            int angle=0;

            if(rotations==0){
                angle=90;
            }
           else if(rotations==1){
                angle=180;
            }
          else  if(rotations==2){
                angle=270;
            }
          else  if(rotations==3){
                angle=360;
            }
            else{
                rotations=0;
                angle=90;
            }

           rotations++;
            addShare_imageUpload.setRotation(angle);
           // return true;
        }

        if(id==android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

}
