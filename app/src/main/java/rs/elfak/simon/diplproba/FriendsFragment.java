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
    ArrayAdapter<String> friendsAdapter;
    UserListAdapter frAdap;
    ArrayList<User> friends;
    String friendsID = "";
    View v;

    String[] name ={
            "Safari",
            "Camera",
            "Global",
            "FireFox",
            "UC Browser",
            "Android Folder",
            "VLC Player",
            "Cold War",
            "UC Browser",
            "Android Folder",
            "VLC Player",
            "Cold War"
    };

    String[] uname ={
            "Safari1",
            "Camera1",
            "Global1",
            "FireFox1",
            "UC Browser1",
            "Android Folder1",
            "VLC Player1",
            "Cold War1",
            "UC Browser1",
            "Android Folder1",
            "VLC Player1",
            "Cold War1"
    };
    Integer[] pom = {1,2,3};

    public static FriendsFragment newInstance(String param1, String param2) {
        FriendsFragment fragment = new FriendsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        frAdap = new UserListAdapter(getActivity().getApplicationContext(), name, uname, pom);
    }

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_friends, container, false);
        Button mainBtn = (Button)v.findViewById(R.id.mainBtn);
        mainBtn.setOnClickListener(this);
        listFriends("[{}]");
        return v;
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

    public void listFriends(String f)
    {
        /*friendsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
        Gson gson = new GsonBuilder().serializeNulls().create();
        friends = gson.fromJson(f, new TypeToken<ArrayList<User>>(){}.getType());
        int br = 1;
        for (Iterator<User> i = friends.iterator(); i.hasNext(); ) {
            User fr = i.next();
            friendsAdapter.add(br+".  "+fr.getFname()+" "+fr.getLname());
            friendsID += fr.getId()+" ";
            br++;
        }*/

        /*friendsAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1);
        for (int i = 0; i < 4; i++)
        {
            friendsAdapter.add("Item ["+i+"]");
        }*/

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
