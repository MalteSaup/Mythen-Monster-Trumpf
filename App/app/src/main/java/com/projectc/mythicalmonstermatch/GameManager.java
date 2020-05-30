package com.projectc.mythicalmonstermatch;

import android.os.AsyncTask;
import android.util.Log;

import com.projectc.mythicalmonstermatch.Connection.Server;
import com.projectc.mythicalmonstermatch.Connection.ServerListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameManager {

    private CardClass[] allCards;
    private ArrayList<PlayerItem> players;
    private Server server;
    private ArrayList<ServerListener> playerList;

    private int currentPlayer;

    public GameManager(CardClass[] allCards, ArrayList<PlayerItem> players, Server server){
        this.allCards = allCards;
        this.players = players;
        this.server = server;
        this.playerList = server.getServerListeners();
        currentPlayer = (int)((playerList.size()-1) * Math.random());
        //if(currentPlayer < 0){currentPlayer = 0;}
    }
/*

    public void startGame(){
        for(ServerListener sL : playerList){
            sL.sendMessage("start");
        }
    }
*/

    public void dealOutCards(){

        shuffle();
        for (int i = 0; i < allCards.length; i++){
            callAddToPlayerDeck(players.get(i % players.size()), allCards[i]); // player1 cards0 becomes allCards0, player2 cards0 becomes allCards1 etc.
        }
        for (PlayerItem p : players){
            Log.d("cardDealing", p.getUsername() + " " + p.getPlayerDeck().size());
        }

        sendCard();

    }

    private void handleMessageSend(final String msg, final ServerListener sL){
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                sL.sendMessage(msg);
                return null;
            }
        };
        asyncTask.execute();
    }

    public void compareResults(int attributeNumber){
        Log.d("ATTRIBUTENUMBER", "" + attributeNumber);
        int currentMax = 0;
        List<PlayerItem> eligiblePlayers = new ArrayList<>(); // necessary to determine participants in draw rounds

        /*for (PlayerItem player : players){
            if (player.getPartOfDrawRound()){ // a draw round where everyone is part of it, is a normal round
                eligiblePlayers.add(player);
            }
        }*/

        List<PlayerItem> currentWinners = new ArrayList<>();

        for (PlayerItem player : players) {
            Log.d("CURRENTMAX", "NOT CM BUT PLAYER" + player.getUsername());
            if (player.getCard(0).attributeMap.get("attribute" + attributeNumber) > currentMax ){   // find the highest value and assign it's index
                currentMax = player.getCard(0).attributeMap.get("attribute" + attributeNumber);
                currentWinners.clear();
                currentWinners.add(player);
                Log.d("CURRENTMAX", ""+currentMax + " " + player.getCard(0).name + " " + player.getUsername());
            }
            else if (player.getCard(0).attributeMap.get("attribute" + attributeNumber) == currentMax){ // in case it's a draw
                currentWinners.add(player);
            }
        }
        if (currentWinners.size() == 1){ // there is one winner
            for(ServerListener sL : playerList){
                if (sL.getID() == currentWinners.get(0).getId()){
                    handleMessageSend("WIN", sL);
                    //sL.sendMessage("WIN");
                    awardWinner(players.indexOf(currentWinners.get((0))));
                } else {
                    //sL.sendMessage("LOSE");
                    handleMessageSend("LOSE", sL);
                }
            }

            for (PlayerItem player : players){
                player.setPartOfDrawRound(true);
            }
        }
        else{ // draw round begins
            for (PlayerItem player : players){
                if (!currentWinners.contains(players.indexOf(player))){
                    player.setPartOfDrawRound(false);
                }
            }
        }
        nextTurn();
    }


    private void callAddToPlayerDeck(PlayerItem player, CardClass card){
        player.addToPlayerDeck(card);
    }

    private void shuffle(){ // shuffles the allCards array
        Random rand = new Random();
        for (int i = 0; i < allCards.length; i++) {
            int randomIndexToSwap = rand.nextInt(allCards.length);
            CardClass temp = allCards[randomIndexToSwap];
            allCards[randomIndexToSwap] = allCards[i];
            allCards[i] = temp;
        }
    }

    private void awardWinner(int index){
        ArrayList<PlayerItem> temp;
        temp = (ArrayList<PlayerItem>) players.clone();
        temp.remove(index);
        for (int i = 0; i < temp.size(); i++){
            Log.d("INDEX TEMP PLAYER", "" + temp.get(i) + " " + i);
            callAddToPlayerDeck(players.get(index), temp.get(i).getCard(0)); // index 0 is always the current card
            //players.remove(players.get(temp.indexOf(temp.get(i)))); // removes the card from a players deck, after it was rewarded to the winner
            players.get(i).playerDeck.remove(0);
        }
        CardClass card = players.get(index).getCard(0);
        players.get(index).playerDeck.remove(0);
        callAddToPlayerDeck(players.get(index), card);
        nextTurn();
    }

    public ArrayList<PlayerItem> getPlayers(){
        return players;
    }

    public CardClass[] getAllCards(){
        return allCards;
    }

    public PlayerItem findPlayerById(int id) {
        for (PlayerItem player : players) {
            if (player.getId() == id) {
                return player;
            }
        }
        return null;
    }

    public void determineCurrentPlayer(){
        for (PlayerItem player: players) {
            player.setAllowedToPlay(false);
        }
        int uebergabe = -1;
        if(currentPlayer < players.size()){
            if(players.get(currentPlayer).getPartOfDrawRound()){
                players.get(currentPlayer).setAllowedToPlay(true);
            }else{
                boolean currentDraw = false;
                for(int i = 0; i < players.size();i++) {
                    if(players.get(currentPlayer).getPartOfDrawRound()){
                        if(currentPlayer != i && !currentDraw){
                            if(i < currentPlayer){
                                uebergabe = i;
                            }else{
                                if(uebergabe != -1 && (!(uebergabe > currentPlayer) || !(uebergabe > i))){
                                    uebergabe = i;
                                }else if(uebergabe == -1){
                                    uebergabe = i;
                                }
                            }
                        }else{
                            uebergabe = currentPlayer;
                            currentDraw = true;
                        }
                    }
                }
            }
        }
        if(uebergabe != -1) {
            currentPlayer = uebergabe;
        }

        if(playerList.size() != 0){players.get(currentPlayer).setAllowedToPlay(true);}  //TODO NACHM MOVE PLAYERLIST LEER VLT INVESTIGATIVE CIWANARITES
    }


    public void nextTurn() {
        determineCurrentPlayer();
        sendCard();
        playerList = server.getServerListeners();
        //TODO NEXT TURN MSG AN ALLE
    }

    public void setPlayer(ArrayList<ServerListener> sLL){
        playerList = sLL;
    }

    public void sendCard(){
        for(ServerListener sL : playerList){
            for(PlayerItem pI : players){
                if(pI.getId() == sL.getID()){
                    if(pI.getId() == playerList.get(currentPlayer).getID()){
                        handleMessageSend("turn 1 " + (pI.getPlayerDeck().get(0).id), sL);
                    }else{
                        handleMessageSend("turn 0 " + (pI.getPlayerDeck().get(0).id), sL);
                    }

                }
            }
        }
    }
}
