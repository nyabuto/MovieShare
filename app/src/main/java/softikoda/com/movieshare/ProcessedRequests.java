package softikoda.com.movieshare;

import android.Manifest;
import android.app.AlertDialog;
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
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Movii.MovieMarkers;

public class ProcessedRequests extends AppCompatActivity implements OnMapReadyCallback,LocationListener {

    private GoogleMap mMap;
    double latitude,longitude;
    JSONArray allUsers=null;
    String provider;
    private boolean isGPSEnabled;
    Location location;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    public LocationManager locationManager;
    private HashMap<Marker, MovieMarkers> mMarkersHashMap;
    private ArrayList<MovieMarkers> mMyMarkersArray = new ArrayList<MovieMarkers>();
    String url="http://softikoda.com/movieshare/getResponse.php";
    LatLng latitude_longitude,currentLocation;
    String user_id;
    Polyline polyline = null;
 @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processed_requests);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
     getSupportActionBar().setDisplayHomeAsUpEnabled(true);
     getSupportActionBar().setDisplayShowHomeEnabled(true);

     Bundle getUser = getIntent().getExtras();
     user_id=getUser.getString("user_id");

     SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
             .findFragmentById(R.id.mapShared);
     mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        getMyLocation();
        if(isInternetAvailable()) {
            getMyLocation();
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

    private void enableGPS() {
        if (!isGPSEnabled) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("GPS settings");
            alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });

            // on pressing cancel button
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            // Showing Alert Message
            alertDialog.show();

        }
    }

    private void getMyLocation(){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);

        if(provider!=null && !provider.equals("")){
            Log.d("provider is : ", provider);
            // Get the location from the given provider
            Location location = locationManager.getLastKnownLocation(provider);
            if(location!=null) {
                Log.d("provider is : ", location.toString());

                latitude = location.getLatitude();
                longitude = location.getLongitude();
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

                if (location != null) {
                    onLocationChanged(location);
                }
            }
            else {
                Toast.makeText(getApplicationContext(), "Location can't be retrieved", Toast.LENGTH_SHORT).show();
            }
        }else{
            enableGPS();
//            Toast.makeText(getApplicationContext(), "No Provider Found", Toast.LENGTH_SHORT).show();
        }
        if(Build.VERSION.SDK_INT == 23) {
            int hasWriteContactsPermission = this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocation = this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
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

    public void CreateMap(){
        mMap.clear();
//JSONObject response;
//        try {
        String finalUrl=url+"?longitude="+longitude+"&latitude="+latitude+"&user_id="+user_id;
Log.d("finalUrl : ",finalUrl);
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, finalUrl, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                mMap.clear();
                Log.d("responseData : ",response.toString());
                try {
                    allUsers = response.getJSONArray("shared_movies");
                    mMarkersHashMap = new HashMap<Marker, MovieMarkers>();
                    mMyMarkersArray.clear();
                    for (int i = 0; i < allUsers.length(); i++) {
                        JSONObject userData = allUsers.getJSONObject(i);
Log.d("username : ",userData.getString("username"));

                        String username,phone,distance;
                        double heldLat,heldLong;

                        username=userData.getString("username");
                        phone=userData.getString("phone");
                        heldLat= userData.getDouble("latitude");
                        heldLong =  userData.getDouble("longitude");
                        double roundOff = Math.round(userData.getDouble("distance") * 100.0) / 100.0;
                        distance = roundOff+" Kms away.";

                        JSONArray movieData= userData.getJSONArray("movieArray");
                        String movie_details="";
                        int no_movies=0;
                        for(int j=0;j<movieData.length();j++){
                            JSONObject objMovie= movieData.getJSONObject(j);
                       String statusData="";
                        String movie_name=objMovie.getString("movie_name");
                            int status=objMovie.getInt("status");
                            if(status==0){statusData="Pending";}
                            if(status==1){statusData="Accepted";}
                            if(status==2){statusData="Denied";}

                            movie_details+=movie_name+" - "+statusData+".\n";
                    no_movies++;
                        }


                        latitude_longitude = new LatLng(latitude, longitude);

                        mMyMarkersArray.add(new MovieMarkers("1",username,movie_details,phone,distance,""+no_movies,heldLat,heldLong));

                    }
                    Log.d("mMyMarkersArray size : ", "" + mMyMarkersArray.size());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                plotMarkers(mMyMarkersArray);
                currentLocation = new LatLng(latitude, longitude);

                mMap.addMarker(new MarkerOptions().position(currentLocation).title("").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        return false;


                    }
                });
            }
        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", error.getMessage());
            }
        });
        queue.add(request);

        Log.d("end of loading codes : ","ended well");

    }

    private void plotMarkers(ArrayList<MovieMarkers> markers)
    {
        Log.d("end of loading codes : ","plotted");
        if(markers.size() > 0)
        {
            for (MovieMarkers myMarker : markers)
            {

                Log.d("well : ","looped");
                // Create user marker with custom icon and other options
                MarkerOptions markerOption = new MarkerOptions().position(new LatLng(myMarker.getLatitude(), myMarker.getLongitude()));
                markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

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
            View v = getLayoutInflater().inflate(R.layout.map_shared, null);

            MovieMarkers myMarker = mMarkersHashMap.get(marker);

            TextView markerUsername = (TextView) v.findViewById(R.id.username);
            TextView markerMovies = (TextView) v.findViewById(R.id.shared_movies);
            TextView markerDistance = (TextView) v.findViewById(R.id.distance);
            TextView markerNoMovies = (TextView) v.findViewById(R.id.no_movies);
try {
            markerUsername.setText(myMarker.getUsername());
            markerMovies.setText(myMarker.getMovies());
            markerDistance.setText(myMarker.getDistance());
            markerNoMovies.setText(myMarker.getNo_movies() + " Movies.");

    String url = getMapsApiDirectionsUrl(latitude, longitude, myMarker.getLatitude(), myMarker.getLongitude());
    ReadTask downloadTask = new ReadTask();
    downloadTask.execute(url);
} catch (Exception e) {
    Log.d("error on click 2 : ", e.getMessage());
}

            return v;

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
                Log.d("Background Task", e.toString());
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
        Log.d("url", url);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
