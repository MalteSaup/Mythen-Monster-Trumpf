package com.projectc.mythicalmonstermatch;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class GameManager {
    private CardClass[] allCards;
    private ArrayList<PlayerItem> players;
    private List<Integer> tempResults;
    private Dictionary playerAndResult;

    public GameManager(CardClass[] allCards, ArrayList<PlayerItem> players){
        this.allCards = allCards;
        this.players = players;
    }

    public void pushResult(int result, PlayerItem player){
        playerAndResult.put(result, player);
        tempResults.add(result);
        if (playerAndResult.size() == players.size()){ // all players have selected a value
            compareResults(tempResults, playerAndResult);
        }
    }

    public void dealOutCards(){

        shuffle();
        for (int i = 0; i < allCards.length; i++){
            callAddToPlayerDeck(players.get(i % players.size()), allCards[i]); // player1 cards0 becomes allCards0, player2 cards0 becomes allCards1 etc.
        }
    }

    public void compareResults(List<Integer> results, Dictionary playerAndIndex){
        int currentMax = 0;
        List<Integer> currentWinners = new ArrayList<>();

        for (int i: results) {
            if (results.get(i) > currentMax ){   // find the highest value and assign it's index
                currentMax = results.get(i);
                currentWinners.clear();
                currentWinners.add(i);
            }
            else if (results.get(i) == currentMax){ // in case it's a draw
                currentWinners.add(i);
            }
        }
        if (currentWinners.size() > 1){ // winner gets awarded
            awardWinner(players.indexOf(playerAndIndex.get(currentWinners.get(0))));
        }
        else{ // draw round begins
            for (PlayerItem player : players){
                if (!currentWinners.contains(players.indexOf(player))){
                    player.setAllowedToPlay(false);
                }
            }
        }
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
        temp = players;
        temp.remove(index);
        for (int i = 0; i < temp.size(); i++){
            callAddToPlayerDeck(players.get(index), temp.get(i).getCard(0)); // index 0 is always the current card
            players.remove(players.get(temp.indexOf(temp.get(i)))); // removes the card from a players deck, after it was rewarded to the winner
        }
    }

    public ArrayList<PlayerItem> getPlayers(){
        return players;
    }
}