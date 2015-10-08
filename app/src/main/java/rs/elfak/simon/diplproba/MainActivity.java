package rs.elfak.simon.diplproba;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;
import android.widget.EditText;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
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
    JSONArray users1;

    boolean searchOpened = false;
    EditText edtSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        mDrawer.setDrawerListener(drawerToggle);
        fm = getSupportFragmentManager();

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent();

        //startActivity(new Intent(this, MapActivity.class));
        LoginActivity.socket.on("findUsersResponse", onFindUsersResponse);
    }

    private Emitter.Listener onFindUsersResponse = new Emitter.Listener() {
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
                    if (response.equals("nomatch")) {
                        Toast.makeText(getApplicationContext(), "Nema takvog korisnika!", Toast.LENGTH_SHORT).show();
                    } else
                    {
                        Gson gson = new GsonBuilder().serializeNulls().create();
                        users = gson.fromJson(response, new TypeToken<ArrayList<User>>(){}.getType());
                        Toast.makeText(getApplicationContext(), users.get(0).getUname(), Toast.LENGTH_SHORT).show();
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
        Class fragClass;
        int pom = menuItem.getItemId();
        switch (menuItem.getItemId()) {
            case R.id.nav_first_fragment:
                fragClass = FriendsFragment.class;
                break;
            case R.id.nav_second_fragment:
                fragClass = FriendsFragment.class;
                break;
            case R.id.sub1:
                fragClass = FriendsFragment.class;
                break;
            default:
                fragClass = FriendsFragment.class;
        }
        try {
            fragment = (Fragment)fragClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace(); }
        fm.beginTransaction().replace(R.id.flContent, fragment).commit();
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.search);

        searchView = (SearchView)menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint("Pretraži...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                String pom = searchView.getQuery().toString();
                if (!pom.equals(""))
                    LoginActivity.socket.emit("findUsers", pom);
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
}
