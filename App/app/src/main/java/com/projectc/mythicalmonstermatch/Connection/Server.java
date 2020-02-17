package com.projectc.mythicalmonstermatch.Connection;

import android.util.Log;

import com.projectc.mythicalmonstermatch.Fragments.HostFragment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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

    public Server(String serverName, HostFragment hostFragment) {

        this.serverName = serverName;
        this.hostFragment = hostFragment;
    }

    public void removeListener(ServerListener sL){                                                  //ENTFERNT CLIENTS (LISTE MIT SEARCH CLIENTS)
        serverListeners.remove(sL);
    }

    public boolean startGame(String login){                                                         //STARTET SPIEL TODO EVTL IST HIER IWAS FALSCH WARUM MIT LOGIN??
        if(serverName.equalsIgnoreCase(login) && !gameStarted){
            gameStarted = true;                                                                     //SETZT GAME STARTED FLAG
        }
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

    public synchronized ArrayList<ServerListener> getServerListeners(){                             //GIBT CLIENT LISTE AN (OHNE SEARCH CLIENT)
        return playerList;
    }

    @Override
    public void run(){                                                                              //STARTET SERVER
        try {
            ServerSocket serverSocket = new ServerSocket(port);                                     //ERZEUGT SERVER SOCKET
            while(running) {
                Log.d("SERVER LOG", "Waiting for Users...");
                Socket clientSocket = serverSocket.accept();                                        //NIMMT CLIENTS AN

                if(!gameStarted){                                                                   //STARTET VERBINDUNG UND FÜGT CLIENTS ERSTMAL ZUR "UNSAUBEREN" LISTE, WENN SPIEL NICHT GESTARTET
                    ServerListener sL = new ServerListener(this, clientSocket, false);
                    serverListeners.add(sL);
                    sL.start();
                }
                else{                                                                               //WENN SPIEL GESTARTET WIRD NUR ANHAND DER ÜBERGEBENEN ID GEPRÜFT OB DER SPIELER TEIL DES SPIELS IST UM REJOIN ZU ERMÖGLICHEN ALLE ANDEREN WERDEN DENIED
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                    String line;
                    long startTime = System.currentTimeMillis();
                    boolean vorhanden = false;
                    while(((line = bufferedReader.readLine()) != null) ||(System.currentTimeMillis()-startTime)<1000){  //ERZEUGT WHILE LOOP FÜR NACHRICHTEN VERARBEITUNG MIT 1s LEBENSDAUER
                        String[] tokens = line.split(" ");                                    //NACHRICHT IN SEGMENTE AUFGETEILT, 1. SEGMENT IST DER COMMAND
                        //COMMAND VERARBEITUNG
                        if(tokens[0].equalsIgnoreCase("ask")){
                            bufferedWriter.write("answer 1 " + playerList.size() + " " + serverName + "\r\n");  //ANTWORT AUF SERVER STATUS ANFRAGE
                            bufferedWriter.flush();
                        } else if(tokens[0].equalsIgnoreCase("join")){                  //VERARBEITUNG JOIN ANFRAGE
                            String[] split = line.split(" ", 3);
                            for(ServerListener sL : playerList){
                                if(sL.getID() == Integer.parseInt(split[1]) && sL.getLogin().equals(split[3])){ //ID UND LOGIN ÜBERPRÜFUNG OB VORHANDEN
                                    vorhanden = true;                                               //VORHANDEN FLAG GESETZT
                                }
                            }
                            if(vorhanden){                                                          //VORHANDEN FLAG GEPRÜFT
                                serverListeners.add(new ServerListener(this, clientSocket, true));  //CLIENT HINZUGEFÜGT
                            } else{                                                                 //FALLS NICHT VORHANDEN JOIN ANFRAGE DENIED
                                bufferedWriter.write("denied\r\n");
                                bufferedWriter.flush();
                            }
                            break;
                        }
                    }
                    if(!vorhanden){clientSocket.close();}
                }

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
}
