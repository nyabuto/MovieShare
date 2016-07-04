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
 * Created by Geofrey on 5/22/2016.
 */
public class CustomListview_ShareRequest extends ArrayAdapter<String> {
    private final  String[] imageID;
    private final   String[] movieName;
    private final   String[] movieGenre;
    private final Activity context;
    ImageView imageRequest;
    TextView txtmovie_genre,txtmovie_name;

    public CustomListview_ShareRequest(Activity context,String[] imageID,String[] movieName,String[] movieGenre){
      super(context, R.layout.child_request_layout,movieName);
        this.imageID=imageID;
        this.movieGenre = movieGenre;
        this.movieName=movieName;
        this.context=context;

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("position in custom is :", "" + position);
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.child_request_layout,null,true);
        imageRequest = (ImageView) rowView.findViewById(R.id.child_request_image);
        txtmovie_genre = (TextView) rowView.findViewById(R.id.child_request_movieGenre);
        txtmovie_name = (TextView) rowView.findViewById(R.id.child_request_moviename);


//        Set to the view
        Picasso.with(context).load(imageID[position]).resize(100, 100).centerCrop().into(imageRequest);

        txtmovie_genre.setText(""+movieGenre[position]);
        txtmovie_name.setText(""+movieName[position]);


        return rowView;
    }
}
