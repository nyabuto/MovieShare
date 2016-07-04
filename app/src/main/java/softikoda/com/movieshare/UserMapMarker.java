package softikoda.com.movieshare;

/**
 * Created by Geofrey on 5/12/2016.
 */
public class UserMapMarker {

    private String mTitle;
    private String mUsername;
    private Double mLatitude;
    private Double mLongitude;
    private int mDistance;
    private String mNoOfMovies;;
    private String mUserId;
    private String mPhone;
    public UserMapMarker(String no_of_movies, String user_id, String username, String phone, Double latitude, Double longitude, int distance)
    {
        this.mUsername = username;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mNoOfMovies = no_of_movies;
        this.mDistance = distance;
        this.mUserId = user_id;
        this.mPhone = phone;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public Double getmLatitude() {
        return mLatitude;
    }

    public void setmLatitude(Double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public Double getmLongitude() {
        return mLongitude;
    }

    public void setmLongitude(Double mLongitude) {
        this.mLongitude = mLongitude;
    }

    public int getmDistance() {
        return mDistance;
    }

    public void setmDistance(int mDistance) {
        this.mDistance = mDistance;
    }

    public String getmUsername() {
        return mUsername;
    }

    public void setmUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public String getmNoOfMovies() {
        return mNoOfMovies;
    }

    public void setmNoOfMovies(String mNoOfMovies) {
        this.mNoOfMovies = mNoOfMovies;
    }

    public String getmUserId() {
        return mUserId;
    }

    public void setmUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    public String getmPhone() {
        return mPhone;
    }

    public void setmPhone(String mPhone) {
        this.mPhone = mPhone;
    }
}
