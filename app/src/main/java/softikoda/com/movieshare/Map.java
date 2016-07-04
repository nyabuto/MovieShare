package softikoda.com.movieshare;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Movii.AllMoviesMarkers;

public class Map extends Fragment implements OnMapReadyCallback,LocationListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private static GoogleMap mMap;
    double latitude,longitude;
    JSONArray allUsers=null;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private HashMap<Marker, AllMoviesMarkers> mMarkersHashMap;
    private ArrayList<AllMoviesMarkers> mMyMarkersArray = new ArrayList<AllMoviesMarkers>();
    String urlShared="http://softikoda.com/movieshare/MoviesShared.php";
    String urlShop="http://softikoda.com/movieshare/MoviesShop.php";
    LatLng currentLocation;
    String user_id;
    String movie_genre;
    ProgressDialog progressDialog;

    Polyline polyline = null;
MapView mapView;

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
ListView listMovies;
    String id="0",User="";
    int user_type=0;

    ArrayList<String> ALMovieName = new ArrayList<String>();
    ArrayList<String> ALMovieCost = new ArrayList<String>();
    ArrayList<String> ALImageUrl = new ArrayList<String>();
    ArrayList<HashMap<String,String>> allData = new ArrayList<HashMap<String, String>>();

    public Map() {
        // Required empty public constructor
    }

    public static Map newInstance(String param1, String param2) {
        Map fragment = new Map();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);

        user_id=getArguments().getString("user_id");
        user_type=getArguments().getInt("user_type");
        movie_genre=getArguments().getString("movie_genre");


        mapView=(MapView) view.findViewById(R.id.mapMovies);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        MapsInitializer.initialize(getActivity().getApplicationContext());
        mMap=mapView.getMap();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading ...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        buildGoogleApiClient();
        if(mGoogleApiClient!= null){
            mGoogleApiClient.connect();
        }
        else
            Toast.makeText(getContext(), "Not connected...", Toast.LENGTH_SHORT).show();

