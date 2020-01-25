package com.projectc.mythicalmonstermatch.Connection;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

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

    private CharSequence connectionLostToast = "Connection Lost";
    private CharSequence connectionNotPossibleToast = "Connection Not Possible";

    private int id = -1;

    private boolean gameStarted = false;                                                            //Zeigt an ob das Spiel gestartet wurde
    private boolean joined = false;
    private boolean serverRunning = true;
    public boolean running = true;
    private boolean leaved = false;

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
        this.address = address;
    }

    public Client (String serverName, String login, String address, int id) {
        this.serverName = serverName;
        this.login = login;
        this.address = address;
        this.id = id;
    }

    @Override
    public void run(){
        Log.d("JETZT ADDRESS", "WAT" + address);
        connect(address);
    }

    public void sendMessage(String msg) {                                                          //Funktion fürs Senden von Nachrichten an Server
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
        Log.d("JETZT ADDRESS", address);
            try {
                socket = new Socket(address, 8080);                              //Verbindung auf Server wird hergestellt
                inputStream = socket.getInputStream();                                              //Kriegt In- und Outputstream und versieht diese mit Buffern fürs Lesen und Schreiben für Kommunikation
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                outputStream = socket.getOutputStream();
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

                //WICHTIG \r\n am Ende der Zeile sonst nicht gelesen

                String line;

                Hearbeat hearbeat = new Hearbeat(this);

                sendMessage("join " + id + " " + login);
                joined = true;

                while((line = bufferedReader.readLine()) != null && serverRunning && running){                                  //While Schleife für Nachrichten verarbeitung
                    String[] tokens = line.split(" ");                                        //Splited Nachricht auf. 1 Nachrichten Block ist Command Token
                    if(tokens != null && tokens.length > 0) {
                        String cmd = tokens[0];
                        Log.d("CLIENT", cmd);
                        if("denied".equalsIgnoreCase(cmd)){                                         //Wenn Denied Command (Nachfolgenden IF Statements dasselbe bloß anderes Command mit anderer Funktion)
                            handleDenie();
                        } else if("accept".equalsIgnoreCase(cmd)){
                            handleAccept(tokens[1]);
                        } else if("start".equalsIgnoreCase(cmd)){
                            handleStart(tokens);
                        } else if("closing".equalsIgnoreCase(cmd)){
                            handleClosing();
                            break;
                        } else if("playeranswer".equalsIgnoreCase(cmd)){
                            Log.d("JETZT LOS", line);
                            tokens = line.split("[;]");
                            handlePlayerAnswer(tokens);
                        } else if("playeradded".equalsIgnoreCase(cmd)){
                            Log.d("JETZT LOS", line);
                            tokens = line.split("[;]");
                            handlePlayerAdded(tokens);
                        } else if("playerremoved".equalsIgnoreCase(cmd)){
                            tokens = line.split("[;]");
                            handlePlayerRemoved(tokens);
                        }
                    }
                }
                Log.d("CLIENT", "ZUENDDE");

            } catch (ConnectException e){
                Log.d("ERROR", "CONNECTION FAILED");
                if(joined && !leaved){
                    Toast toast = Toast.makeText(gameActivity, connectionLostToast, Toast.LENGTH_SHORT);
                    toast.show();
                    gameActivity.startFindFrag();
                } else if(!joined && !leaved){
                    Toast toast = Toast.makeText(gameActivity, connectionNotPossibleToast, Toast.LENGTH_SHORT);
                    toast.show();
                    gameActivity.startFindFrag();
                }

            } catch (IOException e) {
                Log.d("ERROR", "SERVER CLOSED");
                if(gameActivity.code == 1){
                    Toast toast = Toast.makeText(gameActivity, connectionLostToast, Toast.LENGTH_SHORT);
                    toast.show();
                    gameActivity.startFindFrag();
                }
                e.printStackTrace();
            }
    }

    private void handlePlayerRemoved(String[] tokens) {
        Log.d("JETZT TOKENS", tokens[1]);
        ArrayList<Integer> uebergabe = new ArrayList<>();
        for(int i = 0; i < playerItems.size(); i++){
            boolean vorhanden = false;
            for(int o = 1; o < tokens.length; o++){
                String[] split = tokens[o].split("[:]", 2);
                if(split[0].equals(playerItems.get(i).getUsername()) && Integer.parseInt(split[1]) == playerItems.get(i).getId()){
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
        Log.d("JETZT TOKENS", tokens[1]);
        ArrayList<PlayerItem> pIs = new ArrayList<>();
        for(int o = 1; o < tokens.length; o++){
            boolean vorhanden = false;
            String[] split = tokens[o].split("[:]", 2);
            for(int i = 0; i < playerItems.size(); i++){
                Log.d("JETZTIWAS", "" +  Integer.parseInt(split[1]) +  ": " + playerItems.get(i).getId());
                if(Integer.parseInt(split[1]) == playerItems.get(i).getId()){
                    vorhanden = true;
                }
            }
            if(!vorhanden){
                Log.d("ARRAYPR", "" + split.length);
                pIs.add(new PlayerItem(split[0], Integer.parseInt(split[1])));
            }
        }
        for(PlayerItem pI : pIs){
            playerItems.add(pI);
        }
    }

    private void handlePlayerAnswer(String[] tokens) {
        Log.d("CLIENT ANSWER PLAYER", ""  + tokens.length);
        playerItems = new ArrayList<>();
        for(int i = 1; i < tokens.length; i++){
            String[] split = tokens[i].split("[:]", 2);
            playerItems.add(new PlayerItem(split[0], Integer.parseInt(split[1])));
        }
    }

    private void handleClosing() {
        serverRunning = false;
    }


    public void leave() {                                                                           //Sendet Leave Nachricht an Server
        leaved = true;
        sendMessage("leave");
        running = false;
    }

    private void handleStart(String[] tokens) {                                                     //Wird aufgerufen wenn Spiel gestartet wird von Host
        for(int i = 1; i < tokens.length; i++){
            String uebergabe = tokens[i].replace("[^\\d]", "");
            cardList.add(Integer.parseInt(uebergabe));
            //TODO START GAME FRAGMENT
        }
        gameStarted = true;
    }

    private void handleAccept(String id) {                                                                   //Wird aufgerufen wenn der Server den Join akzeptiert
        this.id = Integer.parseInt(id);
        Log.d("JETZT GA", "" + (gameActivity != null) + " "+ gameActivity.id);
        gameActivity.id = this.id;
        sendMessage("GETPLAYER");
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
