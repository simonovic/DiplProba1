package rs.elfak.simon.diplproba;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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

        userID = shPref.getInt(Constants.userIDpref, 0);
        userName = shPref.getString(Constants.userNamepref,"false");
        if ((userID != 0) && (!userName.equals("false")))
        {
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("userID", userID);
            startActivity(i);
        }

        // testiranje slanja slika
        startActivity(new Intent(this, MainActivity.class));
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
                        editor.putInt(Constants.userIDpref, userID);
                        editor.putString(Constants.userNamepref, userName);
                        //editor.commit();
                        //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    } else
                    {
                        name.setText("");
                        pass.setText("");
                        Toast.makeText(getApplicationContext(), "Pogrešno korisničko ime i/ili lozinka!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        //editor.putInt(Constants.userIDpref, userID); //ovo je verovatno kad se odjavis
        //editor.commit();
        if ((userID != 0) && (!userName.equals("false")))
        {
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("userID", userID);
            startActivity(i);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.off("loginResponse", onLoginResponse);
        //socket.disconnect();
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
            if (n.equals(""))
                pass.setError("Morate uneti lozinku!");
        }
        else
        {
            JSONObject data = new JSONObject();
            try {
                data.put("user", n);
                data.put("pass", p);
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