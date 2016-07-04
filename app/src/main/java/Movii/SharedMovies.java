package Movii;

/**
 * Created by Geofrey on 5/17/2016.
 */
public class SharedMovies {
    private String movie_url;
   private String movie_name;
    private String movie_genre;
    private int movie_id;
    private String shared_id;
    private String username;
    private String phone;
    String cost;

    public SharedMovies(String movie_url, String movie_genre,String movie_name,int movie_id,String shared_id,String username,String phone,String cost){
        this.movie_url=movie_url;
        this.movie_genre=movie_genre;
        this.movie_name=movie_name;
        this.movie_id=movie_id;
        this.shared_id=shared_id;
        this.phone=phone;
        this.username=username;
        this.cost=cost;

    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getShared_id() {
        return shared_id;
    }

    public void setShared_id(String shared_id) {
        this.shared_id = shared_id;
    }

    public int getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(int movie_id) {
        this.movie_id = movie_id;
    }

    public String getMovie_url() {
        return movie_url;
    }

    public void setMovie_url(String movie_url) {
        this.movie_url = movie_url;
    }

    public String getMovie_name() {
        return movie_name;
    }

    public void setMovie_name(String movie_name) {
        this.movie_name = movie_name;
    }

    public String getMovie_genre() {
        return movie_genre;
    }

    public void setMovie_genre(String movie_genre) {
        this.movie_genre = movie_genre;
    }
}
