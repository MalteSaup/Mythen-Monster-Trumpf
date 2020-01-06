package com.projectc.mythicalmonstermatch;

public class PlayerItem {

    private String username;
    private int id;

    public PlayerItem(String username, int id){
        this.username = username;
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    public int getId(){return id;}
}
