package rs.elfak.simon.diplproba;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;
import com.github.nkzawa.emitter.Emitter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener,
                                                                GoogleApiClient.ConnectionCallbacks,
                                                                GoogleApiClient.OnConnectionFailedListener,
                                                                LocationListener
{
    GoogleMap gmap;
    HashMap<String, Marker> markers;
    public static List<String> unameL, timeL, messageL;
    String mode, role, uname;
    LatLng ll;
    Bundle extras;
    FloatingActionButton chatBtn;
    GoogleApiClient gApiCl;
    int userID, gameID;
    static final LocationRequest locRequest = LocationRequest.create()
            .setInterval(3000).setFastestInterval(3000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    SharedPreferences shPref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        shPref = getSharedPreferences(Constants.loginPref, Context.MODE_PRIVATE);
        userID = shPref.getInt(Constants.userIDpref, 0);
        gameID = shPref.getInt(Constants.gameIDpref, 0);
        uname = shPref.getString(Constants.userNamepref, "false");
        // gde se bude dodeljivala uloga ubaci ovo
        role = "tracker";
        editor = shPref.edit();
        editor.putString(Constants.rolePref, role);
        editor.commit();

        chatBtn = (FloatingActionButton)findViewById(R.id.chatBtn);

        extras = getIntent().getExtras();
        mode = extras.getString("mode");

        if (mode.equals("game"))
        {
            gameID = extras.getInt("gameID");
            JSONObject data = new JSONObject();
            try {
                data.put("id", userID);
                data.put("role", role);
                data.put("gameID", gameID);
            } catch (JSONException e) { e.printStackTrace(); }
            LoginActivity.socket.emit("startGame", data);
        }

        SupportMapFragment smapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        smapFrag.getMapAsync(this);

        /*Snackbar.make(findViewById(R.id.linMap), "Snackbar", Snackbar.LENGTH_INDEFINITE)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .show();*/
    }

    private void setUpGame()
    {
        gApiCl = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        gApiCl.connect();
        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ChatActivity.class));
            }
        });
        gameID = extras.getInt("gameID");
        double[] pom = extras.getDoubleArray("location");
        ll = new LatLng(pom[0], pom[1]);
        //gmap.addMarker(new MarkerOptions().position(ll));
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 15));
        unameL = new ArrayList<String>();
        timeL = new ArrayList<String>();
        messageL = new ArrayList<String>();
        markers = new HashMap<>();
        LoginActivity.socket.on("gameOnResponse", onGameOnResponse);
    }

    private void setUpNav()
    {
        chatBtn.setVisibility(View.GONE);
        double[] pom = extras.getDoubleArray("location");
        ll = new LatLng(pom[0], pom[1]);
        gmap.addMarker(new MarkerOptions().position(ll));
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 15));
    }

    public void setUpNewGame()
    {
        chatBtn.setVisibility(View.GONE);
        ll = new LatLng(43.317758, 21.900435);
        //gmap.addMarker(new MarkerOptions().position(ll));
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 15));
        gmap.setOnMapLongClickListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setMyLocationEnabled(true);
        gmap.getUiSettings().setMapToolbarEnabled(false);

        if (mode.equals("nav"))
            setUpNav();
        else if (mode.equals("newGame"))
            setUpNewGame();
        else if (mode.equals("game"))
            setUpGame();
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Intent retInt = new Intent();
        retInt.putExtra("latlng", latLng);
        setResult(Activity.RESULT_OK, retInt);
        finish();
    }

    private Emitter.Listener onGameOnResponse = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String response;
                    JSONObject data = (JSONObject) args[0];
                    try {
                        response = data.getString("response");
                    } catch (JSONException e) {
                        return;
                    }
                    if (response.equals("location")) {
                        String uname, lat, lng, role1;
                        try {
                            uname = data.getString("uname");
                            lat = data.getString("lat");
                            lng = data.getString("lng");
                            role1 = data.getString("role");
                        } catch (JSONException e) {
                            return;
                        }
                        if (markers.containsKey(uname)) {
                            markers.get(uname).setPosition(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)));
                        } else {
                            LatLng pom = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                            int img;
                            boolean put = false;
                            if (role1.equals("tracker"))
                                img = R.mipmap.ic_tracker;
                            else if (role1.equals("guardian"))
                                img = R.mipmap.ic_guardian;
                            else
                                img = R.mipmap.ic_coordinator;
                            if (role.equals("tracker") || role.equals("coordinator"))
                                put = true;
                            else if (role.equals("guardian") && !role1.equals("tracker"))
                                put = true;
                            if (put) {
                                Marker marker = gmap.addMarker(new MarkerOptions().position(pom).title(uname).icon(BitmapDescriptorFactory.fromResource(img)));
                                markers.put(uname, marker);
                            }
                        }
                    } else if (response.equals("chat")) {
                        String uname, time, mess;
                        try {
                            uname = data.getString("uname");
                            time = data.getString("time");
                            mess = data.getString("message");
                        } catch (JSONException e) {
                            return;
                        }
                        Toast.makeText(getApplicationContext(), uname + ", " + time + ", " + mess, Toast.LENGTH_LONG).show();
                        unameL.add(uname);
                        timeL.add(time);
                        messageL.add(mess);
                    }
                }
            });
        }
    };

    @Override
    public void onLocationChanged(Location location) {
        JSONObject data = new JSONObject();
        try {
            data.put("id", userID);
            data.put("uname", uname);
            data.put("gameID", gameID);
            data.put("lat", location.getLatitude());
            data.put("lng", location.getLongitude());
            data.put("role", role);
            data.put("mode", "location");
        } catch (JSONException e) { e.printStackTrace(); }
        LoginActivity.socket.emit("gameOn", data);
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationServices.FusedLocationApi.requestLocationUpdates(gApiCl, locRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*if (mode.equals("game")) {
            LocationServices.FusedLocationApi.removeLocationUpdates(gApiCl, this);
            gApiCl.disconnect();
        }*/
    }
}
