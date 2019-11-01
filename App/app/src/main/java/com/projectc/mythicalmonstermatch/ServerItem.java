package com.projectc.mythicalmonstermatch;

public class ServerItem {

    private String servername;
    private int playerCount;

    public ServerItem(String servername, int playercount){
        this.servername = servername;
        this.playerCount = playercount;
    }

    public String getServername() {
        return servername;
    }

    public int getPlayerCount(){
        return playerCount;
    }
}
