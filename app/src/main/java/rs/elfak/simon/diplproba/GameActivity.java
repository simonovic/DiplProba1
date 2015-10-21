package rs.elfak.simon.diplproba;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.GetChars;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameActivity extends AppCompatActivity
{
    Game game;
    EditText a,b,c,d,e;
    ArrayList<User> invFrAList, confFrAList;
    List<String> invFrL, confFrL;
    int userID;
    SharedPreferences shPref;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        LoginActivity.socket.on("getInvConfFrResponse", onGetInvConfFrResponse);
        LoginActivity.socket.on("delConfGameReqResponse", onDelConfGameReqResponse);

        game = MainFragment.getGameAtIndex(getIntent().getExtras().getInt("pos"));
        setUI();
        getInvConfFr();
    }

    private void setUI()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(""/*game.getName()*/);

        a = (EditText)findViewById(R.id.a);
        b = (EditText)findViewById(R.id.b);
        c = (EditText)findViewById(R.id.c);
        d = (EditText)findViewById(R.id.d);
        e = (EditText)findViewById(R.id.e);
        a.setText(game.getName());
        b.setText(game.getCreator());
        c.setText(game.getDatetime());
        d.setText(game.getAddress());
        e.setText(game.getDesc());
        invFrL = new ArrayList<String>();
        confFrL = new ArrayList<String>();

        shPref = getSharedPreferences(Constants.loginPref, Context.MODE_PRIVATE);
        //userID = shPref.getInt(Constants.userIDpref, 0);
        userID = 42;
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
                    if (response.equals("delGame"))
                    {
                        LoginActivity.socket.emit("findGames", userID);
                        finish();
                    }
                    else if (response.equals("confGame")) {
                        Toast.makeText(getApplicationContext(), "Dolazak potvrdjen!", Toast.LENGTH_LONG).show();
                        menu.findItem(R.id.confirmGame).setVisible(false);
                    }
                    else { // "failed"
                        if (img.equals("gbi"))
                            Toast.makeText(getApplicationContext(), "Neuspelo brisanje igre!", Toast.LENGTH_LONG).show();
                        else if (img.equals("gpi"))
                            Toast.makeText(getApplicationContext(), "Neuspela potvrda dolaska!", Toast.LENGTH_LONG).show();
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
                            for (int i = 0; i < confFrAList.size(); i++)
                                confFrL.add((i+1)+". "+confFrAList.get(i).getUname());
                        }
                        else
                            confFrAList = new ArrayList<User>();
                    }
                }
            });
        }
    };

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
        AlertDialog.Builder dialog = new AlertDialog.Builder(GameActivity.this);
        dialog.setTitle(b ? "Pozvani prijatelji:" : "Potvrdili dolazak:");
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
        if (userID != Integer.parseInt(game.getCreatorID()))
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
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        return super.onOptionsItemSelected(item);
    }
}
