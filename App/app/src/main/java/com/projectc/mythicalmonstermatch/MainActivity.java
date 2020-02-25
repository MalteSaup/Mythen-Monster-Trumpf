package com.projectc.mythicalmonstermatch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.projectc.mythicalmonstermatch.Fragments.MainFragment;

public class MainActivity extends FragmentActivity {

    public MainFragment mainFrag;                                                                   //DAS MAIN FRAGMENT ENTHÄLT DAS MENÜ UND BUTTONS
    public String name = "";                                                                        //SPIELER NAME
    public CardClass[] cardDeck;                                                                   //KARTEN DECK
    WifiManager wifiManager;                                                                        //WIFI MANAGER FÜR CHECK OB WLAN VERBINDUNG STEHT
    private SharedPreferences data;                                                                 //GESPEICHERTE DATEN DAMIT USERNAME AUCH NACH NEUSTART DER APP VORHANDEN IST
    private SharedPreferences.Editor data_editor;                                                   //EDITOR FÜR GESPEICHERTE DATEN

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifiManager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);          //INITIALISIERT WIFI MANAGER


        mainFrag = (MainFragment) Fragment.instantiate(this, MainFragment.class.getName(), null);   //INITIALISIERT MAIN FRAGMENT


        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();                    //STARTET MAIN FRAGMENT
        ft.add(R.id.mainActivityLayout, mainFrag);
        ft.commit();

        createCardDeck();                                                                           //KARTEN DECK WIRD ERSTELLT, BENÖTIGT FÜR SHOWCARDFRAGMENT

        
        data = getApplicationContext().getSharedPreferences("user_name", 0);                  //GESPEICHERTE DATEN WERDEN INITIALISIERT

        if(data.getString("user_name", null) != null){
            name = data.getString("user_name", null);                                        //FALLS DATEN VORHANDEN => AUSGELESEN UND NAME GESETZT
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && (mainFrag.isInMenu)){                                //CHECKT OB DER RETURN TASTE GEDRÜCKT WURDE UND IN WELCHEM MENÜ GERADE
            if(!mainFrag.onCard){                                                                   //WENN IM SHOWCARDFRAGMENT (OHNE GEÖFFNET KARTE) ODER MENUFRAGMENT => RETURN ZUM MAINFRAGMENT
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.mainActivityLayout, mainFrag);
                ft.commit();
                mainFrag.isInMenu = false;
                return true;
            } else {                                                                                //WENN IM SHOWCARDFRAGMENT UND KARTE GEÖFFNET => KARTE GESCHLOSSEN
                View frag = findViewById(R.id.fragment);
                frag.setVisibility(View.GONE);                                                      //DEAKTIVIERUNG KARTE
                Button close = findViewById(R.id.closeCard);
                close.setVisibility(View.GONE);                                                     //DEAKTIVIERUNG SCHLIEß BUTTON FÜR KARTE
                mainFrag.onCard = false;                                                            //ONCARD FLAG WIEDER AUF FALSCH GESETZT
                return true;
            }

        }
        return super.onKeyDown(keyCode, event);                                                     //FALLS IN KEINEM MENÜ PUNKT => APP GESCHLOSSEN
    }

    public String getName(){                                                                        //GIBT NAMEN ZURÜCK
        return name;
    }

    public void setName(String name){                                                               //SETZT UND SPEICHERT NAMEN
        this.name = name;
        data_editor = data.edit();
        data_editor.putString("user_name", name);
        data_editor.apply();
    }
    public CardClass[] getCardDeck(){return  cardDeck;}                                             //GIBT KARTEN DECK ZURÜCK

    public void createCardDeck(){
        CardSupportClass cSS = new CardSupportClass(this);
        cardDeck = cSS.createDeck();
    }

    public void startGameActivity(int code){                                                        //STARTET GAME ACTIVITY

        if(checkWiFiState() && nameIsSet()){                                                        //CHECKT OB WLAN AKITVIERT UND VERBUNDEN, UND OB NAME GESETZT IST
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("USERNAME", this.name);                                           //ÜBERGIBT NAMEN AN NEUE ACTIVITY
            intent.putExtra("CODE", code);                                                    //ÜBERGIBT CODE AN NEUE ACTIVTY: 0 == Host, 1 == Client

            startActivity(intent);
        } else if(!checkWiFiState()){                                                               //FALLS WLAN NICHT AKTIVIERT ODER VERBUNDE GIBT MELDUNG IN Toast AUS
            createToast("WLAN Verbindung nicht vorhanden");
        } else if(!nameIsSet()){
            createToast("Name noch nicht gesetzt");                                          //FALLS USERNAME NICHT GESETZT GIBT MELDUNG IN Toast AUS
        }
    }

    private void createToast(CharSequence inhalt){                                                  //ERSTELLT UND ZEIGT TOAST
        Toast toast = Toast.makeText(this, inhalt, Toast.LENGTH_SHORT);
        toast.show();
    }

    private boolean nameIsSet() {                                                                   //CHECKT OB USERNAME GESETZT
        if(!name.trim().isEmpty()){return true;}
        return false;
    }

    private boolean checkWiFiState() {                                                              //CHECKT OB WLAN AKTIVIERT UND VERBUNDEN
        if(wifiManager.getWifiState() == 3 && wifiManager.getConnectionInfo().getSupplicantState().toString().equalsIgnoreCase("COMPLETED")){return true;}
        return true;
    }
}
