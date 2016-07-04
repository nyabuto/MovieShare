package softikoda.com.movieshare;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.SearchView;
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
import com.google.android.gms.maps.OnMapReadyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Movii.ParentUsers;
import Movii.SharedMovies;

public class Shop extends Fragment implements LocationListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,SearchView.OnQueryTextListener, SearchView.OnCloseListener {
    final Context context = getContext();
    double latitude,longitude;
    JSONArray allSharedMovies=null;
    private boolean isGPSEnabled;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
//    private SearchView search;
    public static CustomExpandableList listAdapter;
    private ExpandableListView expandableListView;
    private ArrayList<ParentUsers> parentUsersList = new ArrayList<ParentUsers>();
    ProgressDialog progressDialog;
    String addToShopURL="http://softikoda.com/movieshare/manageShopMovies.php";
    String user_id;
    String movie_genre;
    int user_type=0;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    SwipeRefreshLayout swipeRefreshLayout;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public static Shop newInstance(String param1, String param2) {
        Shop fragment = new Shop();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public Shop() {
        // Required empty public constructor
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
        final View view = inflater.inflate(R.layout.fragment_shop, container, false);
        user_id=getArguments().getString("user_id");
        movie_genre=getArguments().getString("movie_genre");
        user_type=getArguments().getInt("user_type");

        swipeRefreshLayout=(SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh) ;
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(isInternetAvailable()==true) {
                    Initializer(view);

                }
                else{
                    Toast.makeText(getContext().getApplicationContext(),"No internet connections",Toast.LENGTH_SHORT).show();
                }
            }
        });

        Initializer(view);

        return view;
    }

   public void Initializer(View view){
       //get reference to the ExpandableListView
       expandableListView = (ExpandableListView) view.findViewById(R.id.expandableListView_movies_shop);
       //display the list
       progressDialog = new ProgressDialog(getContext());
       progressDialog.setMessage("");
       progressDialog.setCancelable(false);

       progressDialog.setMessage("Loading Shop movies ...");



//        progressDialog.show();
       buildGoogleApiClient();
       if(mGoogleApiClient!= null){
           mGoogleApiClient.connect();
       }
       else
           Toast.makeText(getContext(), "Not connected...", Toast.LENGTH_SHORT).show();

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
            if(isInternetAvailable()) {
                if (longitude !=0) {
                    loadServerData();
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

    //method to expand all groups
    private void expandAll() {
        int count = listAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            expandableListView.expandGroup(i);
        }
    }


    //method to expand all groups
    public void displayList() {
        //create the adapter by passing your ArrayList data
        listAdapter = new CustomExpandableList(getActivity(), parentUsersList);
        //attach the adapter to the list
        expandableListView.setAdapter(listAdapter);
//        expandableListView.setGroupIndicator(getActivity().getApplicationContext().getResources().getDrawable(
//                R.drawable.group_indicator));
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                SharedMovies sharedMovies = (SharedMovies) listAdapter.getChild(groupPosition,childPosition);
                ParentUsers parentUser = (ParentUsers)listAdapter.getGroup(groupPosition);

                Log.d("user_id : ",""+parentUser.getUser_id());

                final int movie_id=sharedMovies.getMovie_id();
                final String share_id=sharedMovies.getShared_id();
                final String movie_name=sharedMovies.getMovie_name();
                final String username=sharedMovies.getUsername();
                final String phone_no=sharedMovies.getPhone();
                final String image_url=sharedMovies.getMovie_url();

                if(user_type==2) {
                    //                                    CODE TO SEND ALL PARAMETERS TO THE ACTIONS CLASS
                    Bundle bundle = new Bundle();
                    bundle.putInt("wishlist", 0);
                    bundle.putInt("watchlist", 0);
                    bundle.putInt("request_movie", 0);
                    bundle.putInt("buy", 1);
                    bundle.putInt("remove_watchlist", 0);
                    bundle.putInt("remove_wishlist", 0);

                    bundle.putString("user_id", user_id);
                    bundle.putInt("user_type", user_type);
                    bundle.putInt("sell_id", Integer.parseInt(share_id));
                    bundle.putInt("movie_id", movie_id);
                    bundle.putInt("share_id", 0);
                    bundle.putString("image_url", image_url);
                    bundle.putString("movie_name", movie_name);

                    Intent intent = new Intent(getActivity(), Actions.class);
                    intent.putExtras(bundle);
                    startActivity(intent);

////                LOAD THE CUSTOM XML HERE
                }
                else if (user_type==1) {

                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.shop_popup);
                dialog.setTitle("Add "+movie_name+" to shop");
                dialog.setCancelable(true);

                    final EditText txtCost= (EditText) dialog.findViewById(R.id.movie_cost);

                Button btnSave = (Button) dialog.findViewById(R.id.save);

                    btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                        String cost=txtCost.getText().toString().trim();
                        if(!cost.equals("")) {
                            updateMovieStatus(cost,movie_id);
                        }
                        else{
                            Toast.makeText(getContext().getApplicationContext(),"Enter Movie cost",Toast.LENGTH_SHORT).show();
                        }
                    }
                });


                dialog.show();
                }
                return false;
            }
        });

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if(listAdapter!=null) {
                 int count = listAdapter.getGroupCount();
                for (int i = 0; i < count; i++) {
                    if(i!=groupPosition) {
                        expandableListView.collapseGroup(i);
                    }
                }
                }

            }
        });
        progressDialog.dismiss();
    }


    public void loadServerData() {
        swipeRefreshLayout.setRefreshing(false);

        String finalUrl="http://www.softikoda.com/movieshare/MoviesShop.php?longitude="+longitude+"&&latitude="+latitude+"&&user_id="+user_id+"&movie_genre="+movie_genre+"&user_type="+user_type;

        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, finalUrl, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    int success=response.getInt("success");
                    if(success>0) {
                        allSharedMovies = response.getJSONArray("shop_movies");

                        for (int i = 0; i < allSharedMovies.length(); i++) {
                            JSONObject s = allSharedMovies.getJSONObject(i);


                            double latitude = s.getDouble("latitude");
                            double longitudes = s.getDouble("longitude");
                            String username = s.getString("shopname");
                            int user_id = s.getInt("shopid");
                            double roundOff = Math.round(s.getDouble("distance") * 100.0) / 100.0;
                            String distance = roundOff + " Kms away.";
                            int no_of_movies = s.getInt("count");
                            String phone = s.getString("phone");

//                        loop for user mshared movies
                            ArrayList<SharedMovies> sharedMoviesList = new ArrayList<SharedMovies>();
                            JSONArray userArray = s.getJSONArray("userArray");

                            for (int j = 0; j < userArray.length(); j++) {
                                JSONObject userObject = userArray.getJSONObject(j);
                                String movie_name, movie_genre, image_url, date_uploaded, uploaded_by, sell_date, sell_id,cost;

                                int movie_id = userObject.getInt("movie_id");
                                movie_name = userObject.getString("movie_name");
                                sell_id = userObject.getString("sell_id");
                                movie_genre = userObject.getString("movie_genre");
                                String image_name = userObject.getString("image_url").replace("\"", "");
                                date_uploaded = userObject.getString("date_uploaded");
                                uploaded_by = userObject.getString("uploaded_by");
                                sell_date = userObject.getString("sell_date");
                                cost="Kshs. "+userObject.getString("cost");
                                image_url = image_name;


                                SharedMovies sharedMovies = new SharedMovies(image_url, movie_genre, movie_name, movie_id, sell_id, username, phone,cost);
                                sharedMoviesList.add(sharedMovies);
                            }
                            ParentUsers parentUsers = new ParentUsers(username, distance, no_of_movies, sharedMoviesList, user_id);
                            parentUsersList.add(parentUsers);
                        }
                    }

                    displayList();
//                    --------------------------------------------------------------------
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", error.getMessage());
            }
        });
        queue.add(request);
    }



    @Override
    public boolean onClose() {
        if(listAdapter!=null) {
            listAdapter.filterData("");
            expandAll();
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
    if(listAdapter!=null) {
        listAdapter.filterData(query);
        expandAll();
    }
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if(listAdapter!=null) {
            listAdapter.filterData(query);
            expandAll();
        }
        return false;
    }



    //    load people with movies
    private boolean isInternetAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo!=null && activeNetworkInfo.isConnected();
    }

    private void enableGPS() {
        if (!isGPSEnabled) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
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

    public void updateMovieStatus(final String cost,final int movie_id){
        RequestQueue ManageMoviewQueue = Volley.newRequestQueue(getContext());
        StringRequest request = new StringRequest(Request.Method.POST, addToShopURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(getContext().getApplicationContext(),response,Toast.LENGTH_SHORT).show();
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
                params.put("shop_id", user_id);
                params.put("movie_id", ""+movie_id);
                params.put("cost", cost);
                return params;
            }
        };
        ManageMoviewQueue.add(request);
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
