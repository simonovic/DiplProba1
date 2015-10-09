package rs.elfak.simon.diplproba;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Iterator;

public class FriendsFragment extends Fragment
{
    UserListAdapter frAdap;
    ArrayList<User> friends;
    ListView friendsList;
    View v;

    public ListView getFriendsList() {
        return friendsList;
    }

    public static FriendsFragment newInstance(String param1, String param2) {
        FriendsFragment fragment = new FriendsFragment();
        return fragment;
    }

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_friends, container, false);
        listFriends();
        return v;
    }

    public void listFriends()
    {
        String frList = ((MainActivity)getActivity()).getFrResp();
        if (frList.equals(""))
            return;
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
        friendsList = (ListView) v.findViewById(R.id.listFriends);
        friendsList.setAdapter(frAdap);
        friendsList.setOnItemLongClickListener(friendClickListener);
    }

    private AdapterView.OnItemLongClickListener friendClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
        {
            String user = ((TextView)view.findViewById(R.id.tvl)).getText().toString();
            int ID = friends.get(position).getId();
            ((MainActivity)getActivity()).showDialog(user, ID);
            return true;
        }
    };
}
