package com.projectc.mythicalmonstermatch;

public class ServerItem {

    private String address;
    private String servername;
    private int playerCount;

    public ServerItem(String servername, int playercount, String address){
        this.servername = servername;
        this.playerCount = playercount;
        this.address = address;
    }

    public String getServername() {
        return servername;
    }

    public int getPlayerCount(){
        return playerCount;
    }

    public String getAddress(){return address;}
}
