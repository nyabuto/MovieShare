package softikoda.com.movieshare;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.telephony.SmsManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Movii.AllMoviesMarkers;
import Movii.ParentUsers;
import Movii.SharedMovies;

public class MoviShare extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback,LocationListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener  {
    private static GoogleMap mMap;
    double latitude,longitude;
    JSONArray allUsers=null;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private HashMap<Marker, AllMoviesMarkers> mMarkersHashMap;
    private ArrayList<AllMoviesMarkers> mMyMarkersArray = new ArrayList<AllMoviesMarkers>();
    String url="http://softikoda.com/movieshare/AllMovies.php";
    LatLng currentLocation;
    String user_id;
    String movie_genre;
int user_type=0;

    Polyline polyline = null;
    MapView mapView;

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    String id="0",User="";
    int userType=0;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movi_share);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       //Get my user id
        Bundle getUser = getIntent().getExtras();
        user_id=getUser.getString("user_id");
       user_type=getUser.getInt("user_type");

        movie_genre="";

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("user_id",""+user_id);
                bundle.putInt("user_type",user_type);
                Intent intent = new Intent(MoviShare.this,AddShareMovie.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

//        Menu items

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
if(user_type==1) {
    MenuItem item = navigationView.getMenu().getItem(2);
    item.setVisible(false);
}
        mapView=(MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        MapsInitializer.initialize(getApplicationContext());
        mMap=mapView.getMap();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading ...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        buildGoogleApiClient();
        if(mGoogleApiClient!= null){
            mGoogleApiClient.connect();
        }
        else
            Toast.makeText(getApplicationContext(), "Not connected...", Toast.LENGTH_SHORT).show();

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
            if(isInternetAvailable()) {
                if (longitude !=0) {
                    CreateMap();
                }
            }
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

        if(isInternetAvailable()) {
            if (longitude > 0) {
                CreateMap();
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "Error no internet available", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isInternetAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo!=null && activeNetworkInfo.isConnected();
    }

    public void loadMovies(){
        mMap.clear();
        mMarkersHashMap = new HashMap<Marker, AllMoviesMarkers>();
        mMyMarkersArray.clear();

        String finalUrl=url+"?longitude="+longitude+"&latitude="+latitude+"&user_id="+user_id+"&type="+user_type;

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, finalUrl, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {


                try {
                    int success=response.getInt("success");
                    if(success>0) {
                        allUsers = response.getJSONArray("movies");
                        for (int i = 0; i < allUsers.length(); i++) {
                            JSONObject userData = allUsers.getJSONObject(i);

                            String shop_name, phone, distance, shopid;
                            double shopLat, shopLong;
                            int no_movies = 0,type=0;
                            shopid = userData.getString("user_id");
                            shop_name = userData.getString("username");
                            phone = userData.getString("phone");
                            shopLat = userData.getDouble("latitude");
                            shopLong = userData.getDouble("longitude");
                            no_movies = userData.getInt("count");
                            type=userData.getInt("user_type");
                            double roundOff = Math.round(userData.getDouble("distance") * 100.0) / 100.0;
                            distance = roundOff + " Kms away.";

                            mMyMarkersArray.add(new AllMoviesMarkers(type, shopid, shop_name, no_movies, distance, phone, shopLat, shopLong));

                        }

                    }
                    plotMarkers(mMyMarkersArray);
                    currentLocation = new LatLng(latitude, longitude);

                    mMap.addMarker(new MarkerOptions().position(currentLocation).title("My Location.").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(10));

                    progressDialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
             //   Log.e("Error", error.getMessage());
            }
        });
        queue.add(request);

    }

    public void CreateMap(){
    loadMovies();
    }

    private void plotMarkers(ArrayList<AllMoviesMarkers> markers)
    {

        if(markers.size() > 0)
        {
            for (final AllMoviesMarkers myMarker : markers)
            {

                // Create user marker with custom icon and other options
                MarkerOptions markerOption = new MarkerOptions().position(new LatLng(myMarker.getLatitude(), myMarker.getLongitude()));
                if(myMarker.getUser_type()==1) { //for shop movies
                    markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                }
                else if(myMarker.getUser_type()==2){// For shared movies
                    markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                }
                else{
                    markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                }
                Marker currentMarker = mMap.addMarker(markerOption);
                mMarkersHashMap.put(currentMarker, myMarker);

                mMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter());
            }
        }
    }


    public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        public MarkerInfoWindowAdapter() {
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {

            AllMoviesMarkers myMarker = mMarkersHashMap.get(marker);
            if (myMarker != null) {
                View v = getLayoutInflater().inflate(R.layout.map_movies, null);

                id = myMarker.getId();
                userType = myMarker.getUser_type();
                User = myMarker.getName();

                TextView markerUsername = (TextView) v.findViewById(R.id.username);
                TextView markerDistance = (TextView) v.findViewById(R.id.distance);
                TextView markerNoMovies = (TextView) v.findViewById(R.id.no_movies);
                Button button_viewMore = (Button) v.findViewById(R.id.button_viewMore);

                button_viewMore.setVisibility(Button.GONE);
                try {
                    markerUsername.setText(myMarker.getName());
                    markerDistance.setText(myMarker.getDistance());
                    if (myMarker.getNo_of_movies() == 1) {
                        markerNoMovies.setText(myMarker.getNo_of_movies() + " Movie.");
                    } else {
                        markerNoMovies.setText(myMarker.getNo_of_movies() + " Movies.");
                    }

                    String url = getMapsApiDirectionsUrl(latitude, longitude, myMarker.getLatitude(), myMarker.getLongitude());
                    ReadTask downloadTask = new ReadTask();
                    downloadTask.execute(url);
                } catch (Exception e) {

                }



            return v;
        }
            else{
                return null;
            }
        }
    }

    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {

            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }
    }

    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;
            if (polyline != null) {
                polyline.remove();
            }
            // traversing through routes
            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = routes.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }
                polyLineOptions.addAll(points);
                polyLineOptions.width(20);
