package rs.elfak.simon.diplproba;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.github.nkzawa.emitter.Emitter;
import org.json.JSONException;
import org.json.JSONObject;

////////////////////////////////////////////////////////////////////////////
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import ws.wamp.jawampa.*;
import ws.wamp.jawampa.connection.IWampConnectorProvider;
import ws.wamp.jawampa.transport.netty.NettyWampClientConnectorProvider;

public class ProfileActivity extends Activity
{
    int id;
    String name, uname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_profile);

        LoginActivity.socket.on("profImgReqResponse", onProfImgReqResponse);
        Bundle bundle = getIntent().getExtras();
        id = bundle.getInt("id");
        LoginActivity.socket.emit("profImg", id);

        EditText nameET = (EditText)findViewById(R.id.name);
        EditText unameET = (EditText)findViewById(R.id.uname);
        name = bundle.getString("name");
        uname = bundle.getString("uName");
        nameET.setText(name);
        unameET.setText(uname);
        //Button btn = (Button)findViewById(R.id.profBtn);
    }

    private Emitter.Listener onProfImgReqResponse = new Emitter.Listener() {
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
                    byte[] decodedString = Base64.decode(response, Base64.DEFAULT);
                    Bitmap bm = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    ImageView profImg = (ImageView)findViewById(R.id.img);
                    profImg.setImageBitmap(bm);
                }
            });
        }
    };
}
