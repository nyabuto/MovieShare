package softikoda.com.movieshare;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShareRequests extends AppCompatActivity {
ListView  lstShareRequests;
    ArrayList<String> ALMovieName = new ArrayList<String>();
    ArrayList<String> ALMovieGenre = new ArrayList<String>();
    ArrayList<String> ALImageUrl = new ArrayList<String>();
    ArrayList<HashMap<String,String>> allData = new ArrayList<HashMap<String, String>>();
    String url="http://softikoda.com/movieshare/getShared.php?user_id=";
    String manageUrl="http://softikoda.com/movieshare/ManageShare.php";
    String user_id="";
    JSONArray allMovies=null;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_requests);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Get my user id
        Bundle getUser = getIntent().getExtras();
        user_id=getUser.getString("user_id");
        allData.clear();
        ALMovieName.clear();ALMovieGenre.clear();ALImageUrl.clear();

        lstShareRequests = (ListView) findViewById(R.id.listView_ShareRequest);

        loadAllMovies();
    }


    public int loadAllMovies(){
        progressDialog = new ProgressDialog(ShareRequests.this);
        progressDialog.setMessage("Loading most requested movies ...");
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url+""+user_id, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("response is : ",response.toString());
                try {
                    allMovies = response.getJSONArray("share_requests");
                    for (int i = 0; i < allMovies.length(); i++) {
                        JSONObject s = allMovies.getJSONObject(i);
                        HashMap<String,String> myMap = new HashMap<>();

                        String request_id=s.getString("request_id");
                        String share_id=s.getString("share_id");
                        String movie_id=s.getString("movie_id");
                        String movie_name = s.getString("movie_name");
                        String movie_genre=s.getString("movie_genre");
                        String image_url ="http://softikoda.com/movieshare/Images/"+ s.getString("image_url");
                        String requester_id=s.getString("requester_id");

                        myMap.put("movie_id",movie_id);
                        myMap.put("movie_name",movie_name);
                        myMap.put("movie_genre",movie_genre);
                        myMap.put("image_url",image_url);
                        myMap.put("request_id", request_id);
                        myMap.put("share_id",share_id);
                        myMap.put("requester_id",requester_id);

                        allData.add(myMap);
                        ALMovieName.add(movie_name);
                        ALImageUrl.add(image_url);
                        ALMovieGenre.add(movie_genre);

                    }
                    Log.d("size here in app : ",""+allData.size());
                    if (allData.size() > 0) {

                        CustomListview_ShareRequest adapter = new CustomListview_ShareRequest(ShareRequests.this,ALImageUrl.toArray(new String[ALImageUrl.size()]),ALMovieName.toArray(new String[ALMovieName.size()]),ALMovieGenre.toArray(new String[ALMovieGenre.size()]));
                        lstShareRequests.setAdapter(adapter);
                        lstShareRequests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                HashMap<String,String> clickedData=allData.get(position);
                                final String movie_name=clickedData.get("movie_name");
                                final  String requester_id=clickedData.get("requester_id");
                                final String share_id=clickedData.get("share_id");
                                final  String movie_id=clickedData.get("movie_id");
                                final String request_id=clickedData.get("request_id");


                                final Dialog dialog = new Dialog(ShareRequests.this);
                                dialog.setContentView(R.layout.share_popup);
                                dialog.setTitle("Action on "+movie_name);
                                dialog.setCancelable(true);
                                Button btnAccept=(Button) dialog.findViewById(R.id.share_popup_accept);
                                Button btnDeny=(Button) dialog.findViewById(R.id.share_popup_deny);

                                btnAccept.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.cancel();

                                        updateMovieStatus(requester_id,share_id,"accept");
                                    }
                                });

                                btnDeny.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.cancel();

                                        updateMovieStatus(requester_id,share_id,"deny");
                                    }
                                });

                                dialog.show();
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "No data found on the server", Toast.LENGTH_LONG).show();
                    }




                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("trial : ", " error "+e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", error.getMessage());
            }
        })

        {

        };
        queue.add(request);
        progressDialog.dismiss();
        return allData.size();
    }

    public void updateMovieStatus(final String request_id,final String share_id, final String source){
        RequestQueue ManageMoviewQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, manageUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", error.getMessage());
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("request_id", request_id);
                params.put("share_id", share_id);
                params.put("action", "" + source);
                return params;
            }
        };
        ManageMoviewQueue.add(request);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
