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
 * Created by Geofrey on 5/4/2016.
 */
public class CustomListView_WishList extends  ArrayAdapter<String> {

    private final  String[] imageID;
    private final   String[] title;
    private final   String[] uploadDate;
    private final   String[] movieGenre;
    private final Activity context;

    ImageView wishList_imageTitle;
    TextView wishList_txtMovieTitle,wishList_dateUploaded,wishList_movieGenre;

    public CustomListView_WishList(Activity context, String[] imageID, String[] title, String[] date_uploaded, String[] movie_genre){
        super(context, R.layout.custom_wish_list_items,title);
        this.imageID=imageID;
        this.title=title;
        this.uploadDate=date_uploaded;
        this.movieGenre=movie_genre;
        this.context=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("position in custom is :", "" + position);
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.custom_wish_list_items,null,true);
        wishList_imageTitle = (ImageView) rowView.findViewById(R.id.wishList_titleImage);
        wishList_txtMovieTitle = (TextView) rowView.findViewById(R.id.wishList_txtMovieTitle);
        wishList_dateUploaded = (TextView) rowView.findViewById(R.id.wishList_dateUploaded);
        wishList_movieGenre = (TextView) rowView.findViewById(R.id.wishList_txtMovieGenre);

//        Set to the view
        Picasso.with(context).load(imageID[position]).resize(100, 100).centerCrop().into(wishList_imageTitle);
        wishList_txtMovieTitle.setText(""+title[position]);
        wishList_dateUploaded.setText(""+uploadDate[position]);
        wishList_movieGenre.setText(""+movieGenre[position]+"");

        return rowView;
    }
}

