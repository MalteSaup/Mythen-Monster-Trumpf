package com.projectc.mythicalmonstermatch.Connection;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


//import com.projectc.mythicalmonstermatch.Connection.Client; FÜR ALLE DATEIEN DIE ES BRAUCHEN DA AUSGELAGERT

public class Server extends Thread{

    private int port = 8080;                                                                        //PORT

    private boolean gameStarted = false;                                                            //Zeigt an ob das Spiel gestartet wurde

    public String serverName;                                                                       //Name des Servers (Servername == Name des Hosts)

    private ArrayList<ServerListener> serverListeners = new ArrayList<>();                          //Liste der gejointen Clients
    private ArrayList<ServerListener> playerList = new ArrayList<>();                               //Liste der gejointen Spielen (Clients != Spieler in diesem Fall)

    public Server (String serverName){
        this.serverName = serverName;
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
    }

    public void removePlayer(ServerListener sL){                                                    //Entfernt Spieler
        playerList.remove(sL);
    }

    public ArrayList<ServerListener> getServerListeners(){                                          //Gibt Client List zurück
        return playerList;
    }

    @Override
    public void run(){
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while(true) {
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
                    while((line = bufferedReader.readLine()) != null){
                        String[] tokens = line.split(" ");
                        if((System.currentTimeMillis() - startTime) > 1000){
                            break;
                        }
                        if(tokens[0].equalsIgnoreCase("ask")){
                            bufferedWriter.write("answer " + serverName + " 5\r\n");
                            bufferedWriter.flush();
                            break;
                        } else if(tokens[0].equalsIgnoreCase("join")){
                            bufferedWriter.write("denied\r\n");
                            bufferedWriter.flush();
                            break;
                        }
                    }
                    clientSocket.close();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
