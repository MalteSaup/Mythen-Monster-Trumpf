package com.projectc.mythicalmonstermatch;

public class ServerItem {

    private String address;
    private String servername;
    private int playerCount;
    private int startState;

    public ServerItem(String servername, int playercount, String address, int startState){
        this.servername = servername;
        this.playerCount = playercount;
        this.address = address;
        this.startState = startState;
    }

    public String getServername() {
        return servername;
    }

    public int getPlayerCount(){
        return playerCount;
    }

    public String getAddress(){return address;}

    public int getStartState() {return startState;}
}