//                polyLineOptions.color(Color.rgb(2, 4, 8));
                polyLineOptions.color(Color.MAGENTA);
            }

            polyline = mMap.addPolyline(polyLineOptions);
        }
    }

    private String getMapsApiDirectionsUrl(double sourceLat, double sourceLong, double destinationLat, double destinationLong) {
        String url = "http://maps.googleapis.com/maps/api/directions/json?"
                + "origin=" + sourceLat + "," + sourceLong
                + "&destination=" + destinationLat + "," + destinationLong
                + "&sensor=false&units=metric&mode=driving";

        return url;
    }


    @Override
    public void onLocationChanged(Location location) {

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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.movi_share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
//
//        if (id == R.id.nav_watchedMovies) {
//            Bundle bundle = new Bundle();
//            bundle.putString("user_id",user_id);
//            Intent intent = new Intent(MoviShare.this,MoviesWatched.class);
//            intent.putExtras(bundle);
//            startActivity(intent);
//            // Handle the camera action
//        }
//
//        else if (id == R.id.nav_wishList) {
//            Bundle bundle = new Bundle();
//            bundle.putString("user_id",user_id);
//            Intent intent = new Intent(MoviShare.this,WishList.class);
//            intent.putExtras(bundle);
//            startActivity(intent);
//        }
//
//        else if (id == R.id.nav_mostWatched) {
//            Bundle bundle = new Bundle();
//            bundle.putString("user_id",user_id);
//            Intent intent = new Intent(MoviShare.this,MostWatched.class);
//            intent.putExtras(bundle);
//            startActivity(intent);
//        }
//
        if (id == R.id.nav_MyMovies) {
            Bundle bundle = new Bundle();
            bundle.putString("user_id",user_id);
            bundle.putInt("user_type",user_type);
            Intent intent = new Intent(MoviShare.this,MyMovies.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }

       else if (id == R.id.nav_movies) {
            Bundle bundle = new Bundle();
            bundle.putString("user_id",user_id);
            bundle.putString("movie_genre","1");
            bundle.putInt("user_type",user_type);
            Intent intent = new Intent(MoviShare.this,Movies.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        else if (id == R.id.nav_series) {
            Bundle bundle = new Bundle();
            bundle.putString("user_id",user_id);
            bundle.putString("movie_genre","2");
            bundle.putInt("user_type",user_type);
            Intent intent = new Intent(MoviShare.this,Movies.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }

//        else if (id == R.id.nav_requested) {
//            Bundle bundle = new Bundle();
//            bundle.putString("user_id",user_id);
//            Intent intent = new Intent(MoviShare.this,ShareRequests.class);
//            intent.putExtras(bundle);
//            startActivity(intent);
//        }

        else if (id == R.id.nav_requested) {
            Bundle bundle = new Bundle();
            bundle.putString("user_id",user_id);
            bundle.putInt("user_type",user_type);

            Intent intent = new Intent(MoviShare.this,RequestedMovies.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }


        else if (id == R.id.nav_mylist) {
            Bundle bundle = new Bundle();
            bundle.putString("user_id",user_id);
            bundle.putInt("user_type",user_type);
            Intent intent = new Intent(MoviShare.this,MyList.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        else if (id == R.id.nav_statistics) {
            Bundle bundle = new Bundle();
            bundle.putString("user_id",user_id);
            bundle.putInt("user_type",user_type);
            Intent intent = new Intent(MoviShare.this,Statistics.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        else if (id == R.id.nav_aboutUs) {
            Bundle bundle = new Bundle();
            bundle.putString("user_id",user_id);
            bundle.putInt("user_type",user_type);
            Intent intent = new Intent(MoviShare.this,AboutUs.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
