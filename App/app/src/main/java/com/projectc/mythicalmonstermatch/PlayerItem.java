package com.projectc.mythicalmonstermatch;

import java.util.ArrayList;

public class PlayerItem {

    private boolean isAllowedToPlay; // used to determine eligible players in draw rounds
    private ArrayList<CardClass> playerDeck;

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
