package rs.elfak.simon.diplproba;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
    String mode, role, uName;
    LatLng ll;
    Bundle extras;
    FloatingActionButton chatBtn;
    GoogleApiClient gApiCl;
    int userID, gameID, radius;
    Location gmLoc;
    static final LocationRequest locRequest = LocationRequest.create()
            .setInterval(3000).setFastestInterval(3000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    SharedPreferences shPref;
    SharedPreferences.Editor editor;
    InputStream is = null;
    String json = "";
    ProgressDialog progD;
    boolean gameON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        shPref = getSharedPreferences(Constants.loginPref, Context.MODE_PRIVATE);
        userID = shPref.getInt(Constants.userIDpref, 0);
        gameID = shPref.getInt(Constants.gameIDpref, 0);
        uName = shPref.getString(Constants.userNamepref, "false");
        gameON = false;
        chatBtn = (FloatingActionButton)findViewById(R.id.chatBtn);
        progD = new ProgressDialog(this);
        progD.setCanceledOnTouchOutside(false);

        // gde se bude dodeljivala uloga ubaci ovo
        role = "coordinator";
        editor = shPref.edit();
        editor.putString(Constants.rolePref, role);
        editor.commit();

        extras = getIntent().getExtras();
        mode = extras.getString("mode");

        LoginActivity.socket.on("gameOnResponse", onGameOnResponse);
        if (mode.equals("game"))
        {
            //gameID = extras.getInt("gameID");
            if (extras.getString("creator").equals("yes"))
                waitForCheckIn();
            progD.setMessage("Igra uskoro počinje...");
            progD.show();
        }
        else if (mode.equals("nav"))
        {
            progD.setMessage("Navigacija se učitava...");
            progD.show();
        }

        SupportMapFragment smapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        smapFrag.getMapAsync(this);
    }

    private void setUpApiClient()
    {
        gApiCl = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        gApiCl.connect();
    }

    private void setUpGame()
    {
        setUpApiClient();
        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ChatActivity.class));
            }
        });
        gameID = extras.getInt("gameID");
        double[] pom = extras.getDoubleArray("location");
        ll = new LatLng(pom[0], pom[1]);
        pom = extras.getDoubleArray("glocation");
        gmLoc = new Location("gmrLoc");
        gmLoc.setLatitude(pom[0]);
        gmLoc.setLongitude(pom[1]);
        gmap.addCircle(new CircleOptions().center(ll).radius(radius).strokeWidth(5).strokeColor(Color.BLUE)/*.fillColor(R.color.area)*/);
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 15));
        unameL = new ArrayList<String>();
        timeL = new ArrayList<String>();
        messageL = new ArrayList<String>();
        markers = new HashMap<>();
    }

    private void setUpNav()
    {
        chatBtn.setVisibility(View.GONE);
        double[] pom = extras.getDoubleArray("location");
        ll = new LatLng(pom[0], pom[1]);
        gmap.addMarker(new MarkerOptions().position(ll));
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 15));
        setUpApiClient();
        //gApiCl.connect();
    }

    public void setUpNewGame()
    {
        chatBtn.setVisibility(View.GONE);
        ll = new LatLng(43.317758, 21.900435);
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 15));
        gmap.setOnMapLongClickListener(this);
    }

    private void waitForCheckIn()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {e.printStackTrace();}
                JSONObject data = new JSONObject();
                try {
                    data.put("mode", "stopJoin");
                    data.put("gameID", gameID);
                } catch (JSONException e) { e.printStackTrace(); }
                LoginActivity.socket.emit("startGame", data);
            }
        }).start();
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
        //else if (mode.equals("game"))
            //setUpGame();
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
                        String uname, lat, lng, role1, frID;
                        try {
                            uname = data.getString("uname");
                            lat = data.getString("lat");
                            lng = data.getString("lng");
                            role1 = data.getString("role");
                            frID = data.getString("id");
                        } catch (JSONException e) {  return; }

                        double lt = Double.parseDouble(lat);
                        double lg = Double.parseDouble(lng);
                        if (markers.containsKey(uname))
                            markers.get(uname).setPosition(new LatLng(lt, lg));
                        else {
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

                        Location myLoc = LocationServices.FusedLocationApi.getLastLocation(gApiCl);
                        /*Location myLoc = new Location("blabla");
                        myLoc.setLatitude(43.31926517);
                        myLoc.setLongitude(21.89886868);*/
                        Location frLoc = new Location("frLoc");
                        frLoc.setLatitude(lt);
                        frLoc.setLongitude(lg);
                        String event = "";
                        boolean ctch = false;
                        float distance = myLoc.distanceTo(frLoc);
                        if (role.equals("tracker")) {
                            if (role1.equals("coordinator") && distance < 5) {
                                ctch = true;
                                event = "TC";
                            }
                        } else if (role.equals("guardian")) {
                            if (role1.equals("tracker") && distance < 5) {
                                ctch = true;
                                event = "GT";
                            }
                        } else {
                            if (role1.equals("guardian") && distance < 5) {
                                ctch = true;
                                event = "CG";
                            }
                        }
                        if (ctch) {
                            JSONObject data1 = new JSONObject();
                            try {
                                data1.put("uname", uname);
                                data1.put("frID", frID);
                                data1.put("gameID", gameID);
                                data1.put("mode", "catch");
                                data1.put("event", event);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            LoginActivity.socket.emit("gameOn", data1);
                        }
                    } else if (response.equals("chat")) {
                        String uname, time, mess;
                        try {
                            uname = data.getString("uname");
                            time = data.getString("time");
                            mess = data.getString("message");
                        } catch (JSONException e) { return; }
                        Toast.makeText(getApplicationContext(), uname + ", " + time + ", " + mess, Toast.LENGTH_LONG).show();
                        unameL.add(uname);
                        timeL.add(time);
                        messageL.add(mess);
                    } else if (response.equals("nep")) {
                        progD.dismiss();
                        Snackbar.make(findViewById(R.id.linMap), "Nedovoljan broj igrača!", Snackbar.LENGTH_INDEFINITE).show();
                    } else if (response.equals("goHide")) {
                        try {
                            radius = data.getInt("radius");
                            setUpGame();
                            progD.dismiss();
                        } catch (JSONException e) { return; }
                    } else if (response.equals("gameON")) {
                        gameON = true;
                    } else if (response.equals("endT")) {
                        gApiCl.disconnect();
                        showDialog(true);
                    } else if (response.equals("endCG")) {
                        gApiCl.disconnect();
                        showDialog(false);
                    } else if (response.equals("removeMarker")) {
                        String uname;
                        try {
                            uname = data.getString("uname");
                        } catch (JSONException e) { return; }
                        if (uName.equals(uname)) {
                            gameON = false;
                            Snackbar.make(findViewById(R.id.linMap), "Završili ste igru!", Snackbar.LENGTH_INDEFINITE).show();
                        }
                        else
                            markers.get(uname).remove();
                    }
                }
            });
        }
    };

    private void showDialog(boolean b)
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MapActivity.this);
        dialog.setTitle("Kraj igre");
        dialog.setTitle(b ? "Tragači su pobedili" : "Koordinator i zaštitnici su pobedili");
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
        dialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mode.equals("game") && gameON)
        {
            /*Location myLoc = new Location("blabla");
            myLoc.setLatitude(43.31926517);
            myLoc.setLongitude(21.89886868);*/
            if (location.distanceTo(gmLoc) < radius) {
                JSONObject data = new JSONObject();
                try {
                    data.put("id", userID);
                    data.put("uname", uName);
                    data.put("gameID", gameID);
                    data.put("lat", location.getLatitude());
                    data.put("lng", location.getLongitude());
                    data.put("role", role);
                    data.put("mode", "location");
                } catch (JSONException e) { e.printStackTrace(); }
                LoginActivity.socket.emit("gameOn", data);
            } else {
                JSONObject data = new JSONObject();
                try {
                    data.put("id", userID);
                    data.put("uname", uName);
                    data.put("gameID", gameID);
                    data.put("role", role);
                    data.put("mode", "offarea");
                } catch (JSONException e) { e.printStackTrace(); }
                LoginActivity.socket.emit("gameOn", data);
            }
        }
    }

    private String makeURL()
    {
        Location myLoc = LocationServices.FusedLocationApi.getLastLocation(gApiCl);
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(myLoc.getLatitude());
        urlString.append(",");
        urlString.append(myLoc.getLongitude());
        urlString.append("&destination=");// to
        urlString.append(ll.latitude);
        urlString.append(",");
        urlString.append(ll.longitude);
        urlString.append("&sensor=false&mode=driving");
        urlString.append("&key=AIzaSyASJzxaiE-E2A5uRdNSIT-DdhSYbjqsPKc");
        return urlString.toString();
    }

    private String getJSONFromUrl(String url)
    {
        HttpURLConnection urlConnection;
        try {
            URL u = new URL(url);
            urlConnection = (HttpURLConnection) u.openConnection();
            urlConnection.connect();
            is = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            json = sb.toString();
            br.close();
            is.close();
            urlConnection.disconnect();
        } catch (IOException e) { e.printStackTrace(); }
        return json;
    }

    private List<LatLng> getLLPoints()
    {
        List<LatLng> list = null;
        JSONArray routeArray = null;

        try {
            String response = "";
            response = getJSONFromUrl(makeURL());
            final JSONObject json = new JSONObject(response);
            routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            list = decodePoly(encodedString);
        } catch (JSONException e) { e.printStackTrace(); }

        return list;
    }

    private List<LatLng> decodePoly(String encoded)
    {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationServices.FusedLocationApi.requestLocationUpdates(gApiCl, locRequest, this);
        if (mode.equals("nav"))
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final List<LatLng> list = getLLPoints();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            gmap.addPolyline(new PolylineOptions().addAll(list).width(12).color(Color.BLUE).geodesic(true));
                            progD.dismiss();
                        }
                    });
                }
            }).start();
        }
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

    @Override
    public void onBackPressed() {
        if (mode.equals("game") && gameON) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MapActivity.this);
            dialog.setTitle("Odustati");
            dialog.setTitle("Odustati od igre?");
            dialog.setPositiveButton("Da", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    JSONObject data = new JSONObject();
                    try {
                        data.put("id", userID);
                        data.put("uname", uName);
                        data.put("gameID", gameID);
                        data.put("role", role);
                        data.put("mode", "offarea");
                    } catch (JSONException e) { e.printStackTrace(); }
                    LoginActivity.socket.emit("gameOn", data);
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            });
            dialog.setNegativeButton("Ne", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            dialog.show();
        }
        else {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }
}
