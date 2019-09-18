package com.projectc.mythicalmonstermatch;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ServerMain extends Thread {

    public ServerSocket serverSocket;
    public HashMap<Socket, String> clientSocketHashMap = new HashMap<>();
    public String serverInfoText;
    private String[] msg;
    private boolean gameIsStarted = false;
    private Socket[] rangfolgeSpieler;
    private int anDerReihe = 0;
    final int SocketServerPORT = 8080;

    public ServerMain(String name){
        serverInfoText = name;
    }

    @Override
    public void run(){
        if(serverSocket == null){
            try {
                serverSocket = new ServerSocket(SocketServerPORT);

                while(true){
                    if(!gameIsStarted) {
                        boolean gotConnected = false;

                        int number = 0;
                        Socket client = serverSocket.accept();
                        DataOutputStream out = new DataOutputStream(client.getOutputStream());
                        for (Socket socket : clientSocketHashMap.keySet()) {
                            if (socket.isClosed()) {
                                clientSocketHashMap.remove(socket);
                            }
                        }
                        if (clientSocketHashMap.size() < 5) {
                            DataInputStream input = new DataInputStream(client.getInputStream());
                            out.writeUTF(serverInfoText + ";" + clientSocketHashMap.size());
                            String[] in = input.readUTF().split(";");
                            if (in[0] == "0") {
                                client.close();
                            } else if (in[0] == "1") {
                                clientSocketHashMap.put(client, in[1]);
                            } else {
                                out.writeUTF("FALSCHER CODE");
                                client.close();
                            }
                        } else {
                            out.writeUTF(serverInfoText + ";5");
                            client.close();
                        }
                    }
                    else{

                        //TODO SERVER GAME LOGIC
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setMessage(int number, String msg){
        this.msg[number] = msg;
    }

    public void initializeGame(){
        rangfolgeSpieler = clientSocketHashMap.keySet().toArray(new Socket[clientSocketHashMap.size()]);
        for(int i = 0; i < 10; i++){
            int n1 = (int)Math.round((rangfolgeSpieler.length-1) * Math.random());
            int n2 = (int)Math.round((rangfolgeSpieler.length-1) * Math.random());
            Socket s1 = rangfolgeSpieler[n1];
            rangfolgeSpieler[n1] = rangfolgeSpieler[n2];
            rangfolgeSpieler[n2] = s1;
        }
        gameIsStarted = true;
    }

}


