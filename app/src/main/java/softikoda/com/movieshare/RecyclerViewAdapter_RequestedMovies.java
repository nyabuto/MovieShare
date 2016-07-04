package softikoda.com.movieshare;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;

import Movii.MyMovies_Setter;
import Movii.RequestedMovies_Setter;

/**
 * Created by Geofrey on 6/20/2016.
 */
public class RecyclerViewAdapter_RequestedMovies extends RecyclerView.Adapter<RecyclerViewAdapter_RequestedMovies.DataObjectHolder> {

    private static String LOG_TAG = "MyRecyclerViewAdapter";
    public ArrayList<RequestedMovies_Setter> mDataset;
    final ArrayList<String> selectedIDs = new ArrayList<>();
    public Context context;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        TextView name,genre,cost,username;
        ImageView image;
        ImageView image_accept,image_deny;

        public DataObjectHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.movie_name);
            genre = (TextView) itemView.findViewById(R.id.movie_genre);
            cost = (TextView) itemView.findViewById(R.id.movie_cost);
            username = (TextView) itemView.findViewById(R.id.username);
            image = (ImageView) itemView.findViewById(R.id.movie_image);
            image_accept = (ImageView) itemView.findViewById(R.id.accept);
            image_deny = (ImageView) itemView.findViewById(R.id.deny);

            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //  myClickListener.onItemClick(getAdapterPosition(), v);


        }
    }

    public RecyclerViewAdapter_RequestedMovies(ArrayList<RequestedMovies_Setter> myDataset, Context context) {
        mDataset = myDataset;
        this.context=context;
        selectedIDs.clear();
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_requested, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, final int position) {
        holder.name.setText(mDataset.get(position).getName());
        holder.genre.setText(mDataset.get(position).getGenre());
        holder.cost.setText(mDataset.get(position).getCost());
        holder.username.setText(mDataset.get(position).getUsername());
        Picasso.with(context).load(mDataset.get(position).getImage_url()).resize(100, 100).centerCrop().into(holder.image);

        final String rec_phone=mDataset.get(position).getPhone();
        final   String name=mDataset.get(position).getUsername();
        final String movie_name=mDataset.get(position).getName();

        holder.image_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//        code to update database and send sms
                accept_request(mDataset.get(position).getType(),mDataset.get(position).getId());
                deleteItem(position);
//                send success sms

                SmsManager smsManager = SmsManager.getDefault();
                String receiverPhone="+254"+rec_phone.substring(1);
                smsManager.sendTextMessage("+254725627847", null, "Hi " + name + ", Your request to buy " + movie_name + "  has been approved.  ", null, null);


                Toast.makeText(context,"Accepted",Toast.LENGTH_SHORT).show();
            }
        });

        holder.image_deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//        code to update database and send sms
                deny_request(mDataset.get(position).getType(),mDataset.get(position).getId());
                deleteItem(position);

                SmsManager smsManager = SmsManager.getDefault();
                String receiverPhone="+254"+rec_phone.substring(1);
                smsManager.sendTextMessage("+254725627847", null, "Hi " + name + ", Your request to buy " + movie_name + " has been declined.  ", null, null);

                Toast.makeText(context,"Denied",Toast.LENGTH_SHORT).show();
            }
        });

    }



    public void accept_request(int type,int id){
        String finalUrl = "http://www.softikoda.com/movieshare/accept.php?id="+id+"&type="+type;

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.GET, finalUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }

        );
        queue.add(request);
    }


    public void deny_request(int type,int id){
        String finalUrl = "http://www.softikoda.com/movieshare/deny.php?id="+id+"&type="+type;

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.GET, finalUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }

        );
        queue.add(request);
    }

    public void addItem(RequestedMovies_Setter dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


}
