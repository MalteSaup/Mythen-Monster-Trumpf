package com.projectc.mythicalmonstermatch;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class ServerSendClass extends Thread {

    Socket socket;
    String msg;

    public ServerSendClass(Socket socket, String msg){
        this.socket = socket;
        this.msg = msg;
    }

    @Override
    public void run(){
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
