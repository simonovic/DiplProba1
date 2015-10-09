package rs.elfak.simon.diplproba;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class User
{
    int _id;
    String fname;
    String lname;
    String uname;
    String imgUrl;

    private User() {}

    public User(int _id, String fname, String lname, String uname, String imgUrl)
    {
        this._id = _id;
        this.fname = fname;
        this.lname = lname;
        this.uname = uname;
        this.imgUrl = imgUrl;
    }

    @Override
    public String toString() {
        String s = null;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();
        Gson gson = gsonBuilder.create();
        s = gson.toJson(this);
        return s;
    }

    public int getId() {
        return _id;
    }

    public void setId(int _id) {
        this._id = _id;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
