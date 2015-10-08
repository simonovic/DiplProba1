package rs.elfak.simon.diplproba;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class UserListAdapter extends ArrayAdapter<String>
{
    Context context;
    String[] name;
    String[] uname;
    Integer[] img;

    public UserListAdapter(Context context, String[] name, String[] uname, Integer[] img)
    {
        super(context, R.layout.friend_list,name);
        this.context = context;
        this.name = name;
        this.uname = uname;
        this.img = img;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(R.layout.friend_list, null, true);

        TextView tvl = (TextView)rowView.findViewById(R.id.tvl);
        TextView tvm = (TextView)rowView.findViewById(R.id.tvm);
        ImageView imv = (ImageView)rowView.findViewById(R.id.imageView);

        tvl.setText(name[position]);
        tvm.setText(uname[position]);
        imv.setImageResource(R.drawable.user);
        return rowView;
    }
}
