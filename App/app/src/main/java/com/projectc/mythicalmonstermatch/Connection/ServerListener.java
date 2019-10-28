package com.projectc.mythicalmonstermatch.Connection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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

                String tokens[] = line.split(" ");
                if(tokens != null && tokens.length > 0){
                    String cmd = tokens[0];
                    sendMessage("aknowledge");
                    if(cmd.equalsIgnoreCase("ask")){
                        handleAsk();
                        break;
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
                    }else if(cmd.equalsIgnoreCase("hearbeat")){
                        handleHeartbeat();
                    }

                }

            }
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
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
            server.addPlayer(this);
            this.login = tokens[1];
            return true;
        }
    }

    private void sendMessage(String msg){
        try {
            bufferedWriter.write(msg + "\r\n");
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleAsk() {
        sendMessage("answer 0 " + server.playerCount() + " " + server.serverName);

    }

    private void handleHeartbeat(){
        count = 0;
    }

    public void heartbeatSend(){
        sendMessage("heartbeat");
        count++;
        if(count >= 10){
            handleConnectionLost();
        }
    }

    private void handleConnectionLost(){
        leave();
    }

    private void leave(){
        server.removePlayer(this);
        server.removeListener(this);
        ArrayList<ServerListener> listenerList = server.getServerListeners();
        for(ServerListener sL : listenerList) {
            sL.sendMessage("update");
        }
    }

    public String getLogin(){
        return login;
    }
}
