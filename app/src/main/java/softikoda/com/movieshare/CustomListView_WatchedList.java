package softikoda.com.movieshare;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

/**
 * Created by Geofrey on 5/3/2016.
 */
public class CustomListView_WatchedList extends ArrayAdapter<String> {
    private final  String[] imageID;
    private final   String[] title;
    private final   String[] uploadDate;
    private final   String[] movieGenre;
    private final String[] dateWatched;
    private final Activity context;

    ImageView watched_imageTitle;
    TextView watched_txtMovieTitle,watched_dateUploaded,txtwatchedGenre,txtdateWatched;

    public CustomListView_WatchedList(Activity context, String[] imageID, String[] title,String[] movie_genre, String[] uploadDate, String[] dateWatched){
        super(context, R.layout.custom_watched_list_items,title);
        this.imageID=imageID;
        this.title=title;
        this.uploadDate=uploadDate;
        this.movieGenre=movie_genre;
        this.context=context;
        this.dateWatched = dateWatched;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("position in custom is :", "" + position);
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.custom_watched_list_items,null,true);
        watched_imageTitle = (ImageView) rowView.findViewById(R.id.watched_titleImage);
        watched_txtMovieTitle = (TextView) rowView.findViewById(R.id.watched_txtMovieTitle);
        watched_dateUploaded = (TextView) rowView.findViewById(R.id.watched_dateUploaded);
        txtwatchedGenre = (TextView) rowView.findViewById(R.id.watched_genre);
        txtdateWatched = (TextView) rowView.findViewById(R.id.watched_dateWatched);

//        Set to the view
        Picasso.with(context).load(imageID[position]).resize(100, 100).centerCrop().into(watched_imageTitle);

        watched_txtMovieTitle.setText(""+title[position]);
        watched_dateUploaded.setText(""+uploadDate[position]);
        txtdateWatched.setText(""+dateWatched[position]+".");
        txtwatchedGenre.setText(""+movieGenre[position]+".");


        return rowView;
    }
}
