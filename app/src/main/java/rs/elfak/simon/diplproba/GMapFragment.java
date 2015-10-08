package rs.elfak.simon.diplproba;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.support.v4.app.FragmentManager;

public class GMapFragment extends Fragment implements OnMapReadyCallback
{
    public static GMapFragment newInstance(String param1, String param2) {
        GMapFragment fragment = new GMapFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //SupportMapFragment mapFragment = (SupportMapFragment) mg.findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);
    }

    public GMapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gmap, container, false);
    }
    // ovde se dodaju markeri, linije, listenr-i, ili pomera kamera
    @Override
    public void onMapReady(GoogleMap gmap) {
        gmap.setMyLocationEnabled(true);
        gmap.addMarker(new MarkerOptions().position(new LatLng(43.3192769, 21.899564)));
    }
}
