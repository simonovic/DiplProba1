package rs.elfak.simon.diplproba;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URISyntaxException;

public class RegistrationActivity extends AppCompatActivity {

    private boolean uName = false;
    private int userID;
    private String userName;
    EditText fname, lname, email, uname, upass;
    SharedPreferences shPref;
    SharedPreferences.Editor editor;
    ImageView imgView;

    private static Socket socket;
    {
        try {
            socket = IO.socket(Constants.address);
        }
        catch (URISyntaxException e) {}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        socket.on("registrationResponse", onRegResponse);
        socket.on("chUnameResponse", onChPassResponse);
        fname = (EditText)findViewById(R.id.fname);
        lname = (EditText)findViewById(R.id.lname);
        email = (EditText)findViewById(R.id.email);
        uname = (EditText)findViewById(R.id.uname);
        upass = (EditText)findViewById(R.id.upass);
        shPref = getSharedPreferences(Constants.loginPref, Context.MODE_PRIVATE);
        editor = shPref.edit();

        uname.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Toast.makeText(getApplicationContext(), "Kucanje!", Toast.LENGTH_SHORT).show();
                String up = uname.getText().toString();
                if (up.length() < 6) {
                    uname.setError("Minimum 6 karaktera!");
                    uName = false;
                }
                else
                    socket.emit("checkUname", up);
            }

            @Override
            public void afterTextChanged(Editable s) { }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //LoginActivity.socket.off("registrationResponse", onRegResponse);
        //LoginActivity.socket.off("chPassResponse", onChPassResponse);
    }

    private Emitter.Listener onChPassResponse = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String response;
                    JSONObject data = (JSONObject) args[0];
                    try {
                        response = data.getString("response");
                    } catch (JSONException e) {  return; }

                    if (response.equals("busy"))
                    {
                        uname.setError("Zauzato");
                        uName = false;
                    }
                    else if (response.equals("free")) {
                        uName = true;
                        uname.setError("Slobodno");
                    }
                }
            });
        }
    };

    private Emitter.Listener onRegResponse = new Emitter.Listener() {
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

                    if (response.equals("failed")) {
                        Toast.makeText(getApplicationContext(), "Neuspelo kreiranje naloga, pokušajte ponovo!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        userID = Integer.parseInt(response);
                        editor.putInt(Constants.userIDpref, userID);
                        editor.putString(Constants.userNamepref, userName);
                        //editor.commit();
                        fname.setText("");
                        lname.setText("");
                        email.setText("");
                        uname.setText("");
                        upass.setText("");
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                }
            });
        }
    };

    public void onImageClk(View v)
    {
        Toast.makeText(this, "Klik na sliku!", Toast.LENGTH_SHORT).show();
    }

    public void onCreateBtn(View v)
    {
        String fn, ln, em, un, up = "";
        fn = fname.getText().toString();
        ln = lname.getText().toString();
        em = email.getText().toString();
        un = uname.getText().toString();
        up = upass.getText().toString();

        if (fn.isEmpty() || ln.isEmpty() || em.isEmpty() || un.isEmpty() || up.isEmpty()) {
            Toast.makeText(this, "Popunite sva polja!", Toast.LENGTH_SHORT).show();
        }
        else if (!uName) {
            Toast.makeText(this, "Nevalidno korisničko ime!", Toast.LENGTH_SHORT).show();
        }
        else {
            userName  = un;
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.user);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT); //radi i bez ovog
            JSONObject data = new JSONObject();
            try {
                data.put("fname", fn);
                data.put("lname", ln);
                data.put("email", em);
                data.put("uname", un);
                data.put("upass", up);
                data.put("buff", encoded);
            } catch (JSONException e) { e.printStackTrace(); }
            LoginActivity.socket.emit("registrationRequest", data);
        }
    }
}



