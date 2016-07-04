package softikoda.com.movieshare;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.util.*;
import java.util.Map;

public class Actions extends AppCompatActivity {
CardView card1,card2,card3,card4,card5,card6;
    ImageView image1,image2,image3,image4,image5,image6,imageTitle;
    String requestUrl="http://softikoda.com/movieshare/requestMovie.php";
    String manageUrl="http://softikoda.com/movieshare/ManageMovies.php";
    String buyUrl="http://softikoda.com/movieshare/buyMovie.php";
int sell_id,movie_id,share_id,action,user_type;
    String user_id="";
    int wishlist,watchlist,request_movie,buy,remove_watchlist,remove_wishlist;
    String image_url,movie_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actions);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        wishlist=watchlist=request_movie=buy=remove_watchlist=remove_wishlist=1;

        Bundle getUser = getIntent().getExtras();
        user_id=getUser.getString("user_id");
        user_type=getUser.getInt("user_type");
        sell_id=getUser.getInt("sell_id");
        movie_id=getUser.getInt("movie_id");
        share_id=getUser.getInt("share_id");

        wishlist=getUser.getInt("wishlist");
        watchlist=getUser.getInt("watchlist");
        request_movie=getUser.getInt("request_movie");
        buy=getUser.getInt("buy");
        remove_watchlist=getUser.getInt("remove_watchlist");
        remove_wishlist=getUser.getInt("remove_wishlist");
        image_url = getUser.getString("image_url");
        movie_name=getUser.getString("movie_name");


        card1 = (CardView) findViewById(R.id.card1);
        card2 = (CardView) findViewById(R.id.card2);
        card3 = (CardView) findViewById(R.id.card3);
        card4 = (CardView) findViewById(R.id.card4);
        card5 = (CardView) findViewById(R.id.card5);
        card6 = (CardView) findViewById(R.id.card6);

        image1=(ImageView) findViewById(R.id.image_wishlist);
        image2=(ImageView) findViewById(R.id.image_watchlist);
        image3=(ImageView) findViewById(R.id.image_request);
        image4=(ImageView) findViewById(R.id.image_buy);
        image5=(ImageView) findViewById(R.id.image_remove_wishlist);
        image6=(ImageView) findViewById(R.id.image_remove_watchlist);
        imageTitle = (ImageView) findViewById(R.id.action_image);
        Picasso.with(this).load(image_url).into(imageTitle);
        setTitle(movie_name);

        if(wishlist==0){card1.setVisibility(CardView.GONE);}
        else{ card1.setVisibility(CardView.VISIBLE);}

        if(watchlist==0){card2.setVisibility(CardView.GONE);}
        else{ card2.setVisibility(CardView.VISIBLE);}

        if(request_movie==0){card3.setVisibility(CardView.GONE);}
        else{ card3.setVisibility(CardView.VISIBLE);}

        if(buy==0){card4.setVisibility(CardView.GONE);}
        else{ card4.setVisibility(CardView.VISIBLE);}

        if(remove_wishlist==0){card5.setVisibility(CardView.GONE);}
        else{ card5.setVisibility(CardView.VISIBLE);}

        if(remove_watchlist==0){card6.setVisibility(CardView.GONE);}
        else{ card6.setVisibility(CardView.VISIBLE);}

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Card 1 clicked",Toast.LENGTH_SHORT).show();
                card1.setVisibility(CardView.GONE);
                manageMovies("wishlist");// add the movie to my wishlist
            }
        });

        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                card2.setVisibility(CardView.GONE);
                manageMovies("watched");// add the movie to my watchlist
            }
        });


        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                card3.setVisibility(CardView.GONE);
                request_movie();// request this movie
            }
        });

       card4.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               card4.setVisibility(CardView.GONE);
               buyMovie();//buy movie from the shop
           }
       });

        card5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                card5.setVisibility(CardView.GONE);
manageMovies("delete_wishlist"); // remove movie from wishlist
            }
        });

        card6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                card6.setVisibility(CardView.GONE);
                manageMovies("delete_watched"); // remove movie from watchlist
            }
        });
    }

    public  void buyMovie(){
        RequestQueue ManageMoviewQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, buyUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String output;
                if(Integer.parseInt(response)>0){
                    output="Movie booked successfully.";

//                   send sms to the user
                    String phone_no=response;
                }
                else{
                    output="Error : Movie already booked.";
                }
                Toast.makeText(getApplicationContext(),output,Toast.LENGTH_SHORT).show();
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
                params.put("user_id", ""+user_id);
                params.put("sell_id", ""+sell_id);
                return params;
            }
        };
        ManageMoviewQueue.add(request);
    }


    public void request_movie(){
        RequestQueue ManageMoviewQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, requestUrl, new Response.Listener<String>() {
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
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<>();
                params.put("movie_id", ""+movie_id);
                params.put("share_id", ""+share_id);
                params.put("user_id", ""+user_id);
                return params;
            }
        };
        ManageMoviewQueue.add(request);
    }

    public  void manageMovies(final String source){
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
            protected java.util.Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", ""+user_id);
                params.put("movie_id", ""+movie_id);
                params.put("action", "" + source);
                return params;
            }
        };
        ManageMoviewQueue.add(request);
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
