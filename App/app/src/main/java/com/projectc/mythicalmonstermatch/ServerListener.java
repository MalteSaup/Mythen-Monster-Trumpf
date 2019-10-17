package com.projectc.mythicalmonstermatch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ServerListener extends Thread{
    private Socket socket;
    private Server server;
    private String login;

    private InputStream inputStream;
    private OutputStream outputStream;

    public ServerListener(Server server, Socket socket){
        this.server = server;
        this.socket = socket;
    }

    @Override
    public void run(){
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while((line = bufferedReader.readLine()) != null){

                String tokens[] = line.split(" ");
                if(tokens != null && tokens.length > 0){
                    String cmd = tokens[0];

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
            outputStream.write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleAsk() {
        sendMessage("answer " + server.serverName + " " + server.playerCount());

    }

    private void leave(){
        server.removePlayer(this);
        server.removeListener(this);
        ArrayList<ServerListener> listenerList = server.getServerListeners();
        for(ServerListener sL : listenerList) {
            if(!login.equals(sL.getLogin())) {
                sL.sendMessage("update");
            }
        }
    }

    public String getLogin(){
        return login;
    }
}
