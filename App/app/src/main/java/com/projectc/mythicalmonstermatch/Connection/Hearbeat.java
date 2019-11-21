package com.projectc.mythicalmonstermatch.Connection;

public class Hearbeat extends Thread {
    ServerListener sL;

    long startTime;
    boolean started = false;

    public Hearbeat(ServerListener sL) {

        this.sL = sL;

    }

    @Override
    public void run(){
        while(true){
            if(!started){
                started = true;
                startTime = System.currentTimeMillis();
            } else{
            }
        }
    }




}
