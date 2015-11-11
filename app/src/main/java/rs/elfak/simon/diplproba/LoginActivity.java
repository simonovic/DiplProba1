package rs.elfak.simon.diplproba;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URISyntaxException;

public class LoginActivity extends Activity
{
    SharedPreferences shPref;
    SharedPreferences.Editor editor;
    private int userID;
    private String userName;
    EditText name;
    EditText pass;

    public static Socket socket;
    {
        try {
            socket = IO.socket(Constants.address);
        }
        catch (URISyntaxException e) {}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        shPref = getSharedPreferences(Constants.loginPref, Context.MODE_PRIVATE);
        editor = shPref.edit();
        socket.on("loginResponse", onLoginResponse);
        socket.connect();
        name =  (EditText)findViewById(R.id.name);
        pass  = (EditText)findViewById(R.id.pass);
    }

    private Emitter.Listener onLoginResponse = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String response;
                    JSONObject data = (JSONObject) args[0];
                    try {
                        response = data.getString("response");
                    } catch (JSONException e) {
                        return;
                    }

                    if (!response.equals("denied")) {
                        userID = Integer.parseInt(response);
                        editor.putInt(Constants.userIDpref, userID);
                        editor.putString(Constants.userNamepref, userName);
                        editor.commit();
                        name.setText("");
                        pass.setText("");
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    } else
                    {
                        name.setText("");
                        pass.setText("");
                        userName = "false";
                        Snackbar.make(findViewById(R.id.logLL), "Pogrešno korisničko ime i/ili lozinka!", Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        userID = shPref.getInt(Constants.userIDpref, 0);
        userName = shPref.getString(Constants.userNamepref,"false");
        if ((userID != 0) && (!userName.equals("false")))
            startActivity(new Intent(this, MainActivity.class));
        TextView a1 = (TextView)findViewById(R.id.a1);
        TextView a2 = (TextView)findViewById(R.id.a2);
        Typeface mFont = Typeface.createFromAsset(getAssets(), "harlow.ttf");
        a1.setTypeface(mFont);
        a2.setTypeface(mFont);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.off("loginResponse", onLoginResponse);
    }

    public void onLoginBtn(View v)
    {
        final EditText name = (EditText)findViewById(R.id.name);
        final EditText pass = (EditText)findViewById(R.id.pass);
        String n = userName = name.getText().toString().trim();
        String p = pass.getText().toString().trim();
        if (n.equals("") || p.equals(""))
        {
            if (n.equals(""))
                name.setError("Morate uneti korisničko ime!");
            if (p.equals(""))
                pass.setError("Morate uneti lozinku!");
        }
        else
        {
            JSONObject data = new JSONObject();
            try {
                data.put("uname", n);
                data.put("upass", p);
                data.put("mode", "login");
            } catch (JSONException e) { e.printStackTrace(); }
            socket.emit("loginRequest", data);
        }
    }

    public void onRegBtn(View v)
    {
        startActivity(new Intent(this, RegistrationActivity.class));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }
}