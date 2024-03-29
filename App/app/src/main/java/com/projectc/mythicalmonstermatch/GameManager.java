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
    private int playersRemaining;
    private PlayerItem pool; // cards that are kept in the "middle" during a draw round
    private ArrayList<PlayerItem> players;
    private Server server;
    private ArrayList<ServerListener> playerList;

    private int currentPlayer;
    private int turnCount = 1; // a turn means one comparison

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
        playersRemaining = players.size();
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
        shufflePlayers();
        for (int i = 0; i < allCards.length; i++){

            callAddToPlayerDeck(players.get(i % players.size()), allCards[i]); // player1 cards0 becomes allCards0, player2 cards0 becomes allCards1 etc.
        }
        allCards = new CardClass[] {};
        for (PlayerItem p : players){
            Log.d("cardDealing", p.getUsername() + " " + p.getPlayerDeck().size());
        }

        unshufflePlayers();

        sendCard();
        //TODO play the bring player to front animation

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

        //TODO play card flip animation for everyone, so that every player can see all the open cards

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
                String enemyCardIDs = buildEnemyCardIDs(sL, currentWinners);

                if (sL.getID() == currentWinners.get(0).getId()){

                    Log.d("gezwirbel", enemyCardIDs + "win");
                    supportClass.sendMessage(sL, "WIN");
                    supportClass.sendMessage(sL,"compared 1 " + currentWinners.get(0).getId()+ " " + attributeNumber+":1" + enemyCardIDs);

                } else {

                    if (!players.get(playerList.indexOf(sL)).getHasLost()){

                        Log.d("gezwirbel", enemyCardIDs + "lose");
                        Log.d("tokens", ""+(players.get(playerList.indexOf(sL)).playerDeck.get(players.get(playerList.indexOf(sL)).playerDeck.size()-1).id));
                        if (players.get(playerList.indexOf(sL)).getPartOfDrawRound()){
                            supportClass.sendMessage(sL, "LOSE");
                        }

                        supportClass.sendMessage(sL,"compared 0 " + currentWinners.get(0).getId()+ " " + attributeNumber+":0" + enemyCardIDs);
                    }
                }
            }
            awardWinner(players.indexOf(currentWinners.get((0))));

            for (PlayerItem player : players){
                player.setPartOfDrawRound(true);
            }
            currentPlayer = players.indexOf(currentWinners.get(0));
        }
        else{ // draw round begins

            String drawWinnerIDS = "";
            for (int i = 0; i < currentWinners.size(); i++){
                drawWinnerIDS += currentWinners.get(i).getId();
                if (i < currentWinners.size()-1){
                    drawWinnerIDS+=":";
                }
            }

            for(ServerListener sL : playerList){
                if (!players.get(playerList.indexOf(sL)).getHasLost()) {

                    String enemyCardIDs = buildEnemyCardIDs(sL, currentWinners);
                    supportClass.sendMessage(sL, "DRAW");
                    String message = "compared 2 " + drawWinnerIDS + " " + attributeNumber;
                    if (currentWinners.contains(players.get(playerList.indexOf(sL)))){
                        message+=":1";
                    }
                    else{
                        message+=":0";
                    }
                    message+=enemyCardIDs;
                    supportClass.sendMessage(sL, message);
                }
            }
            List<PlayerItem> drawWinners = new ArrayList<>();

            for (PlayerItem player : players){
                if (!player.getHasLost() && player.getPartOfDrawRound()){
                    callAddToPlayerDeck(pool, player.getCard(0));
                    player.playerDeck.remove(0);
                    if (player.playerDeck.size() == 0){
                        currentWinners.remove(player);
                    }
                }
                if (currentWinners.contains(player)){
                    drawWinners.add(player);
                }
                else{
                    player.setPartOfDrawRound(false);
                }



            }

            if (currentWinners.size() == 1){
                awardWinner(players.indexOf(currentWinners.get(0)));
                for (PlayerItem player : players){
                    player.setPartOfDrawRound(true);
                }
                currentPlayer = players.indexOf(currentWinners.get(0));
            }

            else if (!(currentWinners.contains(players.get(currentPlayer)))){ // if the current player caused the draw, they should be allowed to pick the next card,
                currentPlayer = players.indexOf(drawWinners.get((int) (drawWinners.size()*Math.random()))); //if not, a randomly determined player of the elligible players should be allowed
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

    private void shufflePlayers(){
        Random rand = new Random();
        for (int i = 0; i < players.size(); i++) {
            int randomIndexToSwap = rand.nextInt(players.size());
            PlayerItem temp = players.get(randomIndexToSwap);
            players.set(randomIndexToSwap, players.get(i));
            players.set(i,temp);
        }
    }

    private void unshufflePlayers(){
        for (ServerListener sL : playerList){
            for (PlayerItem pI : players){
                if (sL.getID() == pI.getId()){
                    int oldPosition = players.indexOf(pI);
                    PlayerItem temp = players.get(playerList.indexOf(sL));
                    players.set(playerList.indexOf(sL), pI);
                    players.set(oldPosition, temp);
                }
            }
        }
    }

    private void awardWinner(int index){
        Log.d("ciwo", "" + players.get(index).getUsername());
        ArrayList<PlayerItem> temp;
        temp = (ArrayList<PlayerItem>) players.clone();

        for (int i = 0; i < temp.size(); i++){
            Log.d("INDEX TEMP PLAYER", "" + temp.get(i) + " " + i);
            if (i != index && players.get(i).getPartOfDrawRound()){
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

        playerList = server.getServerListeners();

        /*
        Log.d("deckcontent", players.size() + "");
        Log.d("deckcontent", players.get(0).getUsername() + " | " + players.get(1).getUsername() + " | " + players.get(2).getUsername());
        Log.d("deckcontent", players.get(0).playerDeck.size() + " | " + players.get(1).playerDeck.size() + " | " + players.get(2).playerDeck.size());
        */
        for (ServerListener sL : playerList){
            for (PlayerItem pI : players){
                if (pI.getId() == sL.getID()){
                    if (pI.playerDeck.size() == 0){ // player has no cards left and has therefore lost
                        if (!pI.getHasLost()){
                            supportClass.sendMessage(sL, "totalLose " + getTurnCount());
                            players.get(players.indexOf(pI)).setHasLost(true);
                            Log.d("inspektion", playersRemaining + "");
                            playersRemaining -=1 ;
                            Log.d("inspektion", playersRemaining + "");
                        }
                        players.get(players.indexOf(pI)).setPartOfDrawRound(false);
                    }
                }
            }
        }

        if (playersRemaining == 1){ // one person being left means they are the winner
            for(ServerListener sL : playerList){
                for(PlayerItem pI : players){
                    if((pI.getId() == sL.getID()) && !(pI.getHasLost())){
                        supportClass.sendMessage(sL, "totalWin " + turnCount);
                    }
                }
            }
            Log.d("Ende", "" + playersRemaining);
            resetAll();
        }
        else{
            while(players.get(currentPlayer).getHasLost()){ // in the unlikely event that a player loses his last card by having the highest value in a draw currentPlayer
                currentPlayer += 1;                         // has to be set to a player who is still in the game, while this method is not very random it should work
                if (currentPlayer >= players.size()){
                    currentPlayer = 0;
                }
            }

            Log.d("tcount", "" + turnCount);
            turnCount += 1;
        }
        sendCard();
        //TODO NEXT TURN MSG AN ALLE
    }

    public void setPlayer(ArrayList<ServerListener> sLL){
        playerList = sLL;
    }

    public void sendCard(){
        for(ServerListener sL : playerList){
            for(PlayerItem pI : players){
                if(pI.getId() == sL.getID() && !pI.getHasLost()){

                    if (pI.getId() == playerList.get(currentPlayer).getID()){
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

    public void resetAll(){
        allCards = new CardClass[0];
        pool = null;
        players.clear();

        currentPlayer = -1;
        turnCount = -1;
        supportClass = null;

    }

    public String buildEnemyCardIDs(ServerListener sL, List<PlayerItem> currentWinners){
        int[] cardIDs = new int[players.size()];
        for (int i = 0; i < cardIDs.length; i++){
            if (playerList.get(i).getID() != sL.getID()){
                if(players.get(i).playerDeck.size() > 0 && players.get(i).getPartOfDrawRound()){
                    cardIDs[i] = players.get(i).getCard(0).id;
                }
                else{
                    cardIDs[i] = -1;
                }
            }
            Log.d("arrayprüfung", cardIDs[i] + "");
        }
        String enemyCardIDs = "";
        for (int i = 0; i < cardIDs.length; i++){
            if (playerList.get(i).getID() != sL.getID()){
                enemyCardIDs += " " + cardIDs[i];
                if(currentWinners.contains(players.get(i))){
                    enemyCardIDs+=":1";
                }
                else{
                    enemyCardIDs+=":0";
                }
            }
        }
        return enemyCardIDs;
    }
}
