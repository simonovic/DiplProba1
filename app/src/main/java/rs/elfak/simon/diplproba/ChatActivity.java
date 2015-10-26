package rs.elfak.simon.diplproba;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ChatActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        String[] pom = {"sdads", "sdads", "sdads", "sdads", "sdads", "sdads", "sdads", "sdads", "sdads", "sdads", "sdads", "sdads", "sdads", "sdads", "sdads", "sdads", "sdads", "sdads", "sdads", "sdads", "sdads", "sdads"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, pom);
        ListView listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }

    public void onSendBtn(View v)
    {

    }
}
