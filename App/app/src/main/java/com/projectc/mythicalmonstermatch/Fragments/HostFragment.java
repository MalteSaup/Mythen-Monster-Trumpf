package com.projectc.mythicalmonstermatch.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.projectc.mythicalmonstermatch.GameActivity;
import com.projectc.mythicalmonstermatch.R;

import java.util.ArrayList;

public class HostFragment extends Fragment {

    public RecyclerView playerRecyclerView;
    public RecyclerView.Adapter playerAdapter;

    Button button;

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
        playerRecyclerView.setAdapter(playerAdapter);
        Log.d("WAS", " "+ view.findViewById(R.id.ueberschrift));

        button = view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gA.update();
            }
        });

        super.onActivityCreated(saveInstandesState);
    }

    public void update(ArrayList<String> playerList){

        playerAdapter.notifyDataSetChanged();
    }


}

