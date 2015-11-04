package rs.elfak.simon.diplproba;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity
{
    ListView listView;
    List<String> unameL1, timeL1, messageL1;
    int userID, gameID;
    SharedPreferences shPref;
    String role, uname;
    EditText messEt;
    ChatListAdapter clAdap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messEt = (EditText)findViewById(R.id.messET);
        unameL1 = new ArrayList<String>();
        timeL1 = new ArrayList<String>();
        messageL1 = new ArrayList<String>();
        shPref = getSharedPreferences(Constants.loginPref, Context.MODE_PRIVATE);
        userID = shPref.getInt(Constants.userIDpref, 0);
        gameID = shPref.getInt(Constants.gameIDpref, 0);
        uname = shPref.getString(Constants.userNamepref, "false");
        role = shPref.getString(Constants.rolePref, "");
        listView = (ListView)findViewById(R.id.listView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LoginActivity.socket.on("gameOnResponse", onGameOnResponse);
        unameL1 = MapActivity.unameL;
        timeL1 = MapActivity.timeL;
        messageL1 = MapActivity.messageL;
        String[] unames = unameL1.toArray(new String[unameL1.size()]);
        String[] times = timeL1.toArray(new String[timeL1.size()]);
        String[] messages = messageL1.toArray(new String[messageL1.size()]);
        clAdap = new ChatListAdapter(getApplicationContext(), unames, times, messages);
        listView.setAdapter(clAdap);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LoginActivity.socket.off("gameOnResponse", onGameOnResponse);
    }

    private Emitter.Listener onGameOnResponse = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String response;
                    JSONObject data = (JSONObject) args[0];
                    try {
                        response = data.getString("response");
                    } catch (JSONException e) { return; }
                    if (response.equals("chat")) {
                        String uname, time, mess;
                        try {
                            uname = data.getString("uname");
                            time = data.getString("time");
                            mess = data.getString("message");
                        } catch (JSONException e) { return; }
                        unameL1.add(uname);
                        timeL1.add(time);
                        messageL1.add(mess);
                        String[] unames = unameL1.toArray(new String[unameL1.size()]);
                        String[] times = timeL1.toArray(new String[timeL1.size()]);
                        String[] messages = messageL1.toArray(new String[messageL1.size()]);
                        clAdap = new ChatListAdapter(getApplicationContext(), unames, times, messages);
                        listView.setAdapter(clAdap);
                    }
                }
            });
        }
    };

    public void onSendBtn(View v)
    {
        String mess = messEt.getText().toString();
        if (!mess.equals(""))
        {
            SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
            String time = sf.format(new Date());
            JSONObject data = new JSONObject();
            try {
                data.put("id", userID);
                data.put("gameID", gameID);
                data.put("uname", uname);
                data.put("time", time);
                data.put("mess", mess);
                data.put("role", role);
                data.put("mode", "chat");
            } catch (JSONException e) { e.printStackTrace(); }
            LoginActivity.socket.emit("gameOn", data);
            messEt.setText("");
            messEt.clearFocus();
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //NavUtils.navigateUpFromSameTask(this);
        //return true;
        return super.onOptionsItemSelected(item);
    }
}
