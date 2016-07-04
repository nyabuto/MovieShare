package softikoda.com.movieshare;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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

public class Fragment_wishlist extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    ListView listWishList;
    String url="http://softikoda.com/movieshare/loadWishList.php?user_id=";
    String manageUrl="http://softikoda.com/movieshare/ManageMovies.php";
    String user_id="";
    int user_type=0;
    JSONArray allMovies=null;
    ArrayList<String> ALMovieName = new ArrayList<String>();
    ArrayList<String> ALDateUploaded = new ArrayList<String>();
    ArrayList<String> ALImageUrl = new ArrayList<String>();
    ArrayList<String> ALMovieGenre = new ArrayList<String>();
    ArrayList<HashMap<String,String>> allData = new ArrayList<HashMap<String, String>>();
    final Context context=getContext();
    TextView BookMovie_SendSMS;
    String phone_no;
    ProgressDialog progressDialog;
    SwipeRefreshLayout swipeRefreshLayout;

    public Fragment_wishlist() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Fragment_wishlist newInstance(String param1, String param2) {
        Fragment_wishlist fragment = new Fragment_wishlist();
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
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_wishlist, container, false);
        user_id=getArguments().getString("user_id");
        user_type=getArguments().getInt("user_type");

        listWishList = (ListView) view.findViewById(R.id.list_wishList);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("");
        progressDialog.setCancelable(false);
        swipeRefreshLayout=(SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh) ;
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(isInternetAvailable()==true) {

loadAllMovies();
                }
                else{
                    Toast.makeText(getContext().getApplicationContext(),"No internet connections",Toast.LENGTH_SHORT).show();
                }
            }
        });

        loadAllMovies();



        return view;
    }


    public int loadAllMovies(){
        allData.clear();
        ALMovieName.clear();ALMovieGenre.clear();ALImageUrl.clear();ALDateUploaded.clear();

        progressDialog.setMessage("Loading wishlist ...");
        progressDialog.show();

        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url+""+user_id, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();
                try {
                    int success=response.getInt("success");
                    if(success>0) {
                        allMovies = response.getJSONArray("wishlist");
                        for (int i = 0; i < allMovies.length(); i++) {
                            JSONObject s = allMovies.getJSONObject(i);
                            HashMap<String, String> myMap = new HashMap<>();

                            String movie_id = s.getString("movie_id");
                            String movie_name = s.getString("movie_name");
                            String image_url = "http://softikoda.com/movieshare/Images/" + s.getString("image_url");
                            String date[] = s.getString("date_uploaded").split(" ");
                            String date_uploaded = "Uploaded : " + date[0];
                            String movie_genre = s.getString("movie_genre");


                            myMap.put("movie_id", movie_id);
                            myMap.put("movie_genre", movie_genre);
                            myMap.put("movie_name", movie_name);
                            myMap.put("image_url", image_url);
                            myMap.put("date_uploaded", date_uploaded);


                            allData.add(myMap);
                            ALMovieName.add(movie_name);
                            ALDateUploaded.add(date_uploaded);
                            ALImageUrl.add(image_url);
                            ALMovieGenre.add(movie_genre);

                        }
                        Log.d("size here in app : ", "" + allData.size());
                        if (allData.size() > 0) {

                            CustomListView_WishList adapter = new CustomListView_WishList(getActivity(), ALImageUrl.toArray(new String[ALImageUrl.size()]), ALMovieName.toArray(new String[ALMovieName.size()]), ALDateUploaded.toArray(new String[ALDateUploaded.size()]), ALMovieGenre.toArray(new String[ALMovieGenre.size()]));
                            listWishList.setAdapter(adapter);
                            listWishList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    HashMap<String, String> clickedData = allData.get(position);
                                    final String selected_movieID = clickedData.get("movie_id");
                                    String movie_name = clickedData.get("movie_name");
                                    String image_url = clickedData.get("image_url");

                                    if(user_type==2) {
//                                    CODE TO SEND ALL PARAMETERS TO THE ACTIONS CLASS
                                        Bundle bundle = new Bundle();
                                        bundle.putInt("wishlist", 0);
                                        bundle.putInt("watchlist", 1);
                                        bundle.putInt("request_movie", 0);
                                        bundle.putInt("buy", 0);
                                        bundle.putInt("remove_watchlist", 0);
                                        bundle.putInt("remove_wishlist", 1);

                                        bundle.putString("user_id", user_id);
                                        bundle.putInt("user_type", user_type);
                                        bundle.putInt("sell_id", 0);
                                        bundle.putInt("movie_id", Integer.parseInt(selected_movieID));
                                        bundle.putInt("share_id", 0);
                                        bundle.putString("image_url", image_url);
                                        bundle.putString("movie_name", movie_name);

                                        Intent intent = new Intent(getActivity(), Actions.class);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }
//                                    --END OF THE ACTION CALLING CLASS---------

//                                    final Dialog dialog = new Dialog(getActivity());
//                                    dialog.setContentView(R.layout.mymovies_popup);
//                                    dialog.setTitle(movie_name);
//                                    dialog.setCancelable(true);
//                                    Button btnWatched = (Button) dialog.findViewById(R.id.mymovies_popup_watched);
//                                    Button btnWished = (Button) dialog.findViewById(R.id.mymovies_popup_wished);
//                                    Button btnRemove = (Button) dialog.findViewById(R.id.mymovies_popup_remove);
//                                    Button btnShare = (Button) dialog.findViewById(R.id.mymovies_popup_share);
//
//                                    ((ViewManager) btnWished.getParent()).removeView(btnWished);
//                                    ((ViewManager) btnShare.getParent()).removeView(btnShare);
//
//                                    btnWatched.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            dialog.cancel();
//
//                                            updateMovieStatus(selected_movieID, "watched");
//                                        }
//                                    });
//
//                                    btnRemove.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            dialog.cancel();
//
//                                            updateMovieStatus(selected_movieID, "delete_wishlist");
//                                        }
//                                    });
//
//                                    dialog.show();
                                }
                            });


                        } else {
                            Toast.makeText(getContext().getApplicationContext(), "No data found on the server", Toast.LENGTH_LONG).show();
                        }


                    }
                    else{
                        Toast.makeText(getContext().getApplicationContext(),"No wishlist item", Toast.LENGTH_SHORT).show();
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
        swipeRefreshLayout.setRefreshing(false);
        return allData.size();
    }

    public void updateMovieStatus(final String selectedMovie, final String source){
        RequestQueue ManageMoviewQueue = Volley.newRequestQueue(getContext());
        StringRequest request = new StringRequest(Request.Method.POST, manageUrl, new Response.Listener<String>() {
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
                params.put("user_id", user_id);
                params.put("movie_id", selectedMovie);
                params.put("action", "" + source);
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

    private boolean isInternetAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo!=null && activeNetworkInfo.isConnected();
    }

}
