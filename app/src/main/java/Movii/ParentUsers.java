package Movii;

import java.util.ArrayList;

/**
 * Created by Geofrey on 5/17/2016.
 */
public class ParentUsers {
    private String username;
    private String radius;
    private int no_of_movies;
private int user_id;
    private ArrayList<SharedMovies> sharedMoviesList = new ArrayList<SharedMovies>();

    public ParentUsers(String username,String radius, int no_of_movies, ArrayList<SharedMovies> sharedMoviesList,int user_id){
        this.username=username;
        this.radius=radius;
        this.no_of_movies=no_of_movies;
        this.sharedMoviesList=sharedMoviesList;
        this.user_id=user_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    public int getNo_of_movies() {
        return no_of_movies;
    }

    public void setNo_of_movies(int no_of_movies) {
        this.no_of_movies = no_of_movies;
    }

    public ArrayList<SharedMovies> getSharedMoviesList() {
        return sharedMoviesList;
    }

    public void setSharedMoviesList(ArrayList<SharedMovies> sharedMovies) {
        this.sharedMoviesList = sharedMovies;
    }
}
