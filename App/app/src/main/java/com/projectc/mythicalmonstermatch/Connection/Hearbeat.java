package com.projectc.mythicalmonstermatch.Connection;

import android.util.Log;

public class Hearbeat extends Thread {
    ServerListener sL;
    Client client;
    long startTime;
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
        if(sL.getLogin() != null) {
            while (running) {
                try {
                    if (sL != null) {
                        sL.sendMessage("heartbeat   " + sL.getLogin());
                    }
                    if (sL == null) {
                        Log.d("IOEXCEPTION", "JETZT HEARBEAT");
                    }
                    if (client != null) {
                        client.sendMessage("heartbeat");
                    }
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }




}
