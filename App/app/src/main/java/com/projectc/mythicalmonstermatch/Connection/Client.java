package com.projectc.mythicalmonstermatch.Connection;

import android.util.Log;
import android.widget.Toast;

import com.projectc.mythicalmonstermatch.GameActivity;
import com.projectc.mythicalmonstermatch.PlayerItem;

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

    private String serverName;                                                                      //SERVER NAME
    private String address;                                                                         //NETWORK IP ADDRESSE FÜRS VERBINDEN
    private String login;                                                                           //USERNAME

    private CharSequence connectionLostToast = "Connection Lost";
    private CharSequence connectionNotPossibleToast = "Connection Not Possible";

    private int id = -1;

    private boolean gameStarted = false;                                                            //SPIEL GESTARTET FLAG
    private boolean joined = false;                                                                 //SERVER GEJOINT FLAG
    private boolean serverRunning = true;                                                           //SERVER LÄUFT FLAG
    public boolean running = true;                                                                  //CLIENT RUNNING FLAG FÜRS CLIENT KILLEN WENN ZURÜCK ZU MAIN ACTIVITY
    private boolean leaved = false;                                                                 //LEAVED FLAG

    private Socket socket;                                                                          //SOCKET

    private ArrayList<Integer> cardList = new ArrayList<>();                                        //KARTEN DECK

    private OutputStream outputStream;                                                              //IN- UND OUTPUT STREAM(BUFFERED READER UND WRITER) FÜR KOMMUNIKATION VON CLIENT UND SERVER
    private InputStream inputStream;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    private GameActivity gameActivity;                                                              //GAME ACTIVITY

    public ArrayList<PlayerItem> playerItems = new ArrayList<>();                                   //LISTE DER GEJOINTEN SPIELER

    public Client (String serverName, String login, String address) {                               //INITIALISIERUNG DES CLIENTS OHNE VORHANDEN ID
        this.serverName = serverName;
        this.login = login;
        this.address = address;
    }

    public Client (String serverName, String login, String address, int id) {                       //INITIALISIERUNG DES CLIENTS MIT VORHANDENER ID
        this.serverName = serverName;
        this.login = login;
        this.address = address;
        this.id = id;
    }

    @Override
    public void run(){                                                                              //STARTET CLIENT
        connect(address);                                                                           //VERSUCHT SICH MIT SERVER ZU CONNECTEN
    }

    public void sendMessage(String msg) {                                                           //SENDET NACHRICHT AN SERVER
        try {
            bufferedWriter.write(msg + "\r\n");                                                 //NACHRICHT GESCHRIEBEN; \r\n UM ENDE DER NACHRICHT ANZUZEIGEN
            bufferedWriter.flush();                                                                 //NACHRICHT SICHER GESENDET
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessageToAll(String msg){
        //TODO Message an alle CMD überlegen und integrieren
    }


    public void connect(String address) {                                                           //VERBINDUNGSAUFBAU FUNKTION
        Log.d("JETZT ADDRESS", address);
            try {
                socket = new Socket(address, 8080);                                            //VERBINDUNG MIT SERVER AUF ÜBERGEBENDER IP ADDRESSE
                inputStream = socket.getInputStream();                                              //IN UND OUTPUT STREMA WERDEN VOM SOCKET AUSGELESEN FÜR NACHRICHTVERKEHR
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                outputStream = socket.getOutputStream();
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));



                String line;

                sendMessage("join " + id + " " + login);                                       //NACHRICHT AN SERVER DAS MAN JOINEN WILL
                joined = true;                                                                      //JOIN FLAG WIRD TRUE GESETZT

                while((line = bufferedReader.readLine()) != null && serverRunning && running){      //WHILE SCHLEIFE FÜR NACHRICHT VERARBEITUNG
                    String[] tokens = line.split(" ");                                        //NACHRICHT IN SEGMENTE AUFGETEILT, 1. SEGMENT IST DER COMMAND
                    if(tokens != null && tokens.length > 0) {
                        String cmd = tokens[0];
                        Log.d("CLIENT", cmd);
                        //COMMAND VERARBEITUNG
                        if("denied".equalsIgnoreCase(cmd)){                                         //JOIN WIRD ABGELEHNT
                            handleDenie();
                        } else if("accept".equalsIgnoreCase(cmd)){                                  //JOIN WIRD AKZEPTIERT
                            handleAccept(tokens[1]);
                        } else if("start".equalsIgnoreCase(cmd)){                                   //SPIEL WIRD GESTARTET
                            handleStart(tokens);
                        } else if("closing".equalsIgnoreCase(cmd)){                                 //SERVER WIRD GESCHLOSSEN
                            handleClosing();
                            break;
                        } else if("playeranswer".equalsIgnoreCase(cmd)){                            //ANTWORT NACH JOIN WELCHE SPIELER AUF SERVER SIND
                            Log.d("JETZT LOS", line);
                            tokens = line.split("[;]");                                       //NACHRICHT WIRD ANDERS AUFGETEILT UM NAMEN ZU LESEN
                            handlePlayerAnswer(tokens);
                        } else if("playeradded".equalsIgnoreCase(cmd)){                             //NACHRICHT VOM SERVER WENN ANDERER SPIELER GEJOINT IST
                            Log.d("JETZT LOS", line);
                            tokens = line.split("[;]");                                       //NACHRICHT WIRD ANDERS AUFGETEILT UM NAMEN ZU LESEN
                            handlePlayerAdded(tokens);
                        } else if("playerremoved".equalsIgnoreCase(cmd)){                           //NACHRICHT VOM SERVER WENN ANDERER SPIELER GELEAVED IST
                            tokens = line.split("[;]");                                       //NACHRICHT WIRD ANDERS AUFGETEILT UM NAMEN ZU LESEN
                            handlePlayerRemoved(tokens);
                        } else if("heartbeat".equalsIgnoreCase(cmd)){                               //HEARTBEAT NACHRICHT VOM SERVER UM FESTZUSTELLEN FALLS EIN CLIENT DIE VERBINDUNG VERLOREN HAT
                            handleHeartbeat();
                        } else if("turn".equalsIgnoreCase(cmd)){
                            handleTurnMsg(tokens);
                        } else if("compared".equalsIgnoreCase(cmd)){
                            handleCompare(tokens);
                        } else if("playerinf".equalsIgnoreCase(cmd)){
                            handlePlayerInfo(line.split("[;]"));
                        } else if("win".equalsIgnoreCase(cmd)){
                            handleWin();
                        } else if("lose".equalsIgnoreCase(cmd)){
                            handleLose();
                        }
                    }
                }
                Log.d("CLIENT", "ZUENDDE");

            } catch (ConnectException e){
                Log.d("ERROR", "CONNECTION FAILED");
                if(joined && !leaved){                                                              //VERBINDUNG NACH JOIN VERLOREN
                    Toast toast = Toast.makeText(gameActivity, connectionLostToast, Toast.LENGTH_SHORT);
                    toast.show();
                    gameActivity.startFindFrag();                                                   //STARTET FIND FRAGMENT
                } else if(!joined && !leaved){                                                      //VERBINDUNG NICHT MÖGLICH
                    Toast toast = Toast.makeText(gameActivity, connectionNotPossibleToast, Toast.LENGTH_SHORT);
                    toast.show();
                    gameActivity.inHost = false;
                    gameActivity.startFindFrag();                                                   //STARTET FIND FRAGMENT
                }

            } catch (IOException e) {
                Log.d("ERROR", "SERVER CLOSED");                                          //VERBINDUNG ZUM SERVER VERLOREN (EVTL SERVER ABSTURZ)
                if(gameActivity.code == 1){                                                         //NUR WENN CLIENT
                    Toast toast = Toast.makeText(gameActivity, connectionLostToast, Toast.LENGTH_SHORT);
                    toast.show();
                    gameActivity.inHost = false;                                                    //SETZT INHOST FLAG AUF FALSE
                    gameActivity.startFindFrag();                                                   //STARTET FIND FRAGMENT
                }
                e.printStackTrace();
            }
    }

    private void handleLose() {
        gameActivity.gameFragment.createWinLoseScreen(0);
    }

    private void handleWin() {
        gameActivity.gameFragment.createWinLoseScreen(1);
    }

    private void handlePlayerInfo(String[] tokens) {
        String[] uebergabe[] = new String[tokens.length][];
        for(int i = 1; i < tokens.length; i++){
            String[] split = tokens[i].split("[:]", 2);
            uebergabe[i-1] = split;
        }
        gameActivity.gameFragment.updateAll(uebergabe);
    }

    private void handleCompare(String[] tokens) {
        if(tokens[1].equalsIgnoreCase("0")){
            //LOSE ANZEIGEN
        } else if(tokens[1].equalsIgnoreCase("1")){
            gameActivity.gameFragment.getPlayerFrag();
        } else if(tokens[1].equalsIgnoreCase("2")){
            //DRAW LAUNCHEN
        }
        int winnerID = Integer.parseInt(tokens[2]);                 //TODO AN GAME FRAGMENT WEITERREICHEN UND ATTRIBUTE AN DEN VERSCHIEDENEN KARTEN EINFÄRBEN
    }

    private void handleTurnMsg(String[] tokens) {
        if(tokens[1].equalsIgnoreCase("1")){
            gameActivity.turn = true;
        }
        int cardID = Integer.parseInt(tokens[2]);                                                   //TODO AN GAME FRAGMENT WEITER REICHEN UND IWO ZWISCHEN SPEICHERN
    }

    private void handleHeartbeat() {
        sendMessage("heartbeat");                                                              //ANTWORTET AUF HEARTBEAT
    }

    private void handlePlayerRemoved(String[] tokens) {
        Log.d("JETZT TOKENS", tokens[1]);
        ArrayList<Integer> uebergabe = new ArrayList<>();
        for(int i = 0; i < playerItems.size(); i++){                                                //CHECKT WELCHE SPIELER GELEAVED SIND UND UPDATED DANACH DEN RECYCLER VIEW DER DIE SPIELER ANZEIGT
            boolean vorhanden = false;                                                              //VERGLEICHT DAZU OB ELEMENTE AUS DER PLAYERITEMS LISTE NOCH IN ÜBERTRAGENDER LISTE SIND
            for(int o = 1; o < tokens.length; o++){
                String[] split = tokens[o].split("[:]", 2);                             //SPLITED EINZELNE SEGMENT NOCHMAL UM ID UND NAME ZU TRENNEN
                if(Integer.parseInt(split[1]) == playerItems.get(i).getId()){
                    vorhanden = true;
                }
            }
            if(!vorhanden){
                uebergabe.add(i);
            }
        }
        for(int i : uebergabe) {
            playerItems.remove(i);                                                                  //ENTFERNT SPIELER AUS DER SPIELERLISTE ANHAND LISTEN POSITION
        }
    }

    private void handlePlayerAdded(String[] tokens) {
        ArrayList<PlayerItem> pIs = new ArrayList<>();
        for(int o = 1; o < tokens.length; o++){                                                     //CHECKT WELCHE SPIELER GEJOINED SIND UND UPDATED DANACH DEN RECYCLER VIEW DER DIE SPIELER ANZEIGT
            boolean vorhanden = false;                                                              //VERGLEICHT DAZU OB ÜBERTRAGENDE LISTENELEMENTE SCHON IN DER PLAYERITEMS LISTE IST
            String[] split = tokens[o].split("[:]", 2);
            for(int i = 0; i < playerItems.size(); i++){
                if(Integer.parseInt(split[1]) == playerItems.get(i).getId()){
                    vorhanden = true;
                }
            }
            if(!vorhanden){
                pIs.add(new PlayerItem(split[0], Integer.parseInt(split[1])));
            }
        }
        for(PlayerItem pI : pIs){
            playerItems.add(pI);                                                                    //FÜGT SPIELER DER SPIELERLISTE HINZU
        }
    }

    private void handlePlayerAnswer(String[] tokens) {                                              //FÜGT SPIELER DER SPIELERLIST UND DEM RECYCLER VIEW HINZU => DA ERSTER KONTAKT MIT SERVER DESHALB KEIN VERGLEICHSFUNKTION MIT VORHANDEN SPIELERN BENÖTIGT
        Log.d("CLIENT ANSWER PLAYER", ""  + tokens.length);
        playerItems = new ArrayList<>();
        for(int i = 1; i < tokens.length; i++){
            String[] split = tokens[i].split("[:]", 2);
            playerItems.add(new PlayerItem(split[0], Integer.parseInt(split[1])));
        }
    }

    private void handleClosing() {
        serverRunning = false;                                                                      //STOPT CLIENT WENN SERVER GESCHLOSSEN WIRD
    }


    public void leave() {                                                                           //STOPT CLIENT WENN SERVER GELEAVED WIRD UND SENDET SERVER ENTSPRECHENDE NACHRICHT
        leaved = true;
        sendMessage("leave");
        running = false;
    }

    private void handleStart(String[] tokens) {                                                     //SETZT SPIEL START FLAG UND STARTET GAME FRAGMENT
        gameActivity.startGame();
        gameStarted = true;                                                                         //SETZT GAME START FLAG
    }

    private void handleAccept(String id) {                                                          //SERVER AKZEPTIERT JOIN ANFRAGE
        this.id = Integer.parseInt(id);                                                             //SERVER SENDET SPIELER ID ZU
        Log.d("JETZT GA", "" + (gameActivity != null) + " "+ gameActivity.id);
        gameActivity.id = this.id;                                                                  //ID WIRD GESETZT
        sendMessage("GETPLAYER");                                                              //ANFRAGE AN SERVERT DIE SPIELER ZU SCHICKEN
    }

    private void handleDenie() {                                                                    //SERVER LEHNT JOIN ANFRAGE AB
        if(gameActivity != null){
            gameActivity.inHost = false;                                                            //IN HOST FLAG WIRD ZURÜCK GESETZT
            gameActivity.startFindFrag();                                                           //FIND FRAGMENT WIRD GESTARTET
        }
    }

    public void setGameActivity(GameActivity gameActivity){
        this.gameActivity = gameActivity;                                                           //CLIENT SETZT GAME ACTIVITY
    }
    public String getLogin(){return login;}
    public String getAddress(){return address;}
}
