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

    /////////////////////////////////////////
    WampClient client;
    WampClient.State state;
    Subscription addProcSubscription;
    Subscription counterPublication;
    Subscription onHelloSubscription;
    Scheduler rxScheduler;
    int counter = 0;
    ////////////////////////////////////////

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
        uname = bundle.getString("uname");
        nameET.setText(name);
        unameET.setText(uname);
        //Button btn = (Button)findViewById(R.id.profBtn);


        /////////////////////////////////////////
        connectWampClient();
        ////////////////////////////////////////
    }

    public void connectWampClient()
    {
        IWampConnectorProvider connectorProvider = new NettyWampClientConnectorProvider();
        WampClientBuilder builder = new WampClientBuilder();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        rxScheduler = Schedulers.from(executor);

        try {
            builder.withUri("ws://192.168.1.8:9000/ws")
                    .withRealm("realm1")
                    .withInfiniteReconnects()
                    .withCloseOnErrors(true)
                    .withReconnectInterval(5, TimeUnit.SECONDS)
                    .withConnectorProvider(connectorProvider);
            client = builder.build();
        } catch (WampError e) {
            e.printStackTrace();
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }

        client.statusChanged()
                .observeOn(rxScheduler)
                .subscribe(new Action1<WampClient.State>() {
                    @Override
                    public void call(WampClient.State t1) {
                        Log.d("wamp", "Session status changed to " + t1);
                        if (t1 instanceof WampClient.ConnectedState) {
                            Log.d("wamp", "connectedStateMethods call");
                            state = t1;
                            connectedStateMethods();
                        }
                        else if (t1 instanceof WampClient.DisconnectedState) {
                            Log.d("wamp", "closeSubscriptions call");
                            state = t1;
                            closeSubscriptions();
                        }else if (t1 instanceof WampClient.ConnectingState) {
                            // Client starts connecting to the remote router
                            Log.d("wamp", "ConnectingState");
                            state = t1;
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable t) {
                        Log.d("wamp", "Session ended with error " + t);
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        Log.d("wamp", "Session ended normally");
                    }
                });
        client.open();
    }

    private void connectedStateMethods()
    {
        onHelloSubscription  = client.makeSubscription("com.example.onhello", String.class)
                .observeOn(rxScheduler)
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String msg) {
                        Log.d("wamp","event for 'onhello' received: " + msg);
                        Toast.makeText(ProfileActivity.this, "Event for 'onhello' received: "+msg, Toast.LENGTH_LONG).show();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable e) {
                        Log.d("wamp","failed to subscribe 'onhello': " + e);
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        Log.d("wamp","'onhello' subscription ended");
                    }
                });
    }

    private void closeSubscriptions() {
        if (onHelloSubscription != null)
            onHelloSubscription.unsubscribe();
        onHelloSubscription = null;
        if (counterPublication != null)
            counterPublication.unsubscribe();
        counterPublication = null;
        if (addProcSubscription != null)
            addProcSubscription.unsubscribe();
        addProcSubscription = null;
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

    public void onProfBtn(View v)
    {
        if (state instanceof WampClient.ConnectedState)
        {
            if (counter%2==0)
            {
                client.call("com.example.add", Long.class, counter, 2)
                        .observeOn(rxScheduler)
                        .subscribe(new Action1<Long>() {
                            @Override
                            public void call(Long result) {
                                Log.d("wamp", "add() called with result: " + result);
                                Toast.makeText(ProfileActivity.this, "add() result from nodejs: "+result, Toast.LENGTH_SHORT).show();
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable e) {
                                boolean isProcMissingError = false;
                                if (e instanceof ApplicationError) {
                                    if (((ApplicationError) e).uri().equals("wamp.error.no_such_procedure"))
                                        isProcMissingError = true;
                                }
                                if (!isProcMissingError) {
                                    Log.d("wamp", "call of mul2() failed: " + e);
                                }
                            }
                        });
            }
            else
            {
                client.publish("com.example.onhello", "Poruka od klijenta!")
                        .observeOn(rxScheduler)
                        .subscribe(new Action1<Long>() {
                            @Override
                            public void call(Long t1) {
                                System.out.println("published to 'oncounter' with counter " + "Poruka od klijenta!");
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable e) {
                                System.out.println("Error during publishing to 'oncounter': " + e);
                            }
                        });
            }
            counter++;
        }
    }
}
