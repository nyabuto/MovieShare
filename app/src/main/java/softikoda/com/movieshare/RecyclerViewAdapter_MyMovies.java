package softikoda.com.movieshare;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
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

public class RecyclerViewAdapter_MyMovies extends RecyclerView.Adapter<RecyclerViewAdapter_MyMovies.DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    public ArrayList<MyMovies_Setter> mDataset;
    final ArrayList<String> selectedIDs = new ArrayList<>();
    public Context context;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View .OnClickListener {
        TextView name,genre,cost;
        ImageView image;
        ImageView image_delete;

        public DataObjectHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.movie_name);
            genre = (TextView) itemView.findViewById(R.id.movie_genre);
            cost = (TextView) itemView.findViewById(R.id.movie_cost);
            image = (ImageView) itemView.findViewById(R.id.movie_image);
            image_delete = (ImageView) itemView.findViewById(R.id.remove);


            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //  myClickListener.onItemClick(getAdapterPosition(), v);


        }
    }

    public RecyclerViewAdapter_MyMovies(ArrayList<MyMovies_Setter> myDataset, Context context) {
        mDataset = myDataset;
        this.context=context;
        selectedIDs.clear();
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_mymovies, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, final int position) {
        holder.name.setText(mDataset.get(position).getName());
        holder.genre.setText(mDataset.get(position).getGenre());
        holder.cost.setText(mDataset.get(position).getCost());

        Picasso.with(context).load(mDataset.get(position).getImage_url()).resize(100, 100).centerCrop().into(holder.image);
holder.image_delete.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
//        action to delete here
        int type=mDataset.get(position).getType();
        int id=mDataset.get(position).getId();
        deleteItem(position);
        deleteFromServer(type,id);
    }
});

}

    public void deleteFromServer(int type,int id){
        String finalUrl = "http://www.softikoda.com/movieshare/removeMovie.php?id="+id+"&type="+type;

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.GET, finalUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(context, "Movie Removed successfully.", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }

        );
        queue.add(request);
    }
    public void addItem(MyMovies_Setter dataObj, int index) {
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