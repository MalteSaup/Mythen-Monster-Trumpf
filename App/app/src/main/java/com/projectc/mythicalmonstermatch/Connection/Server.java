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

    private boolean gameStarted = false;                                                            //Zeigt an ob das Spiel gestartet wurde
    public boolean running = true;

    public String serverName;                                                                       //Name des Servers (Servername == Name des Hosts)

    private ArrayList<ServerListener> serverListeners = new ArrayList<>();                          //Liste der gejointen Clients
    private ArrayList<ServerListener> playerList = new ArrayList<>();                               //Liste der gejointen Spielen (Clients != Spieler in diesem Fall)

    public Server(String serverName, HostFragment hostFragment) {

        this.serverName = serverName;
        this.hostFragment = hostFragment;
    }

    public void removeListener(ServerListener sL){                                                  //Entfernt Listener aus der Client Liste
        serverListeners.remove(sL);
    }

    public boolean startGame(String login){                                                         //Startet das Spiel
        if(serverName.equalsIgnoreCase(login) && !gameStarted){
            gameStarted = true;
        }
        return gameStarted;
    }

    public int playerCount(){                                                                       //Gibt an wieviele Spieler aufm Server sind
        return playerList.size();
    }

    public void addPlayer(ServerListener sL){                                                       //Fügt Spieler hinzu
        playerList.add(sL);

        Log.d("SERVER", "HINZU " + sL.getLogin());
    }

    public void removePlayer(ServerListener sL){                                                    //Entfernt Spieler
        playerList.remove(sL);

        Log.d("SERVER", "ENTFERNT " + sL.getLogin());
    }

    public synchronized ArrayList<ServerListener> getServerListeners(){                                          //Gibt Client List zurück
        return playerList;
    }

    @Override
    public void run(){
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while(running) {
                Log.d("SERVER LOG", "Waiting for Users...");
                Socket clientSocket = serverSocket.accept();                                        //Nimmt Clients an

                if(!gameStarted){                                                                   //Started Verbindung mit Client und gibt weitere Verarbeitung an ServerListener weiter
                    ServerListener sL = new ServerListener(this, clientSocket);
                    serverListeners.add(sL);
                    sL.start();
                }
                else{                                                                               //Wenn Spiel gestarted blockt es alle Abfragen und Join Versuche ab
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                    String line;
                    long startTime = System.currentTimeMillis();

                    while(((line = bufferedReader.readLine()) != null) ||(System.currentTimeMillis()-startTime)<1000){
                        String[] tokens = line.split(" ");
                        if(tokens[0].equalsIgnoreCase("ask")){
                            bufferedWriter.write("answer 1 " + playerList.size() + " " + serverName + "\r\n");
                            bufferedWriter.flush();
                        } else if(tokens[0].equalsIgnoreCase("join")){
                            bufferedWriter.write("denied\r\n");
                            bufferedWriter.flush();
                            break;
                        }
                    }
                    clientSocket.close();
                }

            }
            Log.d("SERVER", "ZUENDE");
        } catch (BindException e){
            if(e.toString().equals("java.net.BindException: bind failed: EADDRINUSE (Address already in use)")){
                Log.d("ERROR", "YEETISTAN");

            }
        } catch (IOException e) {
            Log.d("ERROR", "" + e);
        }
    }



    public void removeItems(ServerListener sL){
        removeListener(sL);
        removePlayer(sL);
    }

    public void closeServer(){
        for(ServerListener sL : serverListeners){
            sL.sendMessage("closing");
            running = false;
        }
    }
}
