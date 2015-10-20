package rs.elfak.simon.diplproba;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment smapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        smapFrag.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        LatLng position = new LatLng(43.3192769, 21.899564);
        gmap.setMyLocationEnabled(true);
        gmap.addMarker(new MarkerOptions().position(position));
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
        //gmap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        gmap.setOnMapLongClickListener(this);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Intent retInt = new Intent();
        retInt.putExtra("latlng", latLng);
        setResult(Activity.RESULT_OK, retInt);
        finish();
    }
}
