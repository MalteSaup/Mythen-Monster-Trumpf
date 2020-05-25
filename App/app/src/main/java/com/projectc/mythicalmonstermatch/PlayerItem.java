package com.projectc.mythicalmonstermatch;

import android.util.Log;

import java.util.ArrayList;

public class PlayerItem {

    private boolean allowedToPlay; // used to determine eligible players in draw rounds
    private boolean partOfDrawRound = true;
    public ArrayList<CardClass> playerDeck;

    private String username;
    private int id;
    private int selectedState;

    public PlayerItem(String username, int id){
        this.username = username;
        this.id = id;
        playerDeck = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }
    public int getId(){return id;}

    public void addToPlayerDeck(CardClass card){
        playerDeck.add(card);
    }

    public CardClass getCard(int index){
        Log.d("INDEX TEMP CARD", "" + playerDeck.size() + " " + index);
        return playerDeck.get(index);
    }

    public void setAllowedToPlay(boolean allowed){
        allowedToPlay = allowed;
    }

    public boolean getAllowedToPlay(){
        return allowedToPlay;
    }

    public void setPartOfDrawRound(boolean allowed) { partOfDrawRound = allowed;}

    public boolean getPartOfDrawRound(){return partOfDrawRound;}

    public ArrayList<CardClass> getPlayerDeck() {
        return playerDeck;
    }

    public void setSelectedStat(int selectedState) {
        this.selectedState = selectedState;
    }

    public int getSelectedStat() {
        return selectedState;
    }
}
