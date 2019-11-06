package com.projectc.mythicalmonstermatch;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.projectc.mythicalmonstermatch.Connection.Client;
import com.projectc.mythicalmonstermatch.Connection.Server;
import com.projectc.mythicalmonstermatch.Connection.ServerListener;
import com.projectc.mythicalmonstermatch.Fragments.FindFragment;
import com.projectc.mythicalmonstermatch.Fragments.HostFragment;

import java.util.ArrayList;

public class GameActivity extends FragmentActivity{

    public int code = 2;
    public String name = "";
    private CardClass[] cardDeck = new CardClass[30];
    public Server server;
    public Client client;
    public HostFragment hostFrag;

    private ArrayList<PlayerItem> playerItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        Bundle bundle = getIntent().getExtras();
        if (bundle.getString("USERNAME") != null) {
            this.name = bundle.getString("USERNAME");
        }
        if (bundle.getInt("CODE") == 0 || bundle.getInt("CODE") == 1) {
            this.code = bundle.getInt("CODE");
        }

        createCardDeck();

        if(code == 0){


            hostFrag = (HostFragment) Fragment.instantiate(this, HostFragment.class.getName(), null);

            hostFrag.playerAdapter = new PlayerAdapter(this, playerItems);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.gameActivityLayout, hostFrag);
            ft.commit();

            server = new Server(this.name, hostFrag);
            server.start();
            Log.d("SERVER STATUS", ""+server.running);

            client = createClient(this.name, this.name, "localhost");
            client.start();

            ArrayList<ServerListener> sL = server.getServerListeners();
            Log.d("SL", " "+sL.size() + " " + server);
            for(ServerListener sLL : sL){
                Log.d("SL", " " + sLL.getLogin());
            }

            //Server server = new Server (8080, 0, name);
            //server.start();
            //Client client = new Client("TODO", 8080, name, 1);
            //SERVER STARTEN
            //CLIENT STARTEN
            //LOBBY FRAGMENT STARTEN

        } else if(code == 1){
            //TODO

            FindFragment findFrag = (FindFragment) Fragment.instantiate(this, FindFragment.class.getName(), null);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.gameActivityLayout, findFrag);
            ft.commit();

            //FIND GAME FRAGMENT STARTEN
            //CLIENT STARTEN
        }


        Log.d("HALLO", " " + cardDeck.length + " " + code + " " + name);

