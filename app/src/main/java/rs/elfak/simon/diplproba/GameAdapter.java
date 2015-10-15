package rs.elfak.simon.diplproba;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import java.util.ArrayList;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameAdapterHolder>
{
    ArrayList<Game> games;

    public GameAdapter(ArrayList<Game> games)
    {
        this.games = games;
    }

    public static class GameAdapterHolder extends RecyclerView.ViewHolder
    {
        CardView cv;
        TextView nameTV;
        TextView creatorTV;
        TextView dateTV;
        TextView addressTV;
        TextView comTV;
        /*EditText name;
        EditText creator;
        EditText date;
        EditText address;
        EditText com;*/

        GameAdapterHolder(View itemView)
        {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            nameTV = (TextView)itemView.findViewById(R.id.nameTV);
            creatorTV = (TextView)itemView.findViewById(R.id.creatorTV);
            dateTV = (TextView)itemView.findViewById(R.id.dataTV);
            addressTV = (TextView)itemView.findViewById(R.id.addressTV);
            comTV = (TextView)itemView.findViewById(R.id.comTV);
            /*name = (EditText)itemView.findViewById(R.id.name);
            creator = (EditText)itemView.findViewById(R.id.creator);
            date = (EditText)itemView.findViewById(R.id.date);
            address = (EditText)itemView.findViewById(R.id.address);
            com = (EditText)itemView.findViewById(R.id.com);*/
        }
    }

    @Override
    public GameAdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_list, parent, false);
        GameAdapterHolder gah = new GameAdapterHolder(v);
        return gah;
    }

    @Override
    public void onBindViewHolder(GameAdapterHolder holder, int position) {
        /*holder.name.setText(games.get(position).getName());
        holder.creator.setText(games.get(position).getCreator());
        holder.date.setText(games.get(position).getDate());
        holder.address.setText(games.get(position).getAddress());
        holder.com.setText(games.get(position).getDescription());*/
        holder.nameTV.setText(games.get(position).getName());
        holder.creatorTV.setText(games.get(position).getCreator());
        holder.dateTV.setText(games.get(position).getDate());
        holder.addressTV.setText(games.get(position).getAddress());
        holder.comTV.setText(games.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return games.size();
    }
}
