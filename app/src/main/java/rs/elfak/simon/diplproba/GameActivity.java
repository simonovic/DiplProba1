package rs.elfak.simon.diplproba;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.github.nkzawa.emitter.Emitter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GameActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
                                                                GoogleApiClient.OnConnectionFailedListener
{
    static Game game;
    EditText a,b,c,d,e,safe;
    ArrayList<User> invFrAList, confFrAList;
    List<String> invFrL, confFrL;
    int userID, numConf;
    SharedPreferences shPref;
    SharedPreferences.Editor editor;
    Menu menu;
    GoogleApiClient gApiCl;
    Button btn;
    static Location myLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setUpApiClient();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUI();
        getInvConfFr();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LoginActivity.socket.off("getInvConfFrResponse", onGetInvConfFrResponse);
        LoginActivity.socket.off("delConfGameReqResponse", onDelConfGameReqResponse);
    }

    private void setUI()
    {
        LoginActivity.socket.on("getInvConfFrResponse", onGetInvConfFrResponse);
        LoginActivity.socket.on("delConfGameReqResponse", onDelConfGameReqResponse);
        shPref = getSharedPreferences(Constants.loginPref, Context.MODE_PRIVATE);
        editor = shPref.edit();
        game = MainFragment.getGameAtIndex(getIntent().getExtras().getInt("pos"));
        editor.putInt(Constants.gameIDpref, game.get_id());
        editor.commit();
        shPref = getSharedPreferences(Constants.loginPref, Context.MODE_PRIVATE);
        userID = shPref.getInt(Constants.userIDpref, 0);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Run 'n Hide"/*game.getName()*/);
        a = (EditText)findViewById(R.id.a);
        b = (EditText)findViewById(R.id.b);
        c = (EditText)findViewById(R.id.c);
        d = (EditText)findViewById(R.id.d);
        //e = (EditText)findViewById(R.id.e);
        safe = (EditText)findViewById(R.id.safe);
        a.setText(game.getName());
        b.setText(game.getCreator());
        c.setText(game.getDt());
        d.setText(game.getAddress());
        //e.setText(game.getDesc());
        safe.setText(game.getSafeRad()+"m, "+game.getSafeTime()+"s");
        btn = (Button)findViewById(R.id.startGameBtn);
        if (Integer.parseInt(game.getCreatorID()) != userID)
            btn.setText(R.string.join);
        invFrL = new ArrayList<String>();
        confFrL = new ArrayList<String>();

        JSONObject data = new JSONObject();
        try {
            data.put("gameID", game.get_id());
        } catch (JSONException e) { e.printStackTrace(); }
        LoginActivity.socket.emit("findGame", data);
    }

    private void setUpApiClient()
    {
        gApiCl = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        gApiCl.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //gApiCl.disconnect();
    }

    private Emitter.Listener onDelConfGameReqResponse = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String response, img;
                    JSONObject data = (JSONObject) args[0];
                    try {
                        response = data.getString("response");
                        img = data.getString("img");
                    } catch (JSONException e) { return; }
                    if (response.equals("delGame")) {
                        LoginActivity.socket.emit("findGames", userID);
                        finish();
                    }
                    else if (response.equals("confGame")) {
                        game.addConfID(userID);
                        confFrL.add((numConf + 1) + ". " + game.getCreator());
                        menu.findItem(R.id.confirmGame).setVisible(false);
                    }
                    else if (response.equals("onStartGame")) {
                        if (img.equals("ok")) {
                            myLoc = LocationServices.FusedLocationApi.getLastLocation(gApiCl);
                            /*myLoc = new Location("blabla");
                            myLoc.setLatitude(43.31926517);
                            myLoc.setLongitude(21.89886868);*/
                            Intent i = new Intent(getApplicationContext(), MapActivity.class);
                            double[] pom = {myLoc.getLatitude(), myLoc.getLongitude()};
                            double[] gpom = {game.getLat(), game.getLng()};
                            editor.putString(Constants.modePref, "game");
                            editor.commit();
                            i.putExtra("location", pom);
                            i.putExtra("glocation", gpom);
                            i.putExtra("gameID", game.get_id());
                            i.putExtra("mode", "game");
                            i.putExtra("creator", "no");
                            startActivity(i);
                        } else if (img.equals("vji"))
                            Snackbar.make(findViewById(R.id.actgamecl), "Isteklo je vreme za prijavu!", Snackbar.LENGTH_LONG).show();
                        else
                            Snackbar.make(findViewById(R.id.actgamecl), "Igra još uvek nije počela!", Snackbar.LENGTH_LONG).show();
                    }
                    else { // "failed"
                        if (img.equals("gbi"))
                            Snackbar.make(findViewById(R.id.actgamecl), "Neuspelo brisanje igre!", Snackbar.LENGTH_LONG).show();
                        else if (img.equals("gpi"))
                            Snackbar.make(findViewById(R.id.actgamecl), "Neuspela potvrda dolaska!", Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        }
    };

    private Emitter.Listener onGetInvConfFrResponse = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String confFr, invFr;
                    JSONObject data = (JSONObject) args[0];
                    try {
                        confFr = data.getString("confID");
                        invFr = data.getString("invID");
                    } catch (JSONException e) { return; }
                    if (invFr.equals("failed")) {
                        // greska na serveru
                    } else {
                        Gson gson = new GsonBuilder().serializeNulls().create();
                        invFrAList = gson.fromJson(invFr, new TypeToken<ArrayList<User>>(){}.getType());
                        for (int i = 0; i < invFrAList.size(); i++)
                            invFrL.add((i+1)+". "+invFrAList.get(i).getUname());
                        if (!confFr.equals("")) {
                            confFrAList = gson.fromJson(confFr, new TypeToken<ArrayList<User>>() {}.getType());
                            numConf = confFrAList.size();
                            for (int i = 0; i < numConf; i++)
                                confFrL.add((i+1)+". "+confFrAList.get(i).getUname());
                        }
                        else
                            confFrAList = new ArrayList<User>();
                    }
                }
            });
        }
    };

    public void onStartGameBtn(View v) {

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date gameDate = null;
            Date curDate = new Date();
            String datum = game.getDatetime().substring(0, 19);
            try {
                gameDate = format.parse(datum);
            } catch (ParseException e) { e.printStackTrace(); }
            long curTime = curDate.getTime();
            long gameTime = gameDate.getTime();
            if (curTime - gameTime > 0 )
            {
                if ((curTime - gameTime)/1000 > 60000) {  //smanji vreme na recimo 10 min
                    Snackbar.make(findViewById(R.id.actgamecl), "Isteklo je vreme za igru!", Snackbar.LENGTH_LONG).show();
                    LoginActivity.socket.emit("findGames", userID);
                    finish();
                }
                else
                {
                    if (gApiCl.isConnected()) {
                        myLoc = null;
                        while (myLoc == null)
                            myLoc = LocationServices.FusedLocationApi.getLastLocation(gApiCl);
                        /*myLoc = new Location("blabla");
                        myLoc.setLatitude(43.31926517);
                        myLoc.setLongitude(21.89886868);*/
                        if (myLoc != null) {
                            Location gameLoc = new Location("gameLocation");
                            gameLoc.setLatitude(game.getLat());
                            gameLoc.setLongitude(game.getLng());
                            float metres = myLoc.distanceTo(gameLoc);
                            if (metres < 50)
                            {
                                if (Integer.parseInt(game.getCreatorID()) == userID) { //creator)
                                    JSONObject data = new JSONObject();
                                    try {
                                        data.put("_id", userID);
                                        data.put("mode", "start");
                                        data.put("gameID", game.get_id());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    LoginActivity.socket.emit("startGame", data);

                                    Intent i = new Intent(this, MapActivity.class);
                                    double[] pom = {myLoc.getLatitude(), myLoc.getLongitude()};
                                    double[] gpom = {game.getLat(), game.getLng()};
                                    editor.putString(Constants.modePref,"game");
                                    editor.commit();
                                    i.putExtra("location", pom);
                                    i.putExtra("glocation", gpom);
                                    i.putExtra("gameID", game.get_id());
                                    i.putExtra("mode", "game");
                                    i.putExtra("creator", "yes");
                                    startActivity(i);
                                } else {
                                    JSONObject data = new JSONObject();
                                    try {
                                        data.put("_id", userID);
                                        data.put("mode", "join");
                                        data.put("gameID", game.get_id());
                                    } catch (JSONException e) { e.printStackTrace(); }
                                    LoginActivity.socket.emit("startGame", data);
                                }
                            }
                            else
                                Snackbar.make(findViewById(R.id.actgamecl), "Niste na lokaciji igre!", Snackbar.LENGTH_LONG).show();
                        }
                    }
                    else
                        Snackbar.make(findViewById(R.id.actgamecl), "Neuspelo povezivanje sa Google Servisima!", Snackbar.LENGTH_LONG).show();
                }
            }
            else
                Snackbar.make(findViewById(R.id.actgamecl), "Još uvek nije vreme za igru!", Snackbar.LENGTH_LONG).show();
    }

    private void getInvConfFr()
    {
        JSONArray confJsonID = new JSONArray();
        int[] frID = game.getConfirmedUsersID();
        for (int i = 0; i < frID.length; i++) {
            try {
                confJsonID.put(frID[i]);
            } catch (Exception e) {} }
        JSONArray invJsonID = new JSONArray();
        frID = game.getInvitedUsersID();
        for (int i = 0; i < frID.length; i++) {
            try {
                invJsonID.put(frID[i]);
            } catch (Exception e) {} }
        JSONObject data = new JSONObject();
        try {
            data.put("confID", confJsonID);
            data.put("invID", invJsonID);
        } catch (JSONException e) { e.printStackTrace(); }
        LoginActivity.socket.emit("getInvConfFr", data);
    }

    public void onInvBtn(View v)
    {
        showDialog(true);
    }

    public void onConfBtn(View v)
    {
        showDialog(false);
    }

    private void showDialog(boolean b)
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(GameActivity.this, R.style.DialogTheme);
        dialog.setTitle(b ? "Pozvani igrači:" : "Potvrdili dolazak:");
        View v = getLayoutInflater().inflate(R.layout.choose_friends, null);
        dialog.setView(v);
        ListView lv = (ListView)v.findViewById(R.id.chFrLV);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, b ? invFrL : confFrL);
        lv.setAdapter(adapter);
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_game, menu);
        if (userID == Integer.parseInt(game.getCreatorID()))
            menu.findItem(R.id.delete).setVisible(false);
        int[] pom = game.getConfirmedUsersID();
        for (int i = 0; i < pom.length; i++)
            if (userID == pom[i]) {
                menu.findItem(R.id.confirmGame).setVisible(false);
                break;
            }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.delete)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
            builder.setTitle(game.getName())
                    .setMessage("Odustati od igre?")
                    .setNegativeButton("Ne", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            JSONObject data = new JSONObject();
                            try {
                                data.put("_id", userID);
                                data.put("gameID", game.get_id());
                                data.put("mode", "del");
                            } catch (JSONException e) { e.printStackTrace(); }
                            LoginActivity.socket.emit("delConfGame", data);
                        }
                    })
                    .show();
        }
        else if (id == R.id.confirmGame)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
            builder.setTitle(game.getName())
                    .setMessage("Potvrditi dolazak?")
                    .setNegativeButton("Ne", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            JSONObject data = new JSONObject();
                            try {
                                data.put("_id", userID);
                                data.put("gameID", game.get_id());
                                data.put("mode", "conf");
                            } catch (JSONException e) { e.printStackTrace(); }
                            LoginActivity.socket.emit("delConfGame", data);
                        }
                    })
                    .show();
        }
        else if (id == R.id.navigation)
        {
            Intent i = new Intent(this, MapActivity.class);
            double[] pom = {game.getLat(), game.getLng()};
            editor.putString(Constants.modePref, "nav");
            editor.commit();
            i.putExtra("location", pom);
            i.putExtra("mode", "nav");
            startActivity(i);
        }
        else {
            finish();
            }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
