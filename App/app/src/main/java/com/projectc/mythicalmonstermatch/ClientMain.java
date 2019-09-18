package com.projectc.mythicalmonstermatch;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ClientMain extends Thread {

    private String name;
    private String address;
    private int port = 8080;
    private Socket socket;
    private boolean wantsJoin = false;

    public ClientMain(String name, boolean wantsJoin) {
        this.name = name;
        this.wantsJoin = wantsJoin;
    }

    @Override
    public void run(){
        if(socket == null){
            try{
                ArrayList<String> deviceList = WifiHelper.getDeviceList();
                if(deviceList.size() > 0){
                    address = deviceList.get(0);
                    if(address != null){
                        socket = new Socket(address, port);
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        DataInputStream in = new DataInputStream(socket.getInputStream());
                        while(true){
                           if(socket.isClosed()){
                                Log.d("CONNECTION", "CONNECTION IS CLOSED");
                                break;
                           }
                           String[] input = in.readUTF().split(";");
                           if(wantsJoin){
                                out.writeUTF("1;" + name);
                           }
                           else{
                               out.writeUTF("0;" + name);
                               //TODO LISTE FÜR VORHANDENE SPIELE HINZUFÜGEN, ABER NUR NICE TO HAVE ERSTMAL
                           }
                       }
                    }
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
