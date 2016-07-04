package softikoda.com.movieshare;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import Movii.AllMoviesMarkers;
import Movii.MyMovies_Setter;

public class MyMovies extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";
    private  final Context context=this;
    SwipeRefreshLayout swipeRefreshLayout;
String user_id;
    String url="";
    JSONArray arrayMovies=null;
    ArrayList results;
    int index=0,type=0;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_movies);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading my movies ...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Bundle getUser = getIntent().getExtras();
        user_id=getUser.getString("user_id");
        type=getUser.getInt("user_type");

//        user_id="1";
//        type=2;


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("user_id",""+user_id);
                bundle.putInt("user_type",type);
                Intent intent = new Intent(MyMovies.this,AddShareMovie.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        swipeRefreshLayout=(SwipeRefreshLayout) findViewById(R.id.swipeRefresh) ;
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(isInternetAvailable()==true) {
//                    displayData();
                    index=0;
                    getServerDataSet();
                }
                else{
                    Toast.makeText(getApplicationContext(),"No internet connections",Toast.LENGTH_SHORT).show();
                }
            }
        });
        if(isInternetAvailable()==true) {
//            displayData();
            getServerDataSet();
        }
        else{
            Toast.makeText(getApplicationContext(),"No internet connections",Toast.LENGTH_SHORT).show();
        }

    }


    public void displayData(){
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new RecyclerViewAdapter_MyMovies(getDataSet(),context);
        mRecyclerView.setAdapter(mAdapter);

        progressDialog.dismiss();
        swipeRefreshLayout.setRefreshing(false);
    }

    private ArrayList<MyMovies_Setter> getDataSet(){

      return results;
    }


    private void getServerDataSet() {
//Load data from the server here

        url="http://softikoda.com/movieshare/myMovies.php?id="+user_id+"&type="+type;
        Log.d("mymovieurl : ",url);
 results = new ArrayList<MyMovies_Setter>();

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("responseData : ",response.toString());
                try {
                    int success=response.getInt("success");
                    if(success>0) {
                        arrayMovies = response.getJSONArray("movies");
                        for (int i = 0; i < arrayMovies.length(); i++) {
                            JSONObject userData = arrayMovies.getJSONObject(i);

                            String movie_name, genre_name,imageUrl,cost;
                            int id = 0;

                            id = userData.getInt("id");
                            movie_name = userData.getString("movie_name");
                            genre_name = userData.getString("movie_genre");
                            imageUrl = userData.getString("image_url");
                            cost =  userData.getString("cost");
                            if(type==1){
                             cost+="/=";
                            }

                            MyMovies_Setter obj = new MyMovies_Setter(type,id,movie_name,genre_name,cost,imageUrl);
                            results.add(index, obj);
index++;
                            Log.d("movienameis",movie_name);
                        }
                    }

                    displayData();

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

    private boolean isInternetAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo!=null && activeNetworkInfo.isConnected();
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
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
