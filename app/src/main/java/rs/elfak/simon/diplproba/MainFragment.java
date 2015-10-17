package rs.elfak.simon.diplproba;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class MainFragment extends Fragment
{
    View v;
    RecyclerView recView;
    ArrayList<Game> games;
    SwipeRefreshLayout srl;

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    public MainFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_main, container, false);
        srl = (SwipeRefreshLayout)v.findViewById(R.id.swipeRefreshLayout);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshGames();
            }
        });
        //games = new ArrayList<Game>();
        listGames();
        return v;
    }

    public void refreshGames()
    {
        Toast.makeText(getActivity().getApplicationContext(), "Radi refresh!", Toast.LENGTH_LONG).show();
        srl.setRefreshing(false);
    }

    public void listGames()
    {
        recView = (RecyclerView)v.findViewById(R.id.recView);
        LinearLayoutManager layMan = new LinearLayoutManager(getActivity().getApplicationContext());
        recView.setLayoutManager(layMan);

        String pom = ((MainActivity)getActivity()).getGames();
        if (pom.equals(""))
            return;

        Gson gson = new GsonBuilder().serializeNulls().create();
        games = gson.fromJson(pom, new TypeToken<ArrayList<Game>>(){}.getType());
        Geocoder geocoder;
        List<Address> addresses = new ArrayList<Address>();
        geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
        for (Iterator<Game> g = games.iterator(); g.hasNext(); ) {
            Game gm = g.next();
            try {
                addresses = geocoder.getFromLocation(gm.getLat(), gm.getLng(), 1);
            } catch (IOException e) {}
            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            gm.setAddress(address+", "+city);
        }

        GameAdapter gameAdapter = new GameAdapter(games);
        recView.setAdapter(gameAdapter);
    }
}
