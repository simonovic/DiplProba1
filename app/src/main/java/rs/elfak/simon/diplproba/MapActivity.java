package rs.elfak.simon.diplproba;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener
{
    GoogleMap gmap;
    String mode;
    LatLng ll;
    Bundle extras;
    FloatingActionButton chatBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        chatBtn = (FloatingActionButton)findViewById(R.id.chatBtn);
        //chatBtn.setVisibility(View.GONE);
        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ChatActivity.class));
            }
        });

        extras = getIntent().getExtras();
        mode = extras.getString("mode");

        SupportMapFragment smapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        smapFrag.getMapAsync(this);
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

        if (mode.equals("nav")) {
            setUpNav();
        }

        if (mode.equals("newGame"))
            setUpNewGame();

        gmap.setMyLocationEnabled(true);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Intent retInt = new Intent();
        retInt.putExtra("latlng", latLng);
        setResult(Activity.RESULT_OK, retInt);
        finish();
    }
}
