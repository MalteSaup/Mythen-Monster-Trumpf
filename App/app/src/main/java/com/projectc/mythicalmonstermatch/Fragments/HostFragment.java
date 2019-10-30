package com.projectc.mythicalmonstermatch.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.projectc.mythicalmonstermatch.PlayerAdapter;
import com.projectc.mythicalmonstermatch.PlayerItem;
import com.projectc.mythicalmonstermatch.R;

import java.util.ArrayList;

public class HostFragment extends Fragment {

    private RecyclerView playerRecyclerView;
    private RecyclerView.Adapter playerAdapter;
    private RecyclerView.LayoutManager playerLayoutManager;

    private ArrayList<PlayerItem> playerList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_host, container, false);
    }

    @Override
    public void onActivityCreated(Bundle saveInstandesState) {
        View view = getView();
        playerList = new ArrayList<>();
        playerList.add(new PlayerItem("Hans"));
        playerList.add(new PlayerItem("John"));
        playerList.add(new PlayerItem("Jürgen"));
        playerList.add(new PlayerItem("Glürgen"));
        playerList.add(new PlayerItem("Dürgen"));
        playerRecyclerView = view.findViewById(R.id.playerView);
        playerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        playerAdapter = new PlayerAdapter(getContext(), playerList);
        playerRecyclerView.setAdapter(playerAdapter);
        Log.d("WAS", " "+ view.findViewById(R.id.ueberschrift));


        super.onActivityCreated(saveInstandesState);
    }

    public void addPlayer(String username){
        playerList.add(new PlayerItem(username));
    }

    public void removePlayer(String username){
        for(int i = 0; i < playerList.size(); i++){
            if(playerList.get(i).getUsername().equals(username)){
                playerList.remove(i);
                break;
            }
        }
    }
}

