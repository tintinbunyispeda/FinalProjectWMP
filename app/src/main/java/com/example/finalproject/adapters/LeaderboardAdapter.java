package com.example.finalproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.R;
import com.example.finalproject.models.LeaderboardModel;

import java.util.ArrayList;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderViewHolder> {

    private Context context;
    private ArrayList<LeaderboardModel> data;
    private String currentUnit = "pts"; // Default unit

    public LeaderboardAdapter(Context context, ArrayList<LeaderboardModel> data) {
        this.context = context;
        this.data = data;
    }

    // Method untuk update data
    public void setData(ArrayList<LeaderboardModel> newData) {
        this.data = newData;
        notifyDataSetChanged();
    }

    // Method untuk ganti unit (pts/mins)
    public void setUnit(String unit) {
        this.currentUnit = unit;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LeaderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_leaderboard, parent, false);
        return new LeaderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderViewHolder holder, int position) {
        LeaderboardModel model = data.get(position);

        holder.rank.setText("#" + model.getRank());
        holder.name.setText(model.getUsername());
        holder.score.setText(String.valueOf(model.getScore()));
        holder.level.setText("Lv. " + model.getLevel());

        // Set Unit secara dinamis
        holder.unit.setText(currentUnit);

        // Gambar Default
        holder.image.setImageResource(R.mipmap.ic_launcher_round);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class LeaderViewHolder extends RecyclerView.ViewHolder {

        TextView rank, name, score, level, unit;
        ImageView image;

        public LeaderViewHolder(@NonNull View itemView) {
            super(itemView);

            rank = itemView.findViewById(R.id.textViewRank);
            name = itemView.findViewById(R.id.textViewUsername);
            score = itemView.findViewById(R.id.textViewScore);
            level = itemView.findViewById(R.id.textViewLevel);
            image = itemView.findViewById(R.id.ivPlayerImage);
            unit = itemView.findViewById(R.id.tvScoreUnit); // ID baru
        }
    }
}