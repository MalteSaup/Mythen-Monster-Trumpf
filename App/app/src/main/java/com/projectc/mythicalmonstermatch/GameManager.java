package com.projectc.mythicalmonstermatch;

import android.util.Log;

import com.projectc.mythicalmonstermatch.Connection.AsyncSupportClass;
import com.projectc.mythicalmonstermatch.Connection.Server;
import com.projectc.mythicalmonstermatch.Connection.ServerListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameManager {

    private CardClass[] allCards;
    private PlayerItem pool; // cards that are kept in the "middle" during a draw round
    private ArrayList<PlayerItem> players;
    private Server server;
    private ArrayList<ServerListener> playerList;

    private int currentPlayer;
    private int turnCount = 0; // a turn means one comparison

    private AsyncSupportClass supportClass;

    public GameManager(CardClass[] allCards, ArrayList<PlayerItem> players, Server server){
        this.allCards = allCards;
        this.players = players;
        this.server = server;
        this.playerList = server.getServerListeners();
        currentPlayer = (int)((playerList.size()) * Math.random());
        pool = new PlayerItem("pool", -1);
        pool.playerDeck = new ArrayList<>();
        this.supportClass = new AsyncSupportClass();
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
        allCards = new CardClass[] {};
        for (PlayerItem p : players){
            Log.d("cardDealing", p.getUsername() + " " + p.getPlayerDeck().size());
        }

        sendCard();

    }

    public void compareResults(int attributeNumber){
        Log.d("ATTRIBUTENUMBER", "" + attributeNumber);
        int currentMax = 0;
        List<PlayerItem> eligiblePlayers = new ArrayList<>(); // necessary to determine participants in draw rounds

        for (PlayerItem player : players){
            if (player.getPartOfDrawRound()){ // a draw round where everyone is part of it, is a normal round
                eligiblePlayers.add(player);
            }
        }

        List<PlayerItem> currentWinners = new ArrayList<>();

        for (PlayerItem player : eligiblePlayers) {
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

                    supportClass.sendMessage(sL, "WIN");
                    awardWinner(players.indexOf(currentWinners.get((0))));
                } else {

                    supportClass.sendMessage(sL, "LOSE");
                }
            }

            for (PlayerItem player : players){
                player.setPartOfDrawRound(true);
            }
            currentPlayer = players.indexOf(currentWinners.get(0));
        }
        else{ // draw round begins
            List<PlayerItem> drawWinners = new ArrayList<>();

            for (PlayerItem player : players){
                if (currentWinners.contains(player)){
                    drawWinners.add(player);
                }
                else{
                    player.setPartOfDrawRound(false);
                }

                for(ServerListener sL : playerList){
                    supportClass.sendMessage(sL, "DRAW");
                }

                callAddToPlayerDeck(pool, player.getCard(0));
                player.playerDeck.remove(0);
            }
            Log.d("alooah", "" +drawWinners.size());
            currentPlayer = players.indexOf(drawWinners.get((int) (drawWinners.size()*Math.random())));
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
        Log.d("ciwo", "" + players.get(index).getUsername());
        ArrayList<PlayerItem> temp;
        temp = (ArrayList<PlayerItem>) players.clone();

        for (int i = 0; i < temp.size(); i++){
            Log.d("INDEX TEMP PLAYER", "" + temp.get(i) + " " + i);
            if (i != index){
                callAddToPlayerDeck(players.get(index), temp.get(i).getCard(0)); // index 0 is always the current card
                players.get(i).playerDeck.remove(0);
            }
        }
        if (pool.playerDeck.size() > 0){ // if someone wins after a draw round they win all the cards in the pool
            for (CardClass c : pool.playerDeck){
                callAddToPlayerDeck(players.get(index), c);
            }
            pool.playerDeck.clear();
        }
        callAddToPlayerDeck(players.get(index), players.get(index).getCard(0)); // the card that won the round gets
        players.get(index).playerDeck.remove(0);                                // sent to the back of the deck
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

        if (players.size() == 1){ // one person being left means they are the winner
            for(ServerListener sL : playerList){
                for(PlayerItem pI : players){
                    if(pI.getId() == sL.getID()){
                        supportClass.sendMessage(sL, "totalWin " + turnCount);
                    }
                }
            }
        }
        else{
            Log.d("tcount", "" + turnCount);
            turnCount += 1;
        }

        //TODO NEXT TURN MSG AN ALLE
    }

    public void setPlayer(ArrayList<ServerListener> sLL){
        playerList = sLL;
    }

    public void sendCard(){
        for(ServerListener sL : playerList){
            for(PlayerItem pI : players){
                if(pI.getId() == sL.getID()){
                    if (pI.playerDeck.size() == 0){ // player has no cards left and has therefore lost
                        supportClass.sendMessage(sL, "totalLose " + getTurnCount());
                        players.remove(pI);
                        break;
                    }
                    else if(pI.getId() == playerList.get(currentPlayer).getID()){
                        supportClass.sendMessage(sL, "turn 1 " + (pI.getPlayerDeck().get(0).id));
                    }else{
                        supportClass.sendMessage(sL, "turn 0 " + (pI.getPlayerDeck().get(0).id));
                    }

                }
            }
        }
    }

    public int getTurnCount(){
        return turnCount;
    }
}
