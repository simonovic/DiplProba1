package rs.elfak.simon.diplproba;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import java.io.File;
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
        setUpUI();

    }

    private void setUpUI()
    {
        fname = (EditText)findViewById(R.id.fname);
        lname = (EditText)findViewById(R.id.lname);
        email = (EditText)findViewById(R.id.email);
        uname = (EditText)findViewById(R.id.uname);
        upass = (EditText)findViewById(R.id.upass);
        imgView = (ImageView)findViewById(R.id.image);
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
                        //uname.setBackgroundColor(getResources().getColor(R.color.free));
                        //uname.setError("Slobodno");
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
        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 1)
        {
            try {
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                int nh = (int)(thumbnail.getHeight()*(2048.0/thumbnail.getWidth()));
                Bitmap scaled = Bitmap.createScaledBitmap(thumbnail, 2048, nh, true);
                imgView.setImageBitmap(scaled);
            }
            catch (Exception e)
            {
                Toast.makeText(this, "Neuspelo učitavanje slike!", Toast.LENGTH_LONG).show();
            }
        }
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
            Bitmap bm = ((BitmapDrawable)imgView.getDrawable()).getBitmap(); //BitmapFactory.decodeResource(getResources(), R.drawable.user);
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



