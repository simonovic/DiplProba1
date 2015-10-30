package rs.elfak.simon.diplproba;

import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;
import java.util.Date;

public class Game
{
    int _id;
    String name;
    String desc;
    String creatorID;
    String creator;
    String datetime;
    String address;
    double lat;
    double lng;
    int[] confirmedUsersID;
    int[] invitedUsersID;
    String dt;

    private Game() {}

    public Game(int _id, String name, String desc, String creatorID, String creator, String date, String address, double lat, double lng, int[] confirmedUsersID, int[] invitedUsersID, String dt)
    {
        this._id = _id;
        this.name = name;
        this.desc = desc;
        this.creatorID = creatorID;
        this.creator = creator;
        this.datetime = date;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.confirmedUsersID = confirmedUsersID;
        this.invitedUsersID = invitedUsersID;
        this.dt = dt;

    }

    public void addConfID(int id)
    {
        int pomL = confirmedUsersID.length;
        int[] pomA = new int[pomL+1];
        pomA = Arrays.copyOf(confirmedUsersID, pomL+1);
        pomA[pomL] = id;
        confirmedUsersID = new int[pomL+1];
        confirmedUsersID = pomA;
    }

    public int get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public int[] getConfirmedUsersID() {
        return confirmedUsersID;
    }

    public int[] getInvitedUsersID() {
        return invitedUsersID;
    }

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }
}
