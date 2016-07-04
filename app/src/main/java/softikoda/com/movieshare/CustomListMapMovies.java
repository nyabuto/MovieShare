package softikoda.com.movieshare;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Geofrey on 6/15/2016.
 */
public class CustomListMapMovies extends ArrayAdapter<String> {
    private final  String[] image_url;
    private final   String[] movie_name;
    private final   String[] movie_cost;
    private final Activity context;
    TextView txtMovie_Name,txtMovie_Cost;
    ImageView imgView;

    public CustomListMapMovies(Activity context,String[] image_url,String[] movie_name, String[] movie_cost){
        super(context, R.layout.child_map_list,movie_name);
        this.image_url=image_url;
        this.movie_name=movie_name;
        this.movie_cost=movie_cost;
        this.context=context;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("position in custom is :", "" + position);
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.child_map_list,null,true);
        imgView = (ImageView) rowView.findViewById(R.id.image_title);
        txtMovie_Name = (TextView) rowView.findViewById(R.id.movie_name);
        txtMovie_Cost = (TextView) rowView.findViewById(R.id.movie_cost);


//        Set to the view
        Picasso.with(context).load(image_url[position]).resize(100, 100).centerCrop().into(imgView);
        txtMovie_Name.setText(""+movie_name[position]);
        txtMovie_Cost.setText(""+movie_cost[position]);

        return rowView;
    }
}
