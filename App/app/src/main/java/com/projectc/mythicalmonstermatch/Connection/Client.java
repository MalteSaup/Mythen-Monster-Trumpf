package com.projectc.mythicalmonstermatch.Connection;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.projectc.mythicalmonstermatch.Fragments.FindFragment;
import com.projectc.mythicalmonstermatch.GameActivity;
import com.projectc.mythicalmonstermatch.PlayerItem;
import com.projectc.mythicalmonstermatch.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;

public class Client extends Thread{

    private Context context;

    private String address;                                                                         //Network IP Addresse fürs Verbinden
    private String serverName;                                                                      //???
    private String login;                                                                           //Username

    private boolean gameStarted = false;                                                            //Zeigt an ob das Spiel gestartet wurde
    private boolean joined = false;
    private boolean aknowleagead = true;
    private boolean serverRunning = true;

    private Socket socket;                                                                          //Socket

    private ArrayList<Integer> cardList = new ArrayList<>();                                        //Liste an Karten die im Deck sind

    private OutputStream outputStream;                                                              //Für Kommunikation zwischen Client und Server
    private InputStream inputStream;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    private GameActivity gameActivity;

    public ArrayList<PlayerItem> playerItems = new ArrayList<>();


    public Client (String serverName, String login, String address) {
        this.serverName = serverName;
        this.login = login;
    }

    @Override
    public void run(){
        connect(address);
    }

    private void sendMessage(String msg) {                                                          //Funktion fürs Senden von Nachrichten an Server
        try {
            Log.d("CLIENT", msg);
            bufferedWriter.write(msg + "\r\n");                                                 //Message wird erstellt
            bufferedWriter.flush();                                                                 //Message wird zum Server gesendet
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessageToAll(String msg){
        //TODO Message an alle CMD überlegen und integrieren
    }


    public void connect(String address) {
            try {
                socket = new Socket(address, 8080);                              //Verbindung auf Server wird hergestellt
                inputStream = socket.getInputStream();                                              //Kriegt In- und Outputstream und versieht diese mit Buffern fürs Lesen und Schreiben für Kommunikation
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                outputStream = socket.getOutputStream();
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

                //WICHTIG \r\n am Ende der Zeile sonst nicht gelesen

                String line;

                sendMessage("join " + login);

                joined = true;

                while((line = bufferedReader.readLine()) != null && serverRunning){                                  //While Schleife für Nachrichten verarbeitung
                    String[] tokens = line.split(" ");                                        //Splited Nachricht auf. 1 Nachrichten Block ist Command Token
                    if(tokens != null && tokens.length > 0) {
                        String cmd = tokens[0];
                        if("denied".equalsIgnoreCase(cmd)){                                         //Wenn Denied Command (Nachfolgenden IF Statements dasselbe bloß anderes Command mit anderer Funktion)
                            handleDenie();
                        } else if("accept".equalsIgnoreCase(cmd)){
                            handleAccept();
                        } else if("start".equalsIgnoreCase(cmd)){
                            handleStart(tokens);
                        } else if("setName".equalsIgnoreCase(cmd)){
                            tokens = line.split(" ", 2);                                //Erneute splitung von String da Nachricht im Inhaltsblock Leerzeichen enthalten darf
                            handleNameChange(tokens[1]);
                        } else if("aknowledge".equalsIgnoreCase(cmd)){
                            aknowleagead = true;
                        } else if("closing".equalsIgnoreCase(cmd)){
                            handleClosing();
                        } else if("playeranswer".equalsIgnoreCase(cmd)){
                            tokens = line.split(";");
                            handlePlayerAnswer(tokens);
                        } else if("playeradded".equalsIgnoreCase(cmd)){
                            tokens = line.split(";");
                            handlePlayerAdded(tokens);
                        } else if("playerremoved".equalsIgnoreCase(cmd)){
                            tokens = line.split(";");
                            handlePlayerRemoved(tokens);
                        }
                    }
                }
                Log.d("CLIENT", "ZUENDDE");

            } catch (ConnectException e){
                Log.d("ERROR", "CONNECTION FAILED");
                //TODO SERVER NICHT MEHR VORHANDEN NACHRICHT WENN NOCH NICHT GEJOINED
                if(joined){
                    //TODO SERVER CONNECTION CLOSED NACHRICHT
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.d("ERROR", "UFF");
                e.printStackTrace();
            }
    }

    private void handlePlayerRemoved(String[] tokens) {
        ArrayList<Integer> uebergabe = new ArrayList<>();
        for(int i = 0; i < playerItems.size(); i++){
            boolean vorhanden = false;
            for(int o = 1; o < tokens.length; o++){
                if(tokens[o].equals(playerItems.get(i).getUsername())){
                    vorhanden = true;
                }
            }
            if(!vorhanden){
                uebergabe.add(i);
            }
        }
        for(int i : uebergabe) {
            playerItems.remove(i);
        }
    }

    private void handlePlayerAdded(String[] tokens) {
        ArrayList<PlayerItem> pIs = new ArrayList<>();
        for(int o = 1; o < tokens.length; o++){
            boolean vorhanden = false;
            for(int i = 0; i < playerItems.size(); i++){
                if(tokens[o].equals(playerItems.get(i).getUsername())){
                    vorhanden = true;
                }
            }
            if(!vorhanden){
                pIs.add(new PlayerItem(tokens[o]));
            }
        }
        for(PlayerItem pI : pIs){
            playerItems.add(pI);
        }
    }

    private void handlePlayerAnswer(String[] tokens) {
        playerItems = new ArrayList<>();
        for(int i = 1; i < tokens.length; i++){
            playerItems.add(new PlayerItem(tokens[i]));
        }
    }

    private void handleClosing() {
        serverRunning = false;
    }


    private void leave() {                                                                          //Sendet Leave Nachricht an Server
        sendMessage("leave");
        aknowleagead = false;
    }

    private void handleNameChange(String token) {                                                   //Setzt Namenstoken aus Nachricht als Namen
        login = token;
    }

    private void handleStart(String[] tokens) {                                                     //Wird aufgerufen wenn Spiel gestartet wird von Host
        for(int i = 1; i < tokens.length; i++){
            String uebergabe = tokens[i].replace("[^\\d]", "");
            cardList.add(Integer.parseInt(uebergabe));
            //TODO START GAME FRAGMENT || ACTIVITY
        }
        gameStarted = true;
    }

    private void handleAccept() {                                                                   //Wird aufgerufen wenn der Server den Join akzeptiert
        sendMessage("GETPLAYER");
        //TODO START LOBBY FRAGMENT
    }

    private void handleDenie() {                                                                    //Wird aufgerufen wenn der Server den Join verweigert
        if(gameActivity != null){
            FindFragment findFrag = (FindFragment) Fragment.instantiate(gameActivity, FindFragment.class.getName(), null);

            FragmentTransaction ft = gameActivity.getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.gameActivityLayout, findFrag);
            ft.commit();
        }
    }

    public void setGameActivity(GameActivity gameActivity){
        this.gameActivity = gameActivity;
    }

}
