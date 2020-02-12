package com.projectc.mythicalmonstermatch;

import java.util.ArrayList;

public class PlayerItem {

    private boolean allowedToPlay; // used to determine eligible players in draw rounds
    private ArrayList<CardClass> playerDeck;

    private String username;
    private int id;
    private int selectedStat;

    public PlayerItem(String username, int id){
        this.username = username;
        this.id = id;
        playerDeck = new ArrayList<CardClass>();
    }

    public String getUsername() {
        return username;
    }
    public int getId(){return id;}
    public void setUsername(String username){
        this.username = username;
    }

    public void addToPlayerDeck(CardClass card){
        playerDeck.add(card);
    }

    public CardClass getCard(int index){
        return playerDeck.get(index);
    }

    public void setAllowedToPlay(boolean allowed){
        allowedToPlay = allowed;
    }

    public boolean getAllowedToPlay(){
        return allowedToPlay;
    }

    public ArrayList<CardClass> getPlayerDeck() {
        return playerDeck;
    }

    public void setSelectedStat(int selectedStat) {
        this.selectedStat = selectedStat;
    }

    public int getSelectedStat() {
        return selectedStat;
    }
}
