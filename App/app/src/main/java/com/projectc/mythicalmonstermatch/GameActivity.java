package com.projectc.mythicalmonstermatch;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.projectc.mythicalmonstermatch.Connection.Client;
import com.projectc.mythicalmonstermatch.Connection.Server;

public class GameActivity extends FragmentActivity {

    private int code = 0;
    private String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);
        Bundle bundle = getIntent().getExtras();
        if (bundle.getString("USERNAME") != null) {
            this.name = bundle.getString("USERNAME");
        }
        if (bundle.getInt("CODE") == 0 || bundle.getInt("CODE") == 1) {
            this.code = code;
        }

        if(code == 0){
            //Server server = new Server (8080, 0, name);
            //server.start();
            //Client client = new Client("TODO", 8080, name, 1);
            //SERVER STARTEN
            //CLIENT STARTEN
            //LOBBY FRAGMENT STARTEN

        } else if(code == 1){
            //FIND GAME FRAGMENT STARTEN
            //CLIENT STARTEN
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