package com.projectc.mythicalmonstermatch;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;

import com.projectc.mythicalmonstermatch.Connection.Client;
import com.projectc.mythicalmonstermatch.Connection.Server;
import com.projectc.mythicalmonstermatch.Connection.ServerListener;
import com.projectc.mythicalmonstermatch.Fragments.FindFragment;
import com.projectc.mythicalmonstermatch.Fragments.GameFragment;
import com.projectc.mythicalmonstermatch.Fragments.HostFragment;

import java.util.ArrayList;

public class GameActivity extends FragmentActivity{

    public int code = 2;
    public String name = "";
    public CardClass[] cardDeck = new CardClass[30];
    public Server server;
    public Client client;
    public HostFragment hostFrag;
    public boolean inHost = false;
    public String servername;
    public String address;

    public int playerCount = -1;
    public int id = -1;

    public GameManager gameManager;

    private PowerManager.WakeLock wakeLock;

    public ArrayList<PlayerItem> playerItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "APP::WAKELOCK");
        wakeLock.acquire();

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

            inHost = true;

            hostFrag = (HostFragment) Fragment.instantiate(this, HostFragment.class.getName(), null);

            //hostFrag.playerAdapter = new PlayerAdapter(this, playerItems);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.gameActivityLayout, hostFrag);
            ft.commit();

            server = new Server(this.name, hostFrag);
            server.start();
            Log.d("SERVER STATUS", ""+server.running);
            client = null;
            client = new Client(this.name, this.name, "localhost");
            client.setGameActivity(this);
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
            //startFindFrag();
            GameFragment findFrag = (GameFragment) Fragment.instantiate(this, GameFragment.class.getName(), null);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.gameActivityLayout, findFrag);
            ft.commit();
            //FIND GAME FRAGMENT STARTEN
            //CLIENT STARTEN
        }
        else if(code == 2){

        }

    }

    @Override
    public void onDestroy() {
        if(client != null){   client.running = false;}
        if(server != null){
            //server.closeServer();

            Client closeClient = new Client("localhost", "localhost", "localhost");
            closeClient.running = false;
            closeClient.start();
        }
        wakeLock.release();
        super.onDestroy();

    }

    public Client createClient(String servername, String login, String address, int id){
        if(id == -1){return new Client(servername, login, address);}
        else{return new Client(servername, login, address, id);}
    }


    public void createCardDeck(){
        BitmapFactory bf = new BitmapFactory();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap[] b = {
                bf.decodeResource(getResources(), R.drawable.image01),
                bf.decodeResource(getResources(), R.drawable.image02),
                bf.decodeResource(getResources(), R.drawable.image03),
                bf.decodeResource(getResources(), R.drawable.image04),
                bf.decodeResource(getResources(), R.drawable.image05),
                bf.decodeResource(getResources(), R.drawable.image06),
                bf.decodeResource(getResources(), R.drawable.image07),
                bf.decodeResource(getResources(), R.drawable.image08),
                bf.decodeResource(getResources(), R.drawable.image09),
        };

        Integer[] imgIDs = {
                R.drawable.chimaere,
                R.drawable.dschinn,
                R.drawable.einhorn,
                R.drawable.medusa,
                R.drawable.minotaur,
                R.drawable.pegasus,
                R.drawable.satyr,
                R.drawable.zyklop
        };

        cardDeck[0] = new CardClass(0, "Chimäre", 4, 6, 6, 5, 7, imgIDs[0]);
        cardDeck[1] = new CardClass(1, "Dschinn", 1, 9 , 1, 10, 2, imgIDs[1]);
        cardDeck[2] = new CardClass(2, "Einhorn", 3, 2, 7 , 2, 1, imgIDs[2]);
        cardDeck[3] = new CardClass(3, "Medusa", 2, 8 , 3 , 7, 7, imgIDs[3]);
        cardDeck[4] = new CardClass(4, "Minotaur", 4, 3, 3, 3, 6, imgIDs[4]);
        cardDeck[5] = new CardClass(5, "Pegasus", 3, 3, 10, 2, 1, imgIDs[5]);
        cardDeck[6] = new CardClass(6, "Satyr", 2, 1, 5, 7, 2, imgIDs[6]);
        cardDeck[7] = new CardClass(7, "Zyklop", 5, 3, 2, 1, 4, imgIDs[7]);

        for(int i = 8; i < 30; i++){
            cardDeck[i] = new CardClass(i, ("card" + i), i, i, i, i, i, b[i%9]);
        }

    }

    public ArrayList<PlayerItem> listenerToPlayerItem(ArrayList<ServerListener> serverListeners){
        ArrayList<PlayerItem> uebergabe = new ArrayList<>();
        for(ServerListener sL : serverListeners){
            uebergabe.add(new PlayerItem(sL.getLogin(), sL.getID()));
        }
        //Log.d("JETZT", "size " +  uebergabe.size());
        return uebergabe;
    }

    public void updateHostFragment(ArrayList<PlayerItem> playerItemUebergabe) {
        if (hostFrag != null && hostFrag.playerAdapter != null && client.running) {
            //Log.d("JETZT NULL", " " + playerItemUebergabe.size() + " " + playerItems.size());
            if (playerItemUebergabe.size() != playerItems.size()) {
                if (playerItemUebergabe.size() > playerItems.size()) {
                    ArrayList<PlayerItem> uebergabe = new ArrayList<>();
                    for (PlayerItem pI1 : playerItemUebergabe) {
                        boolean vorhanden = false;
                        for (PlayerItem pI2 : playerItems) {
                            if (pI1.getUsername().equals(pI2.getUsername())) {
                                vorhanden = true;
                            }
                        }
                        if (!vorhanden) {
                            uebergabe.add(pI1);
                        }
                    }
                    for (PlayerItem pI : uebergabe) {
                        Log.d("JETZT ZAHL", "" + pI.getUsername());
                        playerItems.add(pI);
                        hostFrag.playerAdapter.notifyItemInserted(playerItems.size() - 1);
                    }
                } else if (playerItemUebergabe.size() < playerItems.size()) {
                    ArrayList<Integer> uebergabe = new ArrayList<>();
                    for (int i = 0; i < playerItems.size(); i++) {
                        boolean vorhanden = false;
                        for (PlayerItem pI2 : playerItemUebergabe) {
                            if (playerItems.get(i).getUsername().equals(pI2.getUsername())) {
                                vorhanden = true;
                            }
                        }
                        if (!vorhanden) {
                            uebergabe.add(i);
                        }
                    }
                    for (int i : uebergabe) {
                        try{
                            playerItems.remove(i);
                            hostFrag.playerAdapter.notifyItemRemoved(i);
                        } catch(IndexOutOfBoundsException e){
                            Log.d("JETZT", "KARTOFFEL");
                        }
                    }
                }
            }
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == android.view.KeyEvent.KEYCODE_BACK){
            if(inHost && code == 0){
                AsyncTask asyncTask = new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] objects) {
                        server.closeServer();
                        return null;
                    }
                };
                asyncTask.execute();
            } else if(inHost && code == 1){
                AsyncTask asyncTask = new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] objects) {
                        client.leave();
                        try {
                            client.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                };
                asyncTask.execute();
                startFindFrag();
                inHost = false;
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void reconnect() {
        if(code == 1 && servername != null && address != null){
            client = new Client(servername, name, address);
        }

    }

    public void startFindFrag(){
        FindFragment findFrag = (FindFragment) Fragment.instantiate(this, FindFragment.class.getName(), null);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.gameActivityLayout, findFrag);
        ft.commit();
    }

    private void startGame(){
        gameManager = new GameManager(cardDeck, playerItems);
    }

    public void submit(){
        //TODO START COMPARING PROCESS ON GAME LOGIC
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