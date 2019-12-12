package com.projectc.mythicalmonstermatch;

import java.util.ArrayList;

public class PlayerItem {

    private boolean isAllowedToPlay; // used to determine eligible players in draw rounds
    private ArrayList<CardClass> playerDeck;

    private String username;

    public PlayerItem(String username){
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

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
        isAllowedToPlay = allowed;
    }
}
