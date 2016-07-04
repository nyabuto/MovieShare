package softikoda.com.movieshare;

/**
 * Created by Geofrey on 6/14/2016.
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by hp1 on 21-01-2015.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created
String user_id;
    String source;
String movie_genre;
    int user_type;
    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm,CharSequence mTitles[], int mNumbOfTabsumb,String user_id,String source,String movie_genre,int user_type) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
        this.user_id=user_id;
        this.source=source;
        this.movie_genre=movie_genre;
        this.user_type=user_type;

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {
        Fragment returned=null;
        Bundle args = new Bundle();
        args.putString("user_id", user_id);
        args.putString("movie_genre",movie_genre);
        args.putInt("user_type",user_type);

if(source.equals("movies")) {
    if (position == 0) // if the position is 0 we are returning the First tab
    {
        Map tab1 = new Map();

        tab1.setArguments(args);
        returned= tab1;
    } else if (position == 1)        // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
    {
        Shared tab2 = new Shared();
        tab2.setArguments(args);
        returned= tab2;

    } else          // As we are having 3 tabs if the position is now 0 it must be 1 so we are returning second tab
    {
        Shop tab3 = new Shop();
        tab3.setArguments(args);
        returned= tab3;

    }
}


//for my list movies++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        else if(source.equals("mylist")){
    if(position==0){
        Fragment_watchList tab1 = new Fragment_watchList();
        tab1.setArguments(args);
        returned= tab1;
    }
    else{
        Fragment_wishlist tab2 = new Fragment_wishlist();
        tab2.setArguments(args);
        returned= tab2;
    }
        }

else if(source.equals("statistics")){
    if(position==0){
        Fragment_MostWatched tab1 = new Fragment_MostWatched();
        tab1.setArguments(args);
        returned= tab1;
    }
    else{
        Fragment_mostWished tab2 = new Fragment_mostWished();
        tab2.setArguments(args);
        returned= tab2;
    }
}


        return returned;
    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }
}
