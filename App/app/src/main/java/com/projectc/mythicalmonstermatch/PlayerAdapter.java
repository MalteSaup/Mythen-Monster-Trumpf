package com.projectc.mythicalmonstermatch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {

    private ArrayList<PlayerItem> playerList;
    private LayoutInflater layoutInflater;

    public PlayerAdapter(Context context, ArrayList<PlayerItem> playerList){
        this.playerList = playerList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public PlayerAdapter.PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = layoutInflater.inflate(R.layout.player_item, viewGroup, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerAdapter.PlayerViewHolder playerViewHolder, int i) {
        String username = playerList.get(i).getUsername();
        playerViewHolder.usernameTV.setText(username);
    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }

    public class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTV;
        ImageButton button;
        PlayerViewHolder(View itemView) {
            super(itemView);
            usernameTV = itemView.findViewById(R.id.usernameTextView);
        }


    }
}
