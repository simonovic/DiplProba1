package rs.elfak.simon.diplproba;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Iterator;

public class FriendsFragment extends Fragment implements View.OnClickListener
{
    UserListAdapter frAdap;
    ArrayList<User> friends;
    View v;

    public static FriendsFragment newInstance(String param1, String param2) {
        FriendsFragment fragment = new FriendsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_friends, container, false);
        Button mainBtn = (Button)v.findViewById(R.id.mainBtn);
        mainBtn.setOnClickListener(this);
        listFriends();
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mainBtn:
                Toast.makeText(getActivity(), "FriendsFragment", Toast.LENGTH_LONG).show();
                break;
            case R.id.listFriends:
                break;
        }
    }

    public void listFriends()
    {
        String frList = ((MainActivity)getActivity()).getFrResp();
        Gson gson = new GsonBuilder().serializeNulls().create();
        friends = gson.fromJson(frList, new TypeToken<ArrayList<User>>(){}.getType());

        ArrayList<String> name = new ArrayList<String>();
        ArrayList<String> uname = new ArrayList<String>();
        for (Iterator<User> u = friends.iterator(); u.hasNext(); ) {
            User fr = u.next();
            name.add(fr.getFname()+" "+fr.getLname());
            uname.add(fr.getUname());
        }
        String[] names = name.toArray(new String[name.size()]);
        String[] unames = uname.toArray(new String[uname.size()]);
        Integer[] pom = {1,2,3};
        frAdap = new UserListAdapter(getActivity().getApplicationContext(), names, unames, pom);
        ListView friendsList = (ListView) v.findViewById(R.id.listFriends);
        friendsList.setAdapter(frAdap);
        friendsList.setOnItemClickListener(friendClickListener);
    }

    private AdapterView.OnItemClickListener friendClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            Toast.makeText(getActivity().getApplicationContext(), "Klik na item["+position+"]", Toast.LENGTH_LONG).show();
        }
    };
}
