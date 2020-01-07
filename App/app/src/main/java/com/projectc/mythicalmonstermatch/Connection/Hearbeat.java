package com.projectc.mythicalmonstermatch.Connection;

public class Hearbeat extends Thread {
    ServerListener sL;
    Client client;
    long startTime;
    boolean started = false;

    public Hearbeat(ServerListener sL) {

        this.sL = sL;

    }

    public Hearbeat(Client client){

        this.client = client;

    }

    @Override
    public void run() {
        while (true) {
            if(sL != null){sL.sendMessage("heartbeat");}
            if(client != null){client.sendMessage("heartbeat");}
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }




}
