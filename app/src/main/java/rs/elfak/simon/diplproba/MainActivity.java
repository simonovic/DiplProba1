package rs.elfak.simon.diplproba;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.github.nkzawa.emitter.Emitter;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    DrawerLayout mDrawer;
    Toolbar toolbar;
    NavigationView nvDrawer;
    ActionBarDrawerToggle drawerToggle;
    SearchView searchView;
    FragmentManager fm;
    ArrayList<User> users;
    MenuItem searchItem;
    int userID, ID;
    String frResp = "", frResp1 = "", imgResp = "", imgResp1 = "", friends = "", games = "", frReqSentStr = "", frReqSentImg = "";
    boolean mode, update = false, frReqSent = false, backPress = false;
    SharedPreferences shPref;
    SharedPreferences.Editor editor;
    boolean chosenFr[];
    String uName;

    public void setUpdate(boolean u) {  update = u; }
    public MenuItem getSearchItem() { return searchItem; }
    public String getFrReqSentImg() { return frReqSentImg; }
    public String getFrReqSentStr() { return frReqSentStr; }
    public boolean getFrReqSent() { return frReqSent; }
    public void setFrReqSent(boolean b) { frReqSent = b; }
    public int getUserID() { return userID; }
    public boolean[] getChosenFr() { return chosenFr; }
    public void setChosenFr(boolean[] chosenFr) { this.chosenFr = chosenFr; }
    public String getFrResp() { return frResp; }
    public String getImgResp() { return imgResp; }
    public String getFriends() { return  friends; }
    public String getGames() {return games; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpUI();
        setUpSocketIO();
    }

    public void setUpUI()
    {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        mDrawer.setDrawerListener(drawerToggle);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent();
        fm = getSupportFragmentManager();
        shPref = getSharedPreferences(Constants.loginPref, Context.MODE_PRIVATE);
        editor = shPref.edit();
        userID = shPref.getInt(Constants.userIDpref, 0);
        uName = shPref.getString(Constants.userNamepref, "");
        fm.beginTransaction().replace(R.id.flContent, MainFragment.newInstance())/*.addToBackStack(null)*/.commit();
        nvDrawer.getMenu().getItem(0).setChecked(true);
        TextView tv = (TextView)findViewById(R.id.unametv);
        tv.setText(uName);
    }

    public void setUpSocketIO()
    {
        LoginActivity.socket.on("profImgReqResponse", onProfImgReqResponse);
        LoginActivity.socket.on("findUsersResponse", onFindUsersResponse);
        LoginActivity.socket.on("findFriendsResponse", onFindFriendsResponse);
        LoginActivity.socket.on("friendReqResponse", onFriendReqResponse);
        LoginActivity.socket.on("imgReqResponse", onImgResponse);
        LoginActivity.socket.on("newGameReqResponse", onNewGameReqResponse);
        LoginActivity.socket.on("gameReqResponse", onGameReqResponse);
        LoginActivity.socket.emit("findGames", userID);
        LoginActivity.socket.emit("findFriends", userID);
        LoginActivity.socket.emit("profImg", userID);
    }

    private Emitter.Listener onProfImgReqResponse = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String response;
                    JSONObject data = (JSONObject) args[0];
                    try {
                        response = data.getString("response");
                    } catch (JSONException e) { return; }
                    byte[] decodedString = Base64.decode(response, Base64.DEFAULT);
                    Bitmap bm = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    ImageView profImg = (ImageView)findViewById(R.id.profImg);
                    profImg.setImageBitmap(bm);
                }
            });
        }
    };

    private Emitter.Listener onGameReqResponse = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String response;
                    JSONObject data = (JSONObject) args[0];
                    try {
                        response = data.getString("response");
                    } catch (JSONException e) { return; }

                    if (!response.equals("nogames"))
                    {
                        games = response;
                        MainFragment mf = (MainFragment) fm.findFragmentById(R.id.flContent);
                        mf.listGames();
                    }
                    else
                    {
                        games = "";
                        MainFragment mf = (MainFragment) fm.findFragmentById(R.id.flContent);
                        if (mf.getSrl().isRefreshing())
                            mf.getSrl().setRefreshing(false);
                        mf.getRecView().setAdapter(null);
                    }
                }
            });
        }
    };

    private Emitter.Listener onNewGameReqResponse = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String response;
                    JSONObject data = (JSONObject) args[0];
                    try {
                        response = data.getString("response");
                    } catch (JSONException e) { return; }
                    if (response.equals("success"))
                    {
                        Fragment fragment = null;
                        try {
                            fragment = MainFragment.class.newInstance();
                        } catch (Exception e) {
                            e.printStackTrace(); }
                        fm.beginTransaction().replace(R.id.flContent, fragment).commit();
                        LoginActivity.socket.emit("findGames", userID);
                        nvDrawer.getMenu().getItem(0).setChecked(true);
                    } else {
                        Toast.makeText(getApplicationContext(), "Neuspelo kreirenje igre!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    };

    private Emitter.Listener onImgResponse = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String response;
                    String fORu;
                    JSONObject data = (JSONObject) args[0];
                    try {
                        response = data.getString("response");
                        fORu = data.getString("fORu");
                    } catch (JSONException e) { return; }

                    if (fORu.equals("f")) {
                        Toast.makeText(getApplicationContext(), "f", Toast.LENGTH_SHORT).show();
                        imgResp = imgResp1 = response;
                    } else {
                        Toast.makeText(getApplicationContext(), "u", Toast.LENGTH_SHORT).show();
                        imgResp = response;
                    }
                }
            });
        }
    };

    private Emitter.Listener onFriendReqResponse = new Emitter.Listener() {
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
                    if (response.equals("failed"))
                    {
                        if (img.equals("gpbp"))
                            Toast.makeText(getApplicationContext(), "Neuspelo brisanja prijatelja!", Toast.LENGTH_SHORT).show();
                        else if (img.equals("gzp"))
                            Toast.makeText(getApplicationContext(), "Neuspelo slanje zahteva za prijateljstvo!", Toast.LENGTH_SHORT).show();
                        else if (img.equals("gpz"))
                            Toast.makeText(getApplicationContext(), "Neuspelo pribavaljanje zahteva!!", Toast.LENGTH_SHORT).show();
                        else if (img.equals("gbz"))
                            Toast.makeText(getApplicationContext(), "Neuspelo brisanje zahteva!!", Toast.LENGTH_SHORT).show();
                    } else if (response.equals("denied"))
                    {
                        if (img.equals("AS"))
                            Toast.makeText(getApplicationContext(), "Zahtev je vec poslat!", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getApplicationContext(), "Zahtev je vec primljen!", Toast.LENGTH_SHORT).show();
                    }
                    else if (response.equals("delReq"))
                    {
                        Toast.makeText(getApplicationContext(), "Zahtev izbrisan!", Toast.LENGTH_SHORT).show();
                        JSONObject data1 = new JSONObject();
                        try {
                            data1.put("_id", userID);
                            data1.put("mode", "reqUsers");
                        } catch (JSONException e) { e.printStackTrace(); }
                        LoginActivity.socket.emit("friendReqSent", data1);
                    }
                    else if (response.equals("unfriend"))
                    {
                        update = true;
                        LoginActivity.socket.emit("findFriends", userID);
                        Toast.makeText(getApplicationContext(), "Izbrisan prijatelj!", Toast.LENGTH_SHORT).show();
                    }
                    else if (response.equals("sentFrReq"))
                    {
                        Toast.makeText(getApplicationContext(), "Zahtev poslat!", Toast.LENGTH_SHORT).show();
                    }
                    else if (response.equals("confFr"))
                    {
                        Toast.makeText(getApplicationContext(), "Dodat prijatelj!", Toast.LENGTH_SHORT).show();
                        LoginActivity.socket.emit("findFriends", userID);
                        JSONObject data1 = new JSONObject();
                        try {
                            data1.put("_id", userID);
                            data1.put("mode", "reqUsers");
                        } catch (JSONException e) { e.printStackTrace(); }
                        LoginActivity.socket.emit("friendReqSent", data1);
                    }
                    else if (response.equals("nomatch"))
                    {
                        Toast.makeText(getApplicationContext(), "Nema zahteva za prijateljstvom!", Toast.LENGTH_SHORT).show();
                        frReqSent = false;
                        searchItem.setVisible(true);
                        update = true;
                        FriendsFragment fr = (FriendsFragment) fm.findFragmentById(R.id.flContent);
                        fr.getBtn().setText("Zahtevi");
                        LoginActivity.socket.emit("findFriends", userID);
                        //fr.getFriendsList().setAdapter(null);
                    }
                    else
                    {
                        frReqSent = true;
                        frReqSentStr = response;
                        frReqSentImg = img;
                        if (!frReqSentStr.equals("")) {
                            searchItem.setVisible(false);
                            FriendsFragment fr = (FriendsFragment) fm.findFragmentById(R.id.flContent);
                            fr.getBtn().setText("Završi");
                            fr.listFriends();
                        }
                    }
                }
            });
        }
    };

    private Emitter.Listener onFindUsersResponse = new Emitter.Listener() {
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

                    if (response.equals("nomatch")) {
                        FriendsFragment fr = (FriendsFragment) fm.findFragmentById(R.id.flContent);
                        fr.getFriendsList().setAdapter(null);
                    } else
                    {
                        frResp = response;
                        imgResp = img;
                        FriendsFragment fr = (FriendsFragment) fm.findFragmentById(R.id.flContent);
                        fr.listFriends();
                    }
                }
            });
        }
    };

    private Emitter.Listener onFindFriendsResponse = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String response, img, pom;
                    JSONObject data = (JSONObject) args[0];
                    try {
                        response = data.getString("response");
                        img = data.getString("img");
                        pom = data.getString("friends");
                    } catch (JSONException e) { return; }

                    if (pom.length() > 0)
                        friends = pom.substring(1, pom.length() - 1);

                    if (!response.equals("nofriends")) {
                        frResp = frResp1 = response;
                        imgResp = imgResp1 = img;
                        if (update) {
                            FriendsFragment fr = (FriendsFragment) fm.findFragmentById(R.id.flContent);
                            if (fr.getSrlFr().isRefreshing())
                                fr.getSrlFr().setRefreshing(false);
                            fr.listFriends();
                            update = false;
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Nema prijatelja!", Toast.LENGTH_SHORT).show();
                        frResp = frResp1 = "";
                        imgResp = imgResp1 = "";
                        if (update) {
                            FriendsFragment fr = (FriendsFragment) fm.findFragmentById(R.id.flContent);
                            fr.getFriendsList().setAdapter(null);
                            if (update) update = false;
                        }
                    }
                }
            });
        }
    };

    private void setupDrawerContent()
    {
        nvDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                refreshNavGroup(menuItem);
                selectDrawerItem(menuItem);
                return true;
            }
        });
    }

    private void refreshNavGroup(MenuItem menuItem)
    {
        if (menuItem.getGroupId() == R.id.group1) {
            nvDrawer.getMenu().setGroupCheckable(R.id.group1, true, true);
            nvDrawer.getMenu().setGroupCheckable(R.id.group2, false, true);
        } else {
            nvDrawer.getMenu().setGroupCheckable(R.id.group1, false, true);
            nvDrawer.getMenu().setGroupCheckable(R.id.group2, true, true);
        }
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    private void selectDrawerItem(MenuItem menuItem)
    {
        Fragment fragment = null;
        Class fragClass = MainFragment.class;
        switch (menuItem.getItemId()) {
            case R.id.nav_first_fragment:
                fragClass = MainFragment.class;
                searchItem.setVisible(false);
                LoginActivity.socket.emit("findGames", userID);
                backPress = true;
                break;
            case R.id.nav_second_fragment:
                fragClass = FriendsFragment.class;
                searchItem.setVisible(true);
                LoginActivity.socket.emit("findFriends", userID);
                backPress = false;
                break;
            case R.id.nav_third_fragment:
                fragClass = NewGameFragment.class;
                searchItem.setVisible(false);
                backPress = false;
                break;
            case R.id.rules:
                fragClass = GameRulesFragment.class;
                searchItem.setVisible(false);
                backPress = false;
                break;
            case R.id.logout:
                editor.putInt(Constants.userIDpref, 0);
                backPress = false;
                editor.putString(Constants.userNamepref, "false");
                editor.commit();
                finish();
                break;
        }
        try {
            fragment = (Fragment)fragClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace(); }
        fm.beginTransaction().replace(R.id.flContent, fragment).addToBackStack(null).commit();
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }

    public void showDialog(String user, int id)
    {
        String msg;
        ID = id;
        FriendsFragment fr = (FriendsFragment) fm.findFragmentById(R.id.flContent);
        users = fr.getFriends();
        if (!frReqSent) {
            if (friends.contains(ID+"")) {
                msg = "Ukloniti korisnika '" + user + "' iz prijatelja?";
                mode = true;
            } else {  // dadati proveru da li vec poslat zahtev
                msg = "Poslati zahtev korisniku '" + user + "'?";
                mode = false;
            }
        }
        else
        {
            msg = "Prihvatiti '" + user + "' za prijatelja?";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Prijateljstvo").setMessage(msg)
                .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!frReqSent) {
                            if (mode) {
                                JSONObject data = new JSONObject();
                                try {
                                    data.put("_id", userID);
                                    data.put("friendID", ID);
                                    data.put("mode", "unfriend");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                LoginActivity.socket.emit("friendReq", data);
                            } else
                            {
                                JSONObject data = new JSONObject();
                                try {
                                    data.put("_id", userID);
                                    data.put("userID", ID);
                                    data.put("mode", "req");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                LoginActivity.socket.emit("friendReqSent", data);
                            }
                        }
                        else
                        {
                            JSONObject data = new JSONObject();
                            try {
                                data.put("_id", userID);
                                data.put("userID", ID);
                                data.put("mode", "confFr");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            LoginActivity.socket.emit("friendReq", data);
                        }
                    }
                })
                .setNegativeButton("Ne", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                if (frReqSent)
                    builder.setNeutralButton("Izbriši zahtev", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            JSONObject data = new JSONObject();
                            try {
                                data.put("_id", userID);
                                data.put("userID", ID);
                                data.put("mode", "delReq");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            LoginActivity.socket.emit("friendReqSent", data);
                        }
                    });

                builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        searchItem = menu.findItem(R.id.search);
        searchItem.setVisible(false);
        searchView = (SearchView)searchItem.getActionView();
        searchView.setQueryHint("Pretraži...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.equals("")) {
                    FriendsFragment fr = (FriendsFragment) fm.findFragmentById(R.id.flContent);
                    fr.getBtn().setVisibility(View.GONE);
                    JSONObject data = new JSONObject();
                    try {
                        data.put("_id", userID);
                        data.put("text", newText);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    LoginActivity.socket.emit("findUsers", data);
                } else {
                    frResp = frResp1;
                    imgResp = imgResp1;
                    FriendsFragment fr = (FriendsFragment) fm.findFragmentById(R.id.flContent);
                    fr.getBtn().setVisibility(View.VISIBLE);
                    fr.listFriends();
                }
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    public void populateChFr()
    {
        NewGameFragment ngf = (NewGameFragment) fm.findFragmentById(R.id.flContent);
        ngf.populateChoosenFr();
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START))
        {
            mDrawer.closeDrawer(Gravity.LEFT);
            return;
        }
        /*if (backPress)
            moveTaskToBack(true); // ili finish();
        else
            super.onBackPressed();*/
        moveTaskToBack(true);
    }
}