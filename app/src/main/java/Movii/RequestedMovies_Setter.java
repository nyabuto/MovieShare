package Movii;

/**
 * Created by Geofrey on 6/20/2016.
 */
public class RequestedMovies_Setter {
    int type,id;
    String name,genre,cost,image_url;
    String buyer_id,username,phone;
    public RequestedMovies_Setter(int type,int id,String name,String genre,String cost,String image_url,String buyer_id,String username,String phone){
        this.name=name;
        this.type=type;
        this.id=id;
        this.genre=genre;
        this.cost=cost;
        this.image_url=image_url;
        this.buyer_id=buyer_id;
        this.username=username;
        this.phone=phone;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getBuyer_id() {
        return buyer_id;
    }

    public void setBuyer_id(String buyer_id) {
        this.buyer_id = buyer_id;
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
}
