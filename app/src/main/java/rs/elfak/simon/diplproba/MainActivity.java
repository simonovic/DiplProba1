package rs.elfak.simon.diplproba;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback
{
    DrawerLayout mDrawer;
    Toolbar toolbar;
    NavigationView nvDrawer;
    ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        startActivity(new Intent(this, MapActivity.class));
    }

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
                fragClass = GMapFragment.class;
                break;
            case R.id.nav_second_fragment:
                fragClass = GMapFragment.class;
                break;
            case R.id.sub1:
                fragClass = GMapFragment.class;
                break;
            default:
                fragClass = GMapFragment.class;
        }
        try {
            fragment = (Fragment)fragClass.newInstance();
        } catch (Exception e) { e.printStackTrace(); }
        getSupportFragmentManager().beginTransaction().replace(R.id.flContent, fragment).commit();
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();

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

    @Override
    public void onMapReady(GoogleMap gmap) {
        gmap.addMarker(new MarkerOptions().position(new LatLng(43.3192769, 21.899564)));
    }
}
