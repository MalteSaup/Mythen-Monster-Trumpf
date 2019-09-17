package com.projectc.mythicalmonstermatch;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ClientMain extends Thread {

    private String name;
    private String address;
    private int port = 8080;
    private Socket socket;


    public ClientMain(String name) {
        this.name = name;
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
                        if(socket.isConnected()){

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
