package com.projectc.mythicalmonstermatch;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ServerMain extends Thread {

    public ServerSocket serverSocket;
    public Socket[] clientSockets = new Socket[5];
    public HashMap<Socket, String> clientSocketHashMap = new HashMap<>();
    public String serverInfoText;
    final int SocketServerPORT = 8080;

    public ServerMain(String name){
        serverInfoText = name;
    }


    public void runningServer(){
        if(serverSocket == null){
            try {
                serverSocket = new ServerSocket(SocketServerPORT);

                while(true){
                    boolean gotConnected = false;
                    int number = 0;
                    for(int i = 0; i < 5; i++){
                        if(clientSockets[i] == null){
                            clientSockets [i] = serverSocket.accept();
                            gotConnected = true;
                            number = i;
                        }
                    }
                    if(!gotConnected){
                        Socket client = serverSocket.accept();
                        DataOutputStream out = new DataOutputStream(client.getOutputStream());
                        out.writeUTF(serverInfoText + ";5");
                        client.close();
                    }else{
                        DataOutputStream output = new DataOutputStream(clientSockets[number].getOutputStream());
                        DataInputStream input = new DataInputStream(clientSockets[number].getInputStream());
                        output.writeUTF(serverInfoText + ";" + clientSocketHashMap.size());
                        String[] in = input.readUTF().split(";");
                        if(in[0] == "0") {
                            clientSockets[number].close();
                            clientSockets[number] = null;
                        }
                        else if(in[0] == "1"){
                            clientSocketHashMap.put(clientSockets[number], in[1]);
                        }
                        else{
                            output.writeUTF("FEHLER FALSCHER SERVER");
                            clientSockets[number].close();
                            clientSockets[number] = null;
                        }

                    }


                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


