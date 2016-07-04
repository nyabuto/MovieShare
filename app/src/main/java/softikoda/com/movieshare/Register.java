package softikoda.com.movieshare;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import Movii.ControlDB;

public class Register extends AppCompatActivity implements LocationListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    final Context context=this;
    EditText Register_Username,Register_Phone,Shop_Description;
    Button Register_Save;
Spinner userSpinner;
    String username,phone;
    int registrationType,userid;
int UserType;
    String localPhone="";
    String localUserType="";
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    public LocationManager locationManager;
    double latitude,longitude;
    String shop_description="";
    private boolean isGPSEnabled;

    String url="http://softikoda.com/movieshare/register.php";
    ProgressDialog progressDialog;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(Register.this);
        progressDialog.setMessage("");
        progressDialog.setCancelable(false);

        buildGoogleApiClient();
        if(mGoogleApiClient!= null){
            mGoogleApiClient.connect();
        }
        else
            Toast.makeText(getApplicationContext(), "Not connected...", Toast.LENGTH_SHORT).show();


        Register_Username = (EditText) findViewById(R.id.Register_Username);
        Register_Phone = (EditText) findViewById(R.id.Register_Phone);
        Register_Save = (Button) findViewById(R.id.Register_Save);
        userSpinner = (Spinner) findViewById(R.id.user_type);
        Shop_Description=(EditText) findViewById(R.id.Shop_description);

        userSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                 Shop_Description.setVisibility(EditText.GONE);
                    Register_Username.setHint("Enter username (required)");
                    Register_Save.setText("Save User");
//                    UserType=2;
                }
                else{
                    Shop_Description.setVisibility(EditText.VISIBLE);
                    Register_Username.setHint("Enter Shop name (required)");
                    Register_Save.setText("Save Shop");
//                    UserType=1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //   check if the user exist
//        getLocal phone in local storage
        String returned=new ControlDB(context.getApplicationContext()).checkUser();
        Log.d("returned : ",returned);
        if(!returned.equals("##")) {
            String localDetails[] = returned.split("##");
             localPhone = localDetails[0];
             localUserType = localDetails[1];
        }
        Log.d("Existing user id : ", "" + localPhone+" size : "+localPhone.length());
        if(localPhone.length()>0){
            progressDialog.setMessage("Logging in to the system...");
            progressDialog.show();
            phone=localPhone;
            UserType=Integer.parseInt(localUserType);

            if (internetConnectionsAvailable()) {

                url = "http://softikoda.com/movieshare/getUserID.php?phone="+phone+"&user_type="+UserType;
                url = url.replace(" ","%20");
                RequestQueue queue = Volley.newRequestQueue(this);
                StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if(response.equals("0")){
//                            user does not exist.......
                            Toast.makeText(getApplicationContext(),"Unknown user ",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            userid=Integer.parseInt(response);
                            goNext();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
                );
                queue.add(request);
            }
            else{
             Toast.makeText(getApplicationContext(),"No internet connection",Toast.LENGTH_SHORT).show();
            }

        }

        else{

            username=phone="";
            registrationType=userid=0;
            Register_Save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addNewUser();
                }
            });


        }

    }


    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        Toast.makeText(getApplicationContext(), "Failed to connect...", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnected(Bundle arg0) {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null) {
            latitude=mLastLocation.getLatitude();
            longitude=mLastLocation.getLongitude();
            Log.d("map ready","yes map is ready");

        }
        if(Build.VERSION.SDK_INT == 23) {
            int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocation = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
            if (hasCoarseLocation != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
        }

    }
    @Override
    public void onConnectionSuspended(int arg0) {
        Toast.makeText(getApplicationContext(), "Connection suspended...", Toast.LENGTH_SHORT).show();

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void addNewUser(){
//        GET LONGITUDE AND LATITUDE

       int pos= userSpinner.getSelectedItemPosition();
        if(pos==0){
         UserType=2;
        }
        else {
           UserType=1;
        }

        Log.d("addusermethod",""+UserType);

                username = Register_Username.getText().toString().trim();
                phone = Register_Phone.getText().toString().trim();
        shop_description=Shop_Description.getText().toString().trim();

                if (phone.length() == 10) {
//               save data to the local server and online server
                    if (internetConnectionsAvailable()) {
if(UserType==1){
    progressDialog.setMessage("Saving shop details...");
}
                        else{
    progressDialog.setMessage("Saving user details...");
                        }

                            progressDialog.show();

                            url = "http://softikoda.com/movieshare/register.php";
//save user details to the online database and then to the local database

                            url += "?phone=" + phone + "&&username=" + username+"&&longitude="+longitude+"&&latitude="+latitude+"&user_type="+UserType+"&shop_description="+shop_description;
                            url = url.replace(" ","%20");
                            RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
                            StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

//                                      add to local db
                                        boolean addStatus = new ControlDB(context.getApplicationContext()).addUser(phone, username,""+UserType);
                                        if (addStatus) {
                                            if(UserType==2) {
                                                Toast.makeText(context.getApplicationContext(), "User added successfully", Toast.LENGTH_LONG).show();
                                            }
                                            else if (UserType==1){
                                                Toast.makeText(context.getApplicationContext(), "Shop added successfully", Toast.LENGTH_LONG).show();
                                            }
                              if (internetConnectionsAvailable()) {
                                                url = "http://softikoda.com/movieshare/getUserID.php?phone="+phone+"&user_type="+UserType;
                                                url = url.replace(" ","%20");
                                  Log.d("urlcalled",url);
                                                RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
                                                StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        Log.d("response from getting",response);
                                                        progressDialog.dismiss();
                                                        if(response.equals("0")){
//                                                            USER DOES NOT EXIST
                                                            Toast.makeText(getApplicationContext(),"Unknown user ",Toast.LENGTH_SHORT).show();
                                                        }
                                                        else{
                                                            userid=Integer.parseInt(response);
                                                            goNext();
                                                        }
                                                    }
                                                }, new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {

                                                    }
                                                }
                                                );
                                                queue.add(request);
                                            }
                                        }


                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            }
                            );
                            queue.add(request);

                    }
                }
    }

    public void goNext(){
        progressDialog.dismiss();
        this.finish();
        Log.d("topassusertype",""+UserType);
        Bundle bundle = new Bundle();
        bundle.putString("user_id",""+userid);
        bundle.putInt("user_type",UserType);
        Intent nextPage = new Intent(Register.this,MoviShare.class);
        nextPage.putExtras(bundle);
        startActivity(nextPage);
    }

    private boolean internetConnectionsAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo!=null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        isGPSEnabled = true;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}
