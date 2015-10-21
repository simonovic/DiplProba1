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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class MainFragment extends Fragment
{
    View v;
    RecyclerView recView;
    static ArrayList<Game> games;
    SwipeRefreshLayout srl;
    GameAdapter gameAdapter;

    public RecyclerView getRecView() { return recView; }

    public static Game getGameAtIndex(int index) { return games.get(index); }

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
        recView = (RecyclerView)v.findViewById(R.id.recView);
        LinearLayoutManager layMan = new LinearLayoutManager(getActivity().getApplicationContext());
        recView.setLayoutManager(layMan);
        srl = (SwipeRefreshLayout)v.findViewById(R.id.swipeRefreshLayout);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LoginActivity.socket.emit("findGames", ((MainActivity) getActivity()).getUserID());
            }
        });
        listGames();
        return v;
    }

    public void listGames()
    {
        if (srl.isRefreshing())
            srl.setRefreshing(false);

        String pom = ((MainActivity)getActivity()).getGames();
        if (pom.equals(""))
            return;

        Gson gson = new GsonBuilder().serializeNulls().create();
        games = gson.fromJson(pom, new TypeToken<ArrayList<Game>>(){}.getType());
        Geocoder geocoder;
        List<Address> addresses = new ArrayList<Address>();
        geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = null;
        Calendar cal = Calendar.getInstance();
        for (Iterator<Game> g = games.iterator(); g.hasNext(); ) {
            Game gm = g.next();
            String datum = gm.getDatetime().substring(0, 19);
            try {
                addresses = geocoder.getFromLocation(gm.getLat(), gm.getLng(), 1);
                date = format.parse(datum);
            } catch (IOException e) {}
            catch (ParseException e) {}
            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            gm.setAddress(address+", "+city);
            cal.setTime(date);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH)+1;
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int mins = cal.get(Calendar.MINUTE);
            String h = hour+"", min = mins+"", d = day+"", m = month+"";
            if (hour<10)
                h = "0"+h;
            if (mins<10)
                min = "0"+min;
            if (day<10)
                d = "0"+d;
            if (month<10)
                m = "0"+m;
            gm.setDatetime(h+":"+min+", "+d+"."+m+"."+year+".");
        }
        gameAdapter = new GameAdapter(games);
        recView.setAdapter(gameAdapter);
    }
}
