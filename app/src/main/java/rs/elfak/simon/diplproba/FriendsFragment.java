package rs.elfak.simon.diplproba;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class FriendsFragment extends Fragment implements View.OnClickListener
{
    UserListAdapter frAdap;
    ArrayList<User> friends;
    ListView friendsList;
    View v;
    SwipeRefreshLayout srl;
    Button btn;
    int userID;

    public ArrayList<User> getFriends() {
        return friends;
    }
    public ListView getFriendsList() {
        return friendsList;
    }
    public Button getBtn() { return btn; }

    public static FriendsFragment newInstance(String param1, String param2) {
        FriendsFragment fragment = new FriendsFragment();
        return fragment;
    }

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_friends, container, false);
        srl = (SwipeRefreshLayout)v.findViewById(R.id.swipeRefreshLayout);
        friendsList = (ListView) v.findViewById(R.id.listFriends);
        btn = (Button)v.findViewById(R.id.frReq);
        btn.setText("Zahtevi");
        btn.setOnClickListener(this);
        userID = ((MainActivity)getActivity()).getUserID();
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LoginActivity.socket.emit("findFriends", userID);
            }
        });
        listFriends();  // premesti u nekidrugi lifecycle
        return v;
    }

    public void listFriends()
    {
        if (srl.isRefreshing())
            srl.setRefreshing(false);

        String frList = "";
        boolean frsb = ((MainActivity)getActivity()).getFrReqSent();
        if (frsb)
            frList = ((MainActivity) getActivity()).getFrReqSentStr();
        else
            frList = ((MainActivity)getActivity()).getFrResp();

        if (frList.equals("")) {
            friendsList.setAdapter(null);
            return;
        }
        Gson gson = new GsonBuilder().serializeNulls().create();
        friends = gson.fromJson(frList, new TypeToken<ArrayList<User>>(){}.getType());
        ArrayList<String> name = new ArrayList<String>();
        ArrayList<String> uname = new ArrayList<String>();
        String pom[] = ((MainActivity)getActivity()).getFriends().split(",");
        ArrayList<String> imaf = new ArrayList<String>();
        for (Iterator<User> u = friends.iterator(); u.hasNext(); ) {
            User fr = u.next();
            name.add(fr.getFname() + " " + fr.getLname());
            uname.add(fr.getUname());
            if (Arrays.asList(pom).contains(fr.getId() + "") && !frsb)
                imaf.add("yes");
            else
                imaf.add("no");
        }
        String[] names = name.toArray(new String[name.size()]);
        String[] unames = uname.toArray(new String[uname.size()]);
        String[] imafs = imaf.toArray(new String[imaf.size()]);

        ArrayList<Bitmap> imgBitmap = new ArrayList<Bitmap>();
        String[] encImg;
        if (frsb)
            encImg = ((MainActivity)getActivity()).getFrReqSentImg().split("bratzna");
        else
            encImg = ((MainActivity)getActivity()).getImgResp().split("bratzna");
        for (int i = 0; i < encImg.length; i++)
        {
            byte[] decodedString = Base64.decode(encImg[i], Base64.DEFAULT);
            Bitmap bm = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imgBitmap.add(bm);
        }

        frAdap = new UserListAdapter(getActivity().getApplicationContext(), names, unames, imgBitmap, imafs);
        friendsList.setAdapter(frAdap);
        friendsList.setOnItemLongClickListener(friendClickListener);
        friendsList.setOnItemClickListener(userClickListener);

        //((MainActivity)getActivity()).setFrReqSent(false);
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

    private AdapterView.OnItemClickListener userClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int uid = friends.get(position).getId();
            String name = ((TextView)view.findViewById(R.id.tvl)).getText().toString();
            String uname = ((TextView)view.findViewById(R.id.tvm)).getText().toString();
            Intent i = new Intent(getActivity().getApplicationContext(), ProfileActivity.class);
            i.putExtra("id", uid);
            i.putExtra("name", name);
            i.putExtra("uname", uname);
            startActivity(i);
        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.frReq)
        {
            if (btn.getText().toString().equals("Zahtevi"))
            {
                JSONObject data = new JSONObject();
                try {
                    data.put("_id", userID);
                    data.put("mode", "reqUsers");
                } catch (JSONException e) { e.printStackTrace(); }
                LoginActivity.socket.emit("friendReqSent", data);
            }
            else
            {
                ((MainActivity)getActivity()).setFrReqSent(false);
                listFriends();
                btn.setText("Zahtevi");
                ((MainActivity)getActivity()).getSearchItem().setVisible(true);
            }
        }
    }
}