//        SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
//                .findFragmentById(R.id.mapMovies);
//        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        Toast.makeText(getContext(), "Failed to connect...", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnected(Bundle arg0) {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null) {
            latitude=mLastLocation.getLatitude();
            longitude=mLastLocation.getLongitude();
            Log.d("map ready","yes map is ready");
            if(isInternetAvailable()) {
                if (longitude !=0) {
                    Log.d(">>>>>>>>>","Called to load places");
                    CreateMap();
                }
            }
        }
        if(Build.VERSION.SDK_INT == 23) {
            int hasWriteContactsPermission = getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocation = getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
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
        Toast.makeText(getContext(), "Connection suspended...", Toast.LENGTH_SHORT).show();

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
Log.d("map ready","yes map is ready");
        // Add a marker in Sydney and move the camera
        if(isInternetAvailable()) {
            if (longitude > 0) {
                Log.d(">>>>>>>>>","Called to load places");
                CreateMap();
            }
        }
        else{
            Toast.makeText(getActivity().getApplicationContext(), "Error no internet available", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isInternetAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo!=null && activeNetworkInfo.isConnected();
    }

   public void loadSharedMovies(){
       mMap.clear();
       mMarkersHashMap = new HashMap<Marker, AllMoviesMarkers>();
       mMyMarkersArray.clear();

       String finalUrl=urlShared+"?longitude="+longitude+"&latitude="+latitude+"&user_id="+user_id+"&movie_genre="+movie_genre+"&user_type="+user_type;
       Log.d("finalUrl : ",finalUrl);
       RequestQueue queue = Volley.newRequestQueue(getContext());
       JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, finalUrl, null, new Response.Listener<JSONObject>() {

           @Override
           public void onResponse(JSONObject response) {
               Log.d("responseData : ",response.toString());
               try {
                   int success=response.getInt("success");
                   if(success>0) {
                   allUsers = response.getJSONArray("shared_movies");
                   for (int i = 0; i < allUsers.length(); i++) {
                       JSONObject userData = allUsers.getJSONObject(i);

                       String username, phone, distance, user_id;
                       double userLat, userLong;
                       int no_movies = 0;
                       user_id = userData.getString("user_id");
                       username = userData.getString("username");
                       phone = userData.getString("phone");
                       userLat = userData.getDouble("latitude");
                       userLong = userData.getDouble("longitude");
                       no_movies = userData.getInt("count");

                       double roundOff = Math.round(userData.getDouble("distance") * 100.0) / 100.0;
                       distance = roundOff + " Kms away.";

                       mMyMarkersArray.add(new AllMoviesMarkers(2, user_id, username, no_movies, distance, phone, userLat, userLong));

                   }
               }
                   loadShopMovies();

               } catch (JSONException e) {
                   e.printStackTrace();
               }   }
       }
               , new Response.ErrorListener() {
           @Override
           public void onErrorResponse(VolleyError error) {
               Log.e("Error", error.getMessage());
           }
       });
       queue.add(request);


    }

    public void loadShopMovies(){
        String finalUrl=urlShop+"?longitude="+longitude+"&latitude="+latitude+"&user_id="+user_id+"&movie_genre="+movie_genre+"&user_type="+user_type;
        Log.d("finalUrl : ",finalUrl);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, finalUrl, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                Log.d("responseData : ",response.toString());
                try {
                    int success=response.getInt("success");
                    if(success>0) {
                        allUsers = response.getJSONArray("shop_movies");
                        for (int i = 0; i < allUsers.length(); i++) {
                            JSONObject userData = allUsers.getJSONObject(i);

                            String shop_name, phone, distance, shopid;
                            double shopLat, shopLong;
                            int no_movies = 0;
                            shopid = userData.getString("shopid");
                            shop_name = userData.getString("shopname");
                            phone = userData.getString("phone");
                            shopLat = userData.getDouble("latitude");
                            shopLong = userData.getDouble("longitude");
                            no_movies = userData.getInt("count");
                            double roundOff = Math.round(userData.getDouble("distance") * 100.0) / 100.0;
                            distance = roundOff + " Kms away.";

                            mMyMarkersArray.add(new AllMoviesMarkers(1, shopid, shop_name, no_movies, distance, phone, shopLat, shopLong));

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
                Log.e("Error", error.getMessage());
            }
        });
        queue.add(request);

    }
    public void CreateMap(){
   loadSharedMovies();
 }

    private void plotMarkers(ArrayList<AllMoviesMarkers> markers)
    {
        Log.d("end of loading codes : ","plotted");
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
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                       String myUrl="http://softikoda.com/movieshare/mapMovies.php?id="+id+"&userType="+user_type+"&genre_id="+movie_genre;
                        ALMovieName.clear();ALMovieCost.clear();ALImageUrl.clear();allData.clear();

                        final Dialog dialog = new Dialog(getActivity());
                        dialog.setContentView(R.layout.listofmovies);
                        if(user_type==1) {
                            dialog.setTitle("Shop Name : " +User);
                        }
                        else if(user_type==2){
                            dialog.setTitle("User Name : " +User);
                        }
                        else{
                            dialog.setTitle("");
                        }
                        dialog.setCancelable(true);
                        listMovies = (ListView) dialog.findViewById(R.id.listMovies);

                   loadMapMovies(myUrl,dialog);
                    }
                });
            }
        }
    }
   public void loadMapMovies(String url,final Dialog dialog){
       RequestQueue queue = Volley.newRequestQueue(getContext());
       JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

           @Override
           public void onResponse(JSONObject response) {

               try {
                   int success=response.getInt("success");
                   if(success>0) {
                       allUsers = response.getJSONArray("data");
                       for (int i = 0; i < allUsers.length(); i++) {
                           JSONObject userData = allUsers.getJSONObject(i);

                           String table_id, movie_name,cost,image_url;

                           table_id = userData.getString("table_id");
                           movie_name = userData.getString("movie_name");
                           cost = userData.getString("cost");
                           image_url=userData.getString("image_url");

                           Log.d("pop up image url",image_url);

                           HashMap<String,String> dataMap = new HashMap<>();
                           dataMap.put("image_url",image_url);
                           dataMap.put("movie_name",movie_name);
                           dataMap.put("movie_cost",cost);
                           dataMap.put("table_id",table_id);

                           ALMovieName.add(movie_name);
                           ALMovieCost.add(cost);
                           ALImageUrl.add(image_url);
                           allData.add(dataMap);

 }
                       if (allData.size() > 0) {
                           CustomListMapMovies adapter = new CustomListMapMovies(getActivity(),ALImageUrl.toArray(new String[ALImageUrl.size()]),ALMovieName.toArray(new String[ALMovieName.size()]),ALMovieCost.toArray(new String[ALMovieCost.size()]));
                           listMovies.setAdapter(adapter);
                           listMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                               @Override
                               public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                                   Toast.makeText(getActivity().getApplicationContext(),"Clicked at position "+position,Toast.LENGTH_SHORT).show();

//if(user_type==2) {
//    //                                    CODE TO SEND ALL PARAMETERS TO THE ACTIONS CLASS
//    Bundle bundle = new Bundle();
//    bundle.putInt("wishlist", 1);
//    bundle.putInt("watchlist", 0);
//    bundle.putInt("request_movie", 0);
//    bundle.putInt("buy", 0);
//    bundle.putInt("remove_watchlist", 1);
//    bundle.putInt("remove_wishlist", 0);
//
//    bundle.putString("user_id", user_id);
//    bundle.putInt("user_type", user_type);
//    bundle.putInt("sell_id", 0);
//    bundle.putInt("movie_id", Integer.parseInt(selected_movieID));
//    bundle.putInt("share_id", 0);
//    bundle.putString("image_url", image_url);
//    bundle.putString("movie_name", movie_name);
//
//    Intent intent = new Intent(getActivity(), Actions.class);
//    intent.putExtras(bundle);
//    startActivity(intent);
//
////                                    --END OF THE ACTION CALLING CLASS---------
//}
                               }
                           });


                           dialog.show();
                       }
                   }

               } catch (JSONException e) {
                   e.printStackTrace();
               }   }
       }
               , new Response.ErrorListener() {
           @Override
           public void onErrorResponse(VolleyError error) {
               Log.e("Error", error.getMessage());
           }
       });
       queue.add(request);
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
                View v = getLayoutInflater(getArguments()).inflate(R.layout.map_movies, null);

                id = myMarker.getId();
                user_type = myMarker.getUser_type();
                User = myMarker.getName();

                TextView markerUsername = (TextView) v.findViewById(R.id.username);
                TextView markerDistance = (TextView) v.findViewById(R.id.distance);
                TextView markerNoMovies = (TextView) v.findViewById(R.id.no_movies);
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
                    Log.d("error on click 2 : ", e.getMessage());
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



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
