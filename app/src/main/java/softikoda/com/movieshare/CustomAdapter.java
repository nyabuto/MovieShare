package softikoda.com.movieshare;

/**
 * Created by Geofrey on 6/16/2016.
 */
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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

public class CustomAdapter extends BaseAdapter{
    private final  String[] imageUrl;
    private final  String[] movieId;
    private final   String[] movieName;
    private final   String[] noWatched;
    private final   String[] movieGenre;
    private final Integer[] watchStatus;
    private final Activity context;
    String user_id;
    String source;
    String addToShopURL="http://softikoda.com/movieshare/manageShopMovies.php";
int user_type;
    String image="",movie_name="";
    int wishMovie=0,watchMovie=0,buyMovie=0,requestMovie=0;
int removeWish=0,removeWatch=0;
    private static LayoutInflater inflater=null;
    public CustomAdapter(Activity context, int user_type,String[] imageUrl, String[] movieName,String[] movie_genre, String[] noWatched, Integer[] watchStatus,String user_id,String[] movieId,String source) {
        // TODO Auto-generated constructor stub
      this.context=context;
        this.movieGenre = movie_genre;
        this.imageUrl=imageUrl;
        this.noWatched = noWatched;
        this.watchStatus=watchStatus;
        this.movieName = movieName;
        this.user_id=user_id;
        this.movieId=movieId;
        this.source=source;
this.user_type=user_type;

        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return movieName.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        ImageView imageTitle,imageStatus;
        TextView txtmovieName,txtMovieGenre,txtNoWatched;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.grid_most, null);

        holder.imageTitle = (ImageView) rowView.findViewById(R.id.image_most);
        holder.txtmovieName = (TextView) rowView.findViewById(R.id.movieName_most);
        holder.txtMovieGenre = (TextView) rowView.findViewById(R.id.movieGenre_most);
        holder.txtNoWatched = (TextView) rowView.findViewById(R.id.noPeople_most);
        holder.imageStatus = (ImageView) rowView.findViewById(R.id.ifThere_most);

        //        Set to the view
        Picasso.with(context).load(imageUrl[position]).resize(180, 180).centerCrop().into(holder.imageTitle);

        holder.txtmovieName.setText(""+movieName[position]);
        holder.txtMovieGenre.setText(""+movieGenre[position]);

        if(Integer.parseInt(noWatched[position])==1){
            holder.txtNoWatched.setText(""+noWatched[position]+" Person.");
        }
        else{
            holder.txtNoWatched.setText(""+noWatched[position]+" People.");
        }

        if(user_type==2) {
            if (watchStatus[position] == 0) {
                holder.imageStatus.setImageResource(R.drawable.cross);
                watchMovie = 1;
            } else if (watchStatus[position] == 1) {
                holder.imageStatus.setImageResource(R.drawable.tick);
                watchMovie = 0;
            } else {
//            no algorithm for dispaly
            }
        }
        rowView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
//                Toast.makeText(context, "You Clicked "+movieName[position], Toast.LENGTH_LONG).show();
                Log.d("statistics 3 user type",""+user_type);

    final String selected_movieID = movieId[position];
    String movie_name = movieName[position];
    int watch_status = watchStatus[position];
    if (source.equals("watched")) {
        wishMovie = 1;
        if (watch_status == 0) {
            watchMovie = 1;
            removeWatch = 0;
        } else {
            watchMovie = 0;
            removeWatch = 1;
        }
    } else {
        watchMovie = 1;
        if (watch_status == 0) {
            wishMovie = 1;
            removeWish = 0;
        } else {
            wishMovie = 0;
            removeWish = 1;
        }
    }
    image = imageUrl[position];
    requestMovie = 1;

    if (user_type == 2) {
//                                    CODE TO SEND ALL PARAMETERS TO THE ACTIONS CLASS
        Bundle bundle = new Bundle();
        bundle.putInt("wishlist", wishMovie);
        bundle.putInt("watchlist", watchMovie);
        bundle.putInt("request_movie", 1);
        bundle.putInt("buy", 0);
        bundle.putInt("remove_watchlist", removeWatch);
        bundle.putInt("remove_wishlist", removeWish);

        bundle.putString("user_id", user_id);
        bundle.putInt("user_type", user_type);
        bundle.putInt("sell_id", 0);
        bundle.putInt("movie_id", Integer.parseInt(selected_movieID));
        bundle.putInt("share_id", 0);
        bundle.putString("image_url", image);
        bundle.putString("movie_name", movie_name);

        Intent intent = new Intent(context, Actions.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

else if (user_type==1) {

        final Dialog dialog = new Dialog(context);
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
                    updateMovieStatus(cost,Integer.parseInt(selected_movieID));
                }
                else{
                    Toast.makeText(context,"Enter Movie cost",Toast.LENGTH_SHORT).show();
                }
            }
        });


        dialog.show();
    }

            }

        });

        return rowView;
    }
    public void updateMovieStatus(final String cost,final int movie_id){
        RequestQueue ManageMoviewQueue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, addToShopURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(context.getApplicationContext(),response,Toast.LENGTH_SHORT).show();
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
                params.put("shop_id", user_id);
                params.put("movie_id", ""+movie_id);
                params.put("cost", cost);
                return params;
            }
        };
        ManageMoviewQueue.add(request);
    }

}