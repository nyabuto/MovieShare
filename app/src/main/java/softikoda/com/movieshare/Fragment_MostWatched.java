package softikoda.com.movieshare;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.GridView;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment_MostWatched.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment_MostWatched#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_MostWatched extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

//    ListView listMostWatched;
    GridView gv;
    ArrayList<String> ALMovieName = new ArrayList<String>();
    ArrayList<String> ALMovieGenre = new ArrayList<String>();
    ArrayList<String> ALNoMovies = new ArrayList<String>();
    ArrayList<Integer> ALWatchStatus = new ArrayList<Integer>();
    ArrayList<String> ALMovieId = new ArrayList<String>();
    ArrayList<String> ALImageUrl = new ArrayList<String>();
    ArrayList<HashMap<String,String>> allData = new ArrayList<HashMap<String, String>>();
    String url="http://softikoda.com/movieshare/mostWatched.php?user_id=";
    String user_id="";
    int user_type=0;
    JSONArray allMovies=null;
    ProgressDialog progressDialog;
    SwipeRefreshLayout swipeRefreshLayout;
    public Fragment_MostWatched() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
    public static Fragment_MostWatched newInstance(String param1, String param2) {
        Fragment_MostWatched fragment = new Fragment_MostWatched();
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
View view=inflater.inflate(R.layout.fragment_mostwatched, container, false);

        user_id=getArguments().getString("user_id");
        user_type=getArguments().getInt("user_type");

//        listMostWatched = (ListView) view.findViewById(R.id.list_mostWatched);
        gv=(GridView) view.findViewById(R.id.gridView);


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

        allData.clear();ALMovieId.clear();
        ALMovieName.clear();ALMovieGenre.clear();ALImageUrl.clear();ALNoMovies.clear();ALWatchStatus.clear();

        progressDialog.setMessage("Loading most watched movies ...");
        progressDialog.show();

        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url+""+user_id, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("response is : ",response.toString());
                try {
                    int success=response.getInt("success");
                    if(success>0) {
                    allMovies = response.getJSONArray("most_watched");
                    for (int i = 0; i < allMovies.length(); i++) {
                        JSONObject s = allMovies.getJSONObject(i);
                        HashMap<String,String> myMap = new HashMap<>();

                        String movie_id=s.getString("movie_id");
                        String movie_name = s.getString("movie_name");
                        String movie_genre=s.getString("movie_genre");
                        String image_url ="http://softikoda.com/movieshare/Images/"+ s.getString("image_url");
                        String no_watched=s.getString("no_watched");
                        int watch_status = s.getInt("if_watched");

                        myMap.put("movie_id",movie_id);
                        myMap.put("movie_name",movie_name);
                        myMap.put("movie_genre",movie_genre);
                        myMap.put("image_url",image_url);
                        myMap.put("no_watched", no_watched);
                        myMap.put("watch_status", ""+watch_status);

                        allData.add(myMap);
                        ALMovieName.add(movie_name);
                        ALImageUrl.add(image_url);
                        ALMovieGenre.add(movie_genre);
                        ALNoMovies.add(no_watched);
                        ALWatchStatus.add(watch_status);
                        ALMovieId.add(movie_id);
                    }
                    Log.d("size here in app : ",""+allData.size());
                    if (allData.size() > 0) {

                        Log.d("statistics 2 user type",""+user_type);
                        gv.setAdapter(new CustomAdapter(getActivity(),user_type,ALImageUrl.toArray(new String[ALImageUrl.size()]),ALMovieName.toArray(new String[ALMovieName.size()]),ALMovieGenre.toArray(new String[ALMovieGenre.size()]),ALNoMovies.toArray(new String[ALNoMovies.size()]),ALWatchStatus.toArray(new Integer[ALWatchStatus.size()]),user_id,ALMovieId.toArray(new String[ALMovieId.size()]),"watched"));

                    } else {
                        Toast.makeText(getContext().getApplicationContext(), "No data found on the server", Toast.LENGTH_LONG).show();
                    }
                    }
                    else{
                        Toast.makeText(getContext().getApplicationContext(),"No wishlist item", Toast.LENGTH_SHORT).show();
                    }

                    progressDialog.dismiss();

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
