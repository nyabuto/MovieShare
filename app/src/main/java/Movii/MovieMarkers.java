package Movii;

/**
 * Created by Geofrey on 5/23/2016.
 */
public class MovieMarkers {
    String request_id;
    String username;
    String movies;
    String phone;
    String distance;
    String no_movies;
    double latitude,longitude;

    public MovieMarkers(String request_id, String username,String movies,String phone,String distance , String no_movies,double latitude,double longitude){
        this.request_id=request_id;
        this.username=username;
        this.movies=movies;
        this.phone=phone;
        this.distance=distance;
       this.no_movies=no_movies;
        this.latitude=latitude;
        this.longitude=longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMovies() {
        return movies;
    }

    public void setMovies(String movies) {
        this.movies = movies;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getNo_movies() {
        return no_movies;
    }

    public void setNo_movies(String no_movies) {
        this.no_movies = no_movies;
    }
}
