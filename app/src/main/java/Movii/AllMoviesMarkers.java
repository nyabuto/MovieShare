package Movii;

/**
 * Created by Geofrey on 6/15/2016.
 */
public class AllMoviesMarkers {
    String id;
    String name;
    int no_of_movies;
    String distance;
    double latitude,longitude;
    String phone;
    int user_type;

    public AllMoviesMarkers(int user_type,String id,String name,int no_of_movies,String distance,String phone,double latitude,double longitude){
        this.id=id;
        this.name=name;
        this.no_of_movies=no_of_movies;
        this.distance=distance;
        this.latitude=latitude;
        this.longitude=longitude;
        this.phone=phone;
        this.user_type=user_type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNo_of_movies() {
        return no_of_movies;
    }

    public void setNo_of_movies(int no_of_movies) {
        this.no_of_movies = no_of_movies;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getUser_type() {
        return user_type;
    }

    public void setUser_type(int user_type) {
        this.user_type = user_type;
    }
}
