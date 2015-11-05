package rs.elfak.simon.diplproba;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

        GameAdapterHolder(View itemView)
        {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            nameTV = (TextView)itemView.findViewById(R.id.nameTV);
            creatorTV = (TextView)itemView.findViewById(R.id.creatorTV);
            addressTV = (TextView)itemView.findViewById(R.id.addressTV);
            dateTV = (TextView)itemView.findViewById(R.id.dataTV);
        }
    }

    @Override
    public GameAdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_list, parent, false);
        GameAdapterHolder gah = new GameAdapterHolder(v);
        return gah;
    }

    @Override
    public void onBindViewHolder(GameAdapterHolder holder, final int position) {
        holder.nameTV.setText(games.get(position).getName());
        holder.creatorTV.setText(games.get(position).getCreator());
        holder.dateTV.setText(games.get(position).getDt());
        holder.addressTV.setText(games.get(position).getAddress());
        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getContext().startActivity((new Intent(v.getContext(), GameActivity.class)).putExtra("pos", position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return games.size();
    }
}
