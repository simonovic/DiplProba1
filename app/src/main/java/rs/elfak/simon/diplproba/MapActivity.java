package rs.elfak.simon.diplproba;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener,
                                                                GoogleApiClient.ConnectionCallbacks,
                                                                GoogleApiClient.OnConnectionFailedListener,
                                                                LocationListener
{
    GoogleMap gmap;
    String mode;
    LatLng ll;
    Bundle extras;
    FloatingActionButton chatBtn;
    GoogleApiClient gApiCl;
    int userID, gameID;
    static final LocationRequest locRequest = LocationRequest.create()
            .setInterval(3000).setFastestInterval(3000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    SharedPreferences shPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        shPref = getSharedPreferences(Constants.loginPref, Context.MODE_PRIVATE);
        //userID = shPref.getInt(Constants.userIDpref, 0);
        userID = 41;

        extras = getIntent().getExtras();
        mode = extras.getString("mode");

        if (mode.equals("game"))
        {
            gameID = extras.getInt("gameID");
            JSONObject data = new JSONObject();
            try {
                data.put("id", userID);
                data.put("role", "guardian");
                data.put("gameID", gameID);
            } catch (JSONException e) { e.printStackTrace(); }
            LoginActivity.socket.emit("startGame", data);
        }

        SupportMapFragment smapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        smapFrag.getMapAsync(this);
    }

    private void setUpGame()
    {
        gApiCl = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        gApiCl.connect();
        chatBtn = (FloatingActionButton)findViewById(R.id.chatBtn);
        //chatBtn.setVisibility(View.GONE);
        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ChatActivity.class));
            }
        });
        gameID = extras.getInt("gameID");
        double[] pom = extras.getDoubleArray("location");
        ll = new LatLng(pom[0], pom[1]);
        gmap.addMarker(new MarkerOptions().position(ll));
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 15));

        LoginActivity.socket.on("gameOnResponse", onGameOnResponse);
    }

    private void setUpNav()
    {
        double[] pom = extras.getDoubleArray("location");
        ll = new LatLng(pom[0], pom[1]);
        gmap.addMarker(new MarkerOptions().position(ll));
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 15));
    }

    public void setUpNewGame()
    {
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
                    String lat, lng;
                    JSONObject data = (JSONObject) args[0];
                    try {
                        lat = data.getString("lat");
                        lng = data.getString("lng");
                    } catch (JSONException e) { return; }
                    Toast.makeText(getApplicationContext(), lat+", "+lng, Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    @Override
    public void onLocationChanged(Location location) {
        JSONObject data = new JSONObject();
        try {
            data.put("id", userID);
            data.put("lat", location.getLatitude());
            data.put("lng", location.getLongitude());
            data.put("role", "tracker");
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
