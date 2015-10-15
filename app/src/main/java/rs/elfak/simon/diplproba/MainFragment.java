package rs.elfak.simon.diplproba;

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

import java.util.ArrayList;

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

        /*String pom = ((MainActivity)getActivity()).getGameResp();
        Gson gson = new GsonBuilder().serializeNulls().create();
        games = gson.fromJson(pom, new TypeToken<ArrayList<User>>(){}.getType());*/

        //za probu
        games = new ArrayList<Game>();
        int[] a = {1,2,3};
        int[] b = {4,5,6};
        Game g1 = new Game(1, "ime1", "opis1", "kreator1", "datum1", "adresa1", new LatLng(23.2222, 41.2222), a, b);
        Game g2 = new Game(2, "ime2", "opis2", "kreator2", "datum2", "adresa2", new LatLng(23.2222, 41.2222), a, b);
        Game g3 = new Game(3, "ime3", "opis3", "kreator3", "datum3", "adresa3", new LatLng(23.2222, 41.2222), a, b);
        games.add(g1);
        games.add(g2);
        games.add(g3);

        recView = (RecyclerView)v.findViewById(R.id.recView);
        LinearLayoutManager layMan = new LinearLayoutManager(getActivity().getApplicationContext());
        recView.setLayoutManager(layMan);
        GameAdapter gameAdapter = new GameAdapter(games);
        recView.setAdapter(gameAdapter);
    }
}
