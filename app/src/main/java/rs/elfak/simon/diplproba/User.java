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
    int[] frReqRecv;

    private User() {}

    public User(int _id, String fname, String lname, String uname, String imgUrl, int[] frReqRecv)
    {
        this._id = _id;
        this.fname = fname;
        this.lname = lname;
        this.uname = uname;
        this.imgUrl = imgUrl;
        this.frReqRecv = frReqRecv;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int[] getFrReqRecv() {
        return frReqRecv;
    }

    public void setFrReqRecv(int[] frReqSent) {
        this.frReqRecv = frReqSent;
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
