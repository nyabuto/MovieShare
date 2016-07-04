package softikoda.com.movieshare;

/**
 * Created by Geofrey on 5/19/2016.
 */
public class DataObjects {
    private String id,name,phone_no,imageUrl;

    public DataObjects(String id,String name,String phone_no,String imageUrl){
        this.id=id;
        this.name=name;
        this.phone_no=phone_no;
        this.imageUrl=imageUrl;
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

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
