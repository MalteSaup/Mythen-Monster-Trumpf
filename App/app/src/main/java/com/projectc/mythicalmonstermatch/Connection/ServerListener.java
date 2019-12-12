package com.projectc.mythicalmonstermatch.Connection;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ServerListener extends Thread{
    private Socket socket;
    private Server server;
    private String login;

    private int count = 0;

    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public ServerListener(Server server, Socket socket){
        this.server = server;
        this.socket = socket;
    }

    @Override
    public void run(){
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            Hearbeat hearbeat = new Hearbeat(this);
            hearbeat.start();

            String line;
            while((line = bufferedReader.readLine()) != null){
                Log.d("JETZT SERVER", line);
                String tokens[] = line.split(" ");
                if(tokens != null && tokens.length > 0){
                    String cmd = tokens[0];
                    Log.d("SERVER CMD", cmd);
                    if(cmd.equalsIgnoreCase("ask")){
                        handleAsk();
                    }else if(cmd.equalsIgnoreCase("join")){
                        String[] joinTokens = line.split(" ", 2);
                        if(!handleJoin(joinTokens)){
                            break;
                        }
                    }else if(cmd.equalsIgnoreCase("leave")){
                        leave();
                        break;
                    }else if(cmd.equalsIgnoreCase("start")){
                        handleStart(login);
                    }/*else if(cmd.equalsIgnoreCase("ok")){
                        break;
                    }*/else if(cmd.equalsIgnoreCase("getplayer")){
                        handlePlayerRequest();
                    }else if(cmd.equalsIgnoreCase("playerremoved")){
                        handlePlayerRemove();
                    }else if(cmd.equalsIgnoreCase("playeradded")){
                        handlePlayerAdded();
                    }

                }

            }
            Log.d("JETZT LISTERNER", "ZUENDE");
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handlePlayerAdded() {
        ArrayList<ServerListener> serverListeners = server.getServerListeners();
        String msg = "playeradded ;";
        for(ServerListener sL : serverListeners){
            msg += sL.login + ";";
        }
        sendMessage(msg);
    }

    public void handlePlayerRemove() {
        ArrayList<ServerListener> serverListeners = server.getServerListeners();
        String msg = "playerremoved ;";
        for(ServerListener sL : serverListeners){
            msg += sL.login + ";";
        }
        sendMessage(msg);
    }

    private void handlePlayerRequest() {
        ArrayList<ServerListener> serverListeners = server.getServerListeners();
        String msg = "playeranswer ;";
        for(ServerListener sL : serverListeners){
            msg += sL.login + ";";
            Log.d("PLAYER", sL.login);
        }
        Log.d("PLAYER", msg);
        sendMessage(msg);
    }

    private void handleStart(String login) {
        if(server.startGame(login)){
            ArrayList<ServerListener> listenerList = server.getServerListeners();
            for(ServerListener sL : listenerList) {
                sL.sendMessage("start");
            }
        }
    }

    private boolean handleJoin(String[] tokens) {
        if(server.playerCount() >= 5){
            sendMessage("denied");
            return false;
        }
        else{
            sendMessage("accept");
            //boolean vorhanden = checkForName(tokens[1]);
           // if(vorhanden){sendMessage("setName " + login);}
            this.login = tokens[1];

            server.addPlayer(this);
            for(ServerListener sL : server.getServerListeners()){
                if(sL != this){sL.handlePlayerAdded();}
            }

            return true;
        }
    }

    private boolean checkForName(String login) {
        boolean changed = false;
        int count = 1;
        String uebergabe = login;
        ArrayList<ServerListener> sL = server.getServerListeners();
        Log.d("JETZT LOG", ""+ sL.size());
        for(int i = 0; i < sL.size(); i++){
            Log.d("JETZT LOG", ""+i);
            if(uebergabe.equals(sL.get(i).getLogin())){
                uebergabe = login + count;
                count++;
                i = -1;
                changed = true;
            }
        }
        if(changed){this.login = uebergabe;}
        return changed;
    }

    public void sendMessage(String msg){
        try {
            bufferedWriter.write(msg + "\r\n");
            //bufferedWriter.newLine();
            bufferedWriter.flush();
            Log.d("JETZT SERVER", msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleAsk() {
        sendMessage("ANSWER 0 " + server.playerCount() + " " + server.serverName);

    }

    private void handleConnectionLost(){
        leave();
    }

    private void leave(){
        Log.d("SERVER", "LEAVE");
        server.removeItems(this);
        for(ServerListener sL : server.getServerListeners()){
            if(sL != this){sL.handlePlayerRemove();}
        }

    }

    public String getLogin(){
        return login;
    }
}
