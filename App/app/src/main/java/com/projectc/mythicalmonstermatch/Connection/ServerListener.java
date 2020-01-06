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

    private int id = -1;

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
                        String[] joinTokens = line.split(" ", 3);
                        if(!handleJoin(joinTokens)){
                            sendMessage("denied");
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
            handleConnectionLost();
            e.printStackTrace();
        }
    }

    private void handlePlayerAdded() {
        ArrayList<ServerListener> serverListeners = server.getServerListeners();
        String msg = "playeradded ;";
        for(ServerListener sL : serverListeners){
            msg += sL.login + ":" + sL.id + ";";
        }
        sendMessage(msg);
    }

    private void handlePlayerRemove() {
        ArrayList<ServerListener> serverListeners = server.getServerListeners();
        String msg = "playerremoved ;";
        for(ServerListener sL : serverListeners){
            msg += sL.login + ":" + sL.id + ";";
        }
        sendMessage(msg);
    }

    private void handlePlayerRequest() {
        ArrayList<ServerListener> serverListeners = server.getServerListeners();
        String msg = "playeranswer ;";
        for(ServerListener sL : serverListeners){
            msg += sL.login + ":" + sL.id + ";";
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

    private void joinPlayerAdded(){
        sendMessage("accept " + this.id);
        server.addPlayer(this);
        for(ServerListener sL : server.getServerListeners()){
            if(sL != this){sL.handlePlayerAdded();}
        }
    }

    private boolean handleJoin(String[] tokens) {
        if(server.playerCount() >= 5 && !server.getStartState()){
            return false;
        } else if(server.getStartState() && Integer.parseInt(tokens[1]) != -1){
            if(checkID(tokens[1], tokens[2])){
                this.id = Integer.parseInt(tokens[1]);
                this.login = tokens[2];
                joinPlayerAdded();
                return true;
            }
            return false;
        }
        else{
            this.login = tokens[2];
            this.id = generateID();
            joinPlayerAdded();

            return true;
        }
    }

    private boolean checkID(String id, String usrName) {
        int id_numb = Integer.parseInt(id);
        for(int i = 0; i < server.getServerListeners().size(); i++){
            ServerListener sL = server.getServerListeners().get(i);
            if(id_numb == sL.id){
                if(usrName.equals(sL.login)){
                    return true;
                }
            }
        }
        return false;
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
        int started = 0;
        if(server.getStartState()){started = 1;}
        sendMessage("ANSWER " + started + " " + server.playerCount() + " " + server.serverName);
    }

    private void handleConnectionLost(){
        //TODO DIFFERENT BEHAVIOUR WHEN GAME STARTED
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
    public int getID(){return id;}

    private int generateID() {
        int id = -1;
        boolean idGenerated = false;
        while (!idGenerated) {
            id = (int)(Math.random() * 899999999 + 100000000);
            idGenerated = true;
            for (int i = 0; i < server.getServerListeners().size(); i++) {
                if (server.getServerListeners().get(i).id == id) {
                    idGenerated = false;
                }
            }
        }
        if (id != -1) {
            return id;
        } else {
            Log.d("ERROR", "HOFFENTLICH NIE VORHANDEN");
            return generateID();
        }
    }
}
