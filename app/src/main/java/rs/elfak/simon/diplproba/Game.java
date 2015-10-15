package rs.elfak.simon.diplproba;

import com.google.android.gms.maps.model.LatLng;

public class Game
{
    int _id;
    String name;
    String description;
    String creator;
    String date;
    String address;  //adresa
    LatLng latlng;
    int[] confirmedUsersID;
    int[] invitedUsersID;

    private Game() {}

    public Game(int _id, String name, String description, String creator, String date, String address, LatLng latlng, int[] confirmedUsersID, int[] invitedUsersID)
    {
        this._id = _id;
        this.name = name;
        this.description = description;
        this.creator = creator;
        this.date = date;
        this.address = address;
        this.latlng = latlng;
        this.confirmedUsersID = confirmedUsersID;
        this.invitedUsersID = invitedUsersID;
    }

    public int get_id() { return _id; }

    public void set_id(int _id) { this._id = _id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getCreator() { return creator; }

    public void setCreator(String creator) { this.creator = creator; }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public int[] getConfirmedUsersID() { return confirmedUsersID; }

    public void setConfirmedUsersID(int[] confirmedUsersID) { this.confirmedUsersID = confirmedUsersID; }

    public int[] getInvitedUsersID() { return invitedUsersID; }

    public void setInvitedUsersID(int[] invitedUsersID) { this.invitedUsersID = invitedUsersID; }
}
