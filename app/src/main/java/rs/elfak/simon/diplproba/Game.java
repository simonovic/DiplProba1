package rs.elfak.simon.diplproba;

import com.google.android.gms.maps.model.LatLng;

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

    private Game() {}

    public Game(int _id, String name, String desc, String creatorID, String creator, String date, String address, double lat, double lng, int[] confirmedUsersID, int[] invitedUsersID)
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
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
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

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
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

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int[] getConfirmedUsersID() {
        return confirmedUsersID;
    }

    public void setConfirmedUsersID(int[] confirmedUsersID) {
        this.confirmedUsersID = confirmedUsersID;
    }

    public int[] getInvitedUsersID() {
        return invitedUsersID;
    }

    public void setInvitedUsersID(int[] invitedUsersID) {
        this.invitedUsersID = invitedUsersID;
    }
}
