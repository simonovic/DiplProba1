package rs.elfak.simon.diplproba;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ChatListAdapter extends ArrayAdapter<String>
{
    Context context;
    String[] uname;
    String[] time;
    String[] message;

    public ChatListAdapter(Context context, String[] uname, String[] time, String[] message)
    {
        super(context, R.layout.chat_list, uname);
        this.context = context;
        this.uname = uname;
        this.time = time;
        this.message = message;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(R.layout.chat_list, null, true);

        TextView unameTV =  (TextView)rowView.findViewById(R.id.unameTV);
        TextView timeTV =  (TextView)rowView.findViewById(R.id.timeTV);
        TextView messTV =  (TextView)rowView.findViewById(R.id.messTV);

        unameTV.setText(uname[position]);
        timeTV.setText(time[position]);
        messTV.setText(message[position]);

        return rowView;
    }
}
