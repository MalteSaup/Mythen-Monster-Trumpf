package com.projectc.mythicalmonstermatch.Fragments;


import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.projectc.mythicalmonstermatch.GameActivity;
import com.projectc.mythicalmonstermatch.GameManager;
import com.projectc.mythicalmonstermatch.PlayerAdapter;
import com.projectc.mythicalmonstermatch.R;

import static android.content.Context.WIFI_SERVICE;

public class HostFragment extends Fragment {

    public RecyclerView playerRecyclerView;
    public RecyclerView.Adapter playerAdapter;

    private Handler handler;
    private Runnable runnable;

    private Button startButton;
    private Button cancelButton;

    private boolean stoped = false;
    private TextView connectionIP;


    public static GameManager manager;

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

        connectionIP = getView().findViewById(R.id.connectionIP);
        startButton = getView().findViewById(R.id.startButton);

        if(gA.code == 0){
            runnable = new Runnable() {
                @Override
                public void run() {
                    Log.d("JETZT SL", "" + gA.server.getServerListeners().size());
                    gA.updateHostFragment(gA.listenerToPlayerItem(gA.server.getServerListeners()));

                    WifiManager wifiManager = (WifiManager) gA.getApplicationContext().getSystemService(WIFI_SERVICE);              //Initialisierung WifiManager => IP Address erfahren
                    String address = "";
                    if(wifiManager != null) {
                        address = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());                 //IP-Address abfragen und in passende Form bringen
                    }
                    connectionIP.setText("Connected to IP: " + address);
                    if(!stoped){handler.postDelayed(this, 500);}
                }
            };
        } else{
            runnable = new Runnable() {
                @Override
                public void run() {
                    gA.updateHostFragment(gA.client.playerItems);
                    connectionIP.setText("Connected to IP: " + gA.client.getAddress());
                    startButton.setVisibility(View.GONE);
                    if(!stoped){handler.postDelayed(this, 500);}
                }
            };
        }
        runnable.run();


        startButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                // just for testing with one phone
                /*PlayerItem enemy = new PlayerItem("enemy", 123);
                gA.playerItems.add(enemy);
                gA.startGame();*/
                Log.d("SOLLSTART", "JETZT");
                gA.supportClass.sendMessage(gA.client,"start "+gA.name);
            }
        });

        cancelButton = getView().findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // TODO: Either shut down lobby completely and kick all the players or only remove the leaving player and give host to someone else
            }
        });

        super.onActivityCreated(saveInstandesState);
    }

    @Override
    public void onDestroy() {
        stoped = true;
        super.onDestroy();
    }

}

