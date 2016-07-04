package softikoda.com.movieshare;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import Movii.ParentUsers;
import Movii.SharedMovies;

/**
 * Created by Geofrey on 5/17/2016.
 */
public class CustomExpandableList extends BaseExpandableListAdapter{
    private Context context;
    private ArrayList<ParentUsers> parentUserList;
    private ArrayList<ParentUsers> originalList;
int user_id=0;
int movie_id=0;
    public CustomExpandableList(Context context, ArrayList<ParentUsers> parentUserList) {
        this.context = context;
        this.parentUserList = new ArrayList<ParentUsers>();
        this.parentUserList.addAll(parentUserList);
        this.originalList = new ArrayList<ParentUsers>();
        this.originalList.addAll(parentUserList);
    }

    @Override
    public int getGroupCount() {
        return parentUserList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList<SharedMovies> countryList = parentUserList.get(groupPosition).getSharedMoviesList();
        return countryList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return parentUserList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<SharedMovies> sharedMoviesList = parentUserList.get(groupPosition).getSharedMoviesList();
        return sharedMoviesList.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ParentUsers parentUsers = (ParentUsers) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.parent_items, null);
        }

        TextView username = (TextView) convertView.findViewById(R.id.parent_username);
        TextView no_of_movies = (TextView) convertView.findViewById(R.id.parent_no_movies);
        TextView radius = (TextView) convertView.findViewById(R.id.parent_radius);

        username.setText(parentUsers.getUsername().trim());
        no_of_movies.setText(""+parentUsers.getNo_of_movies()+" Movies ");
        radius.setText(parentUsers.getRadius().trim());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
      SharedMovies sharedMovies = (SharedMovies) getChild(groupPosition,childPosition);
        if(convertView==null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.child_items, null);
        }
        ImageView movie_url = (ImageView) convertView.findViewById(R.id.child_movie_image);
        TextView movie_name = (TextView) convertView.findViewById(R.id.child_movie_name);
        TextView movie_cost = (TextView) convertView.findViewById(R.id.child_movie_cost);
//        set image with picasso
       // movie_url.setImageURI(R.drawable.actionbar_bg);
        movie_name.setText(sharedMovies.getMovie_name().trim());
        movie_cost.setText(sharedMovies.getCost().trim());
        Picasso.with(context).load(sharedMovies.getMovie_url()).resize(250, 200).into(movie_url);

       // convertView.setBackground();
        return convertView;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        super.unregisterDataSetObserver(observer);
    }

    @Override
    public void notifyDataSetInvalidated() {
        super.notifyDataSetInvalidated();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return super.areAllItemsEnabled();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getCombinedChildId(long groupId, long childId) {
        return super.getCombinedChildId(groupId, childId);
    }

    @Override
    public long getCombinedGroupId(long groupId) {
        return super.getCombinedGroupId(groupId);
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }

    @Override
    public int getChildType(int groupPosition, int childPosition) {
        return super.getChildType(groupPosition, childPosition);
    }

    @Override
    public int getChildTypeCount() {
        return super.getChildTypeCount();
    }

    public void filterData(String query){

        query = query.toLowerCase();
        Log.v("MyListAdapter", String.valueOf(parentUserList.size()));
        parentUserList.clear();

        if(query.isEmpty()){
            parentUserList.addAll(originalList);
        }
        else {

            for(ParentUsers parentUsers: originalList){

                ArrayList<SharedMovies> sharedMovieList = parentUsers.getSharedMoviesList();
                ArrayList<SharedMovies> newList = new ArrayList<SharedMovies>();
                for(SharedMovies sharedMovies: sharedMovieList){
                    if(sharedMovies.getMovie_genre().toLowerCase().contains(query) ||
                            sharedMovies.getMovie_name().toLowerCase().contains(query)){
                        newList.add(sharedMovies);
                    }
                }
                if(newList.size() > 0){
                    ParentUsers nContinent = new ParentUsers(parentUsers.getUsername(),parentUsers.getRadius(),parentUsers.getNo_of_movies(),newList,parentUsers.getUser_id());
                    parentUserList.add(nContinent);
                }
            }
        }

        Log.v("MyListAdapter", String.valueOf(parentUserList.size()));
        notifyDataSetChanged();

    }
}
