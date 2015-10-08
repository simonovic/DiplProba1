package rs.elfak.simon.diplproba;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class FriendsFragment extends Fragment implements OnMapReadyCallback
{
    public static FriendsFragment newInstance(String param1, String param2) {
        FriendsFragment fragment = new FriendsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //SupportMapFragment mapFragment = (SupportMapFragment) mg.findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);
    }

    public FriendsFragment() {
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
