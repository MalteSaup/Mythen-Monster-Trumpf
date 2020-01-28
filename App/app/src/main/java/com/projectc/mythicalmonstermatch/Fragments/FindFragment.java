package com.projectc.mythicalmonstermatch.Fragments;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.projectc.mythicalmonstermatch.Connection.SearchClient;
import com.projectc.mythicalmonstermatch.GameActivity;
import com.projectc.mythicalmonstermatch.R;
import com.projectc.mythicalmonstermatch.ServerAdapter;
import com.projectc.mythicalmonstermatch.ServerItem;

import java.util.ArrayList;

import static android.content.Context.WIFI_SERVICE;


public class FindFragment extends Fragment {

    private RecyclerView serverRecyclerView;
    private ServerAdapter serverAdapter;

    private ArrayList<ServerItem> serverList;
    public ArrayList<ServerItem> uebergabeArray = new ArrayList<>();

    private Handler handler;
    private Runnable runnable;

    private boolean stoped = false;
    private boolean searching = true;

    private GameActivity gA;

    @Override
    public void onDestroy() {
        searching = false;
        super.onDestroy();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find, container, false);
    }

    @Override
    public void onActivityCreated(Bundle saveInstandesState) {
        final View view = getView();

        gA = (GameActivity)getActivity();

        //if(gA.client != null){gA.client = null;}
        serverList = new ArrayList<>();
        serverRecyclerView = view.findViewById(R.id.serverView);
        serverRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        serverAdapter = new ServerAdapter(getContext(), serverList);
        serverRecyclerView.setAdapter(serverAdapter);

        serverAdapter.setOnItemClickListener(new ServerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ServerItem joinItem = serverList.get(position);
                searching = false;
                gA.inHost = true; 
                join(joinItem);
                Log.d("JETZT ITEM", serverList.get(position).getServername());
            }
        });

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                updateFindFragment();
                if(!stoped){handler.postDelayed(this, 200);}
            }
        };
        runnable.run();

        final Context context = getContext();

        WifiManager wifiManager = (WifiManager) gA.getApplicationContext().getSystemService(WIFI_SERVICE);             //Initialisierung WifiManager => IP Address erfahren
        if(wifiManager != null){
            String address = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());        //IP-Address abfragen und in passende Form bringen
            Log.d("JETZT CIWAN", address);
            String[] addressUebergabe = address.split("\\.");                                              //Split IP-Address in Zahlenblöcke
            final String addressSeed = addressUebergabe[0] + "." + addressUebergabe[1] + ".";
            final int start3Stelle = Integer.parseInt(addressUebergabe[2]);

            final FindFragment fF = this;



            Thread searchThread = new Thread(new Runnable() {
                int counting3 = start3Stelle;
                int counting4 = 2;

                public void restart(){
                    while(serverList.size() > 0){
                        serverList.remove(0);
                        Log.d("WHILE SCHLEIFE", "1");
                    }
                    serverAdapter.notifyDataSetChanged();
                    serverAdapter.notifyDataSetChanged();
                    counting3 = start3Stelle;
                    counting4 = 1;
                }

                @Override
                public void run() {
                    boolean reset = false;

                    Button button = getActivity().findViewById(R.id.refreshButton);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            restart();
                        }
                    });


                    while(searching){
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        final SearchClient searchClient = new SearchClient(fF, context, addressSeed  + counting3 + "." + counting4);
                        searchClient.start();
                        try {
                            searchClient.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(counting4 < 255){
                            counting4++;
                        }else if(counting3 < 255){
                            counting3++;
                            if(counting4 == start3Stelle){
                                counting4++;
                            }
                            counting4 = 2;
                            if(!reset){
                                counting3 = 1;
                                reset = true;
                            }
                        } else{
                            counting3 = 1;
                            counting4 = 2;
                        }

                    }
                }
            });
            searchThread.start();



        }
        else{
            super.onDestroy();
        }

        /*serverList.add(new ServerItem("hans", 4));
        serverList.add(new ServerItem("Glürgen", 4));
        serverList.add(new ServerItem("Dürben", 4));
        serverList.add(new ServerItem("Türben", 4));
        serverAdapter.notifyDataSetChanged();*/
        super.onActivityCreated(saveInstandesState);
    }

    private void updateFindFragment() {
        ArrayList<ServerItem> uebergabe = new ArrayList<>();
        if(uebergabeArray.size() > 0){
            uebergabe = uebergabeArray;
            uebergabeArray = new ArrayList<>();
            for(ServerItem sI : uebergabe){
                serverList.add(sI);
                serverAdapter.notifyItemInserted(serverList.size()-1);
            }
        }
    }

    public void join(ServerItem joinItem){
        gA.servername = joinItem.getServername();
        gA.address = joinItem.getAddress();
        gA.client = gA.createClient(joinItem.getServername(), gA.name, joinItem.getAddress(), gA.id);
        gA.client.setGameActivity(gA);
        gA.client.start();

        gA.inHost = true;

        HostFragment hostFrag = (HostFragment) Fragment.instantiate(getContext(), HostFragment.class.getName(), null);

        gA.hostFrag = hostFrag;

        FragmentTransaction ft = gA.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.gameActivityLayout, hostFrag);
        ft.commit();
    }
}
