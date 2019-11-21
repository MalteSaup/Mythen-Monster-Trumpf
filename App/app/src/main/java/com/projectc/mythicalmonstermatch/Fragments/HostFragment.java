package com.projectc.mythicalmonstermatch.Fragments;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.projectc.mythicalmonstermatch.GameActivity;
import com.projectc.mythicalmonstermatch.PlayerAdapter;
import com.projectc.mythicalmonstermatch.R;

public class HostFragment extends Fragment {

    public RecyclerView playerRecyclerView;
    public RecyclerView.Adapter playerAdapter;

    private Handler handler;
    private Runnable runnable;


    private boolean stoped = false;

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
        final GameActivity gA = (GameActivity) getActivity();
        View view = getView();
        playerRecyclerView = view.findViewById(R.id.playerView);
        playerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        playerAdapter = new PlayerAdapter(gA, gA.playerItems);
        playerRecyclerView.setAdapter(playerAdapter);
        Log.d("WAS", " "+ view.findViewById(R.id.ueberschrift));

        handler = new Handler();
        if(gA.code == 0){
            runnable = new Runnable() {
                @Override
                public void run() {
                    Log.d("JETZT SL", "" + gA.server.getServerListeners().size());
                    gA.updateHostFragment(gA.listenerToPlayerItem(gA.server.getServerListeners()));
                    if(!stoped){handler.postDelayed(this, 500);}
                }
            };
        } else{
            runnable = new Runnable() {
                @Override
                public void run() {
                    gA.updateHostFragment(gA.client.playerItems);
                    if(!stoped){handler.postDelayed(this, 500);}
                }
            };
        }
        runnable.run();

        super.onActivityCreated(saveInstandesState);
    }

    @Override
    public void onDestroy() {
        stoped = true;
        super.onDestroy();
    }

}

