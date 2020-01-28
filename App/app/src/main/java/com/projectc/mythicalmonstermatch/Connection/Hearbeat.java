package com.projectc.mythicalmonstermatch.Connection;

import android.util.Log;

public class Hearbeat extends Thread {
    ServerListener sL = null;
    Client client = null;
    public boolean running = true;

    public Hearbeat(ServerListener sL) {

        this.sL = sL;

    }

    public Hearbeat(Client client){

        this.client = client;

    }

    @Override
    public void run() {
        Log.d("HEARTBEAT", "  XXX" + sL.getLogin());
        if(sL != null) {
            if(sL.getLogin() != null){
                while (running) {
                    try {
                        if (sL != null) {
                            sL.sendMessage("heartbeat");
                            if(sL.getLogin().equals("localhost")){
                                running = false;
                            }
                        }
                        this.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if(client != null){
            while(running){
                try {
                    if (client != null) {
                        client.sendMessage("heartbeat " + client.getLogin());
                    }
                    this.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }




}