//        public boolean onKeyDown(int keyCode, KeyEvent event) {
            /*if(keyCode == android.view.KeyEvent.KEYCODE_BACK && (mainFrag.isInMenu || mainFrag.isInCards)){
                /*if(!mainFrag.onCard){

                    return true;
                } else {
                    View frag = findViewById(R.id.fragment);
                    frag.setVisibility(View.GONE);
                    ImageButton close = findViewById(R.id.closeCard);
                    close.setVisibility(View.GONE);
                    mainFrag.onCard = false;
                    return true;
                }

            }*/
          //  return super.onKeyDown(keyCode, event);
        //}
    }

    @Override
    public void onDestroy() {

        if(server != null){
            server.closeServer();
            Client closeClient = new Client("localhost", "localhost", "localhost");
            closeClient.start();
        }

        super.onDestroy();

    }

    public Client createClient(String servername, String login, String address){
        return new Client(servername, login, address);
    }


    public void createCardDeck(){
        BitmapFactory bf = new BitmapFactory();
        Bitmap[] b = {
                bf.decodeResource(getResources(), R.drawable.image01),
                bf.decodeResource(getResources(), R.drawable.image02),
                bf.decodeResource(getResources(), R.drawable.image03),
                bf.decodeResource(getResources(), R.drawable.image04),
                bf.decodeResource(getResources(), R.drawable.image05),
                bf.decodeResource(getResources(), R.drawable.image06),
                bf.decodeResource(getResources(), R.drawable.image07),
                bf.decodeResource(getResources(), R.drawable.image08),
                bf.decodeResource(getResources(), R.drawable.image09)
        };
        for(int i = 0; i < 30; i++){
            cardDeck[i] = new CardClass(i, ("card" + i), i, i, i, i, i, b[i%9]);
        }

    }

    public void updateHostFragment() {

        if(hostFrag != null) {

            ArrayList<ServerListener> serverListeners = server.getServerListeners();
            if (serverListeners.size() != playerItems.size()) {
                if (serverListeners.size() > playerItems.size()) {
                    PlayerItem uebergabe = null;
                    for (ServerListener sL : serverListeners) {
                        boolean vorhanden = false;
                        for (PlayerItem pI : playerItems) {
                            if (pI.getUsername().equals(sL.getLogin())) {
                                vorhanden = true;
                            }
                        }
                        if (!vorhanden) {
                            uebergabe = new PlayerItem(sL.getLogin());
                            break;
                        }
                    }
                    if (uebergabe != null) {
                        playerItems.add(uebergabe);
                        hostFrag.playerAdapter.notifyItemInserted(playerItems.size() - 1);
                        updateHostFragment();
                    }
                } else if (serverListeners.size() < playerItems.size()) {
                    int uebergabe = 100;
                    for (int i = 0; i < playerItems.size(); i++) {
                        boolean vorhanden = false;
                        for (ServerListener sL : serverListeners) {
                            if (playerItems.get(i).getUsername().equals(sL.getLogin())) {
                                vorhanden = true;
                            }
                        }
                        if (!vorhanden) {
                            uebergabe = i;
                            break;
                        }
                    }
                    if (uebergabe != 100) {
                        playerItems.remove(uebergabe);
                        hostFrag.playerAdapter.notifyDataSetChanged();
                        updateHostFragment();
                    }
                }
            }
        }
    }

    public void updateClientHostFragment(){
        if(hostFrag != null){
            ArrayList<PlayerItem> playerItemUebergabe = client.playerItems;
             if(playerItemUebergabe.size() != playerItems.size()){
                if(playerItemUebergabe.size() > playerItems.size()){
                    ArrayList<PlayerItem> uebergabe = new ArrayList<>();
                    for(PlayerItem pI1 : playerItemUebergabe){
                        boolean vorhanden = false;
                        for(PlayerItem pI2 : playerItems){
                            if(pI1.getUsername().equals(pI2.getUsername())){
                                vorhanden = true;
                            }
                        }
                        if(!vorhanden){
                            uebergabe.add(pI1);
                        }
                    }
                    for(PlayerItem pI : uebergabe){
                        playerItems.add(pI);
                        hostFrag.playerAdapter.notifyItemInserted(playerItems.size()-1);
                    }
                }else if(playerItemUebergabe.size() < playerItems.size()){
                    ArrayList<Integer> uebergabe = new ArrayList<>();
                    for(int i = 0; i < playerItems.size(); i++){
                        boolean vorhanden = false;
                        for(PlayerItem pI2 : playerItemUebergabe){
                            if(playerItems.get(i).getUsername().equals(pI2.getUsername())){
                                vorhanden = true;
                            }
                        }
                        if(!vorhanden){
                            uebergabe.add(i);
                        }
                    }
                    for(int i : uebergabe){
                        playerItems.remove(i);
                        hostFrag.playerAdapter.notifyItemRemoved(i);
                    }
                }
            }
        }
    }
}

//TODO newWakeLock(int, String); => WakeLock.acquire() (zum starten) und wenn Activity close Wake.Lock.release() (beendet WakeLock, besser für Akku, aber notwendig um Netzwerkverbindung aufrecht zu halten)

/*
        mainFrag = (MainFragment) Fragment.instantiate(this, MainFragment.class.getName(), null);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.mainActivityLayout, mainFrag);
        ft.commit();
*/

/*




public void startFragment(int code){
        if(code == 1){
            HostFragment hostFrag = (HostFragment) Fragment.instantiate(getActivity(), HostFragment.class.getName(), null);

            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.gameActivityLayout, hostFrag);
            ft.commit();
        }else if(code == 0){
            FindFragment findFragment = (FindFragment) Fragment.instantiate(getActivity(), FindFragment.class.getName(), null);

            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.gameActivityLayout, findFragment);
            ft.commit();
        }

    }

 */