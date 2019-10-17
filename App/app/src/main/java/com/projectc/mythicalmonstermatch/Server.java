package com.projectc.mythicalmonstermatch;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends Thread{

    private int[] portArray = {8080};
    private int port;
    private int portNumber = 0;
    private boolean gameStarted = false;
    public String serverName;

    private ArrayList<ServerListener> serverListeners = new ArrayList<>();
    private ArrayList<ServerListener> playerList = new ArrayList<>();

    public Server (int port, int portNumber, String serverName){
        this.port = port;
        this.portNumber = portNumber;
        this.serverName = serverName;
    }

    public void removeListener(ServerListener sL){
        serverListeners.remove(sL);
    }

    public boolean startGame(String login){
        if(serverName.equalsIgnoreCase(login) && !gameStarted){
            gameStarted = true;
        }
        return gameStarted;
    }

    public int playerCount(){
        return playerList.size();
    }

    public void addPlayer(ServerListener sL){
        playerList.add(sL);
    }

    public void removePlayer(ServerListener sL){
        playerList.remove(sL);
    }

    public ArrayList<ServerListener> getServerListeners(){
        return playerList;
    }

    @Override
    public void run(){
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while(true) {
                Log.d("SERVER LOG", "Waiting for Users...");
                Socket clientSocket = serverSocket.accept();

                if(!gameStarted){
                    ServerListener sL = new ServerListener(this, clientSocket);
                    serverListeners.add(sL);
                    sL.start();
                }
                else{
                    OutputStream outputStream= clientSocket.getOutputStream();
                    outputStream.write(("answer " + serverName + " 5").getBytes());
                    outputStream.write("denied".getBytes());
                    clientSocket.close();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
