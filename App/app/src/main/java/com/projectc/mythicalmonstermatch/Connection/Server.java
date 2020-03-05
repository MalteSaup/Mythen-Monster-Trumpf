package com.projectc.mythicalmonstermatch.Connection;

import android.util.Log;

import com.projectc.mythicalmonstermatch.Fragments.HostFragment;
import com.projectc.mythicalmonstermatch.GameActivity;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


//import com.projectc.mythicalmonstermatch.Connection.Client; FÜR ALLE DATEIEN DIE ES BRAUCHEN DA AUSGELAGERT

public class Server extends Thread{

    private int port = 8080;                                                                        //PORT

    private HostFragment hostFragment;

    private boolean gameStarted = false;                                                            //GAME STARTED FLAG
    public boolean running = true;                                                                  //RUNNING FLAG

    public String serverName;                                                                       //SERVERNAME

    private ArrayList<ServerListener> serverListeners = new ArrayList<>();                          //LISTE GEJOINTER CLIENTS (AUCH SEARCH CLIENTS DIE MOMENTAN ANFRAGE STELLEN)
    private ArrayList<ServerListener> playerList = new ArrayList<>();                               //LISTE GEJOINTER CLIENTS (NUR SPIELER, KEINE SEARCH CLIENTS)
    private ArrayList<Boolean> nextTurn = new ArrayList<>();
    private GameActivity gameActivity;

    public Server(String serverName, HostFragment hostFragment, GameActivity gameActivity) {

        this.serverName = serverName;
        this.hostFragment = hostFragment;
        this.gameActivity = gameActivity;
    }

    public void removeListener(ServerListener sL){                                                  //ENTFERNT CLIENTS (LISTE MIT SEARCH CLIENTS)
        serverListeners.remove(sL);
    }

    public boolean startGame(String login){                                                         //STARTET SPIEL TODO EVTL IST HIER IWAS FALSCH WARUM MIT LOGIN??
        if(serverName.equalsIgnoreCase(login) && !gameStarted){
            gameStarted = true;                                                                     //SETZT GAME STARTED FLAG
        }
        for(int i = 0; i < playerList.size(); i++){nextTurn.set(i, false);}
        return gameStarted;
    }

    public int playerCount(){                                                                       //GIBT SPIELER ANZAHL AN
        return playerList.size();
    }

    public void addPlayer(ServerListener sL){                                                       //FÜGT SPIELER HINZU
        playerList.add(sL);
    }

    public void removePlayer(ServerListener sL){                                                    //ENTFERNT SPIELER
        playerList.remove(sL);
    }

    public synchronized ArrayList<ServerListener> getServerListeners(){                             //GIBT CLIENT LISTE AN (OHNE SEARCH CLIENT) KOMMENTAR: UNGLÜCKLICH GEWÄHLTER NAME DER FUNKTIOIN... SORRY
        return playerList;
    }

    @Override
    public void run(){                                                                              //STARTET SERVER
        try {
            ServerSocket serverSocket = new ServerSocket(port);                                     //ERZEUGT SERVER SOCKET
            while(running) {
                Log.d("SERVER LOG", "Waiting for Users...");
                Socket clientSocket = serverSocket.accept();                                        //NIMMT VERBINDUNG AN UND ARBEITET DIESE IN EINEN SERVERLISTENER AB

                ServerListener sL = new ServerListener(this, clientSocket, false);     //ERZEUGT SERVERLISTENER ANHAND DER VERBINDUNG
                serverListeners.add(sL);                                                            //FÜGT SERVERLISTENER DER SERVERLISTENERLISTE HINZU
                sL.start();                                                                         //SERVERLISTENER GESTARTET
            }
        } catch (BindException e){
            if(e.toString().equals("java.net.BindException: bind failed: EADDRINUSE (Address already in use)")){
                Log.d("ERROR", "YEETISTAN");

            }
        } catch (IOException e) {
            Log.d("ERROR", "" + e);
        }
    }



    public void removeItems(ServerListener sL){                                                     //ENTFERNT SPIELER AUS BEIDEN LISTEN, AUßER SPIEL IST SCHON GESTARTET DANN NUR AUS UNSAUBERER LISTE
        removeListener(sL);
        if(!gameStarted){removePlayer(sL);}
        //TODO ALLE BENACHRICHTIGEN DAS SPIELER VERBINDUNGSFEHLER HAT => IM SERVERLISTENER
    }

    public void closeServer(){                                                                      //SERVER WIRD GESCHLOSSEN
        for(ServerListener sL : serverListeners){                                                   //NACHRICHT AN ALLE CLIENTS DAS SERVER GESCHLOSSEN WIRD
            sL.sendMessage("closing");
            sL.closeHeartbeat();                                                                    //ALLE HEARTBEATS WERDEN BEENDET
            running = false;                                                                        //RUNNING FLAG WIRD FALSE GESETZT
        }
    }

    public boolean getStartState(){
        return gameStarted;                                                                         //GIBT GAME STARTED FLAG ZURÜCK
    }
    public void setNextTurn(int id){
        for(int i = 0; i < playerList.size(); i++){
            if(id == playerList.get(i).getID()){
                nextTurn.set(i, true);
                break;
            }
        }
        boolean allNextTurn = true;
        for(int i = 0; i < playerList.size(); i++){
            if(!nextTurn.get(i) && !playerList.get(i).connectionLoss){
                allNextTurn = false;
            }
        }
        if(allNextTurn){
            startNextTurn();
        }
    }

    private void startNextTurn() {
        gameActivity.gameManager.nextTurn();
    }
}
