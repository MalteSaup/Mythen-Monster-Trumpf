package com.projectc.mythicalmonstermatch.Fragments;

import android.animation.Animator;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.projectc.mythicalmonstermatch.AnimationHolder;
import com.projectc.mythicalmonstermatch.CardAnimator;
import com.projectc.mythicalmonstermatch.Connection.ServerListener;
import com.projectc.mythicalmonstermatch.GameActivity;
import com.projectc.mythicalmonstermatch.PlayerItem;
import com.projectc.mythicalmonstermatch.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class GameFragment extends Fragment {

    private Button compareButton;
    private PlayerItem myPlayerItem;

    private ImageView imageView;
    private ImageView playerBackground;
    private ImageView deckImageView;

    private View playerFrag;
    private View playerDeckFrag;
    private View[] enemieFrags;
    private View[] enemieAnimationFrags;
    private View[] enemieDeck;
    private View view;
    private TableRow[] tableRows;


    private GameActivity gA;
    //private MainActivity gA;
    private boolean playerCardAnimationPlayed = false;
    private boolean[] enemieAnimationDirection;
    private boolean colorWasChanged = false;
    private boolean background = true;
    private boolean changed = false;
    private boolean isPlaying = false;

    private TextView[] enemieTextViews[];
    private ImageView[] enemieImageViews[];
    private TextView[] playerTextViews;
    private TextView[] playerDeckTextViews;
    private Button submitBtn;

    private TextView[] enemieAnimTextViews[];
    private ImageView[] enemieAnimImageViews[];

    private ImageView winLoseScreen;

    private int[] playerID;

    private int[] game_fragments = new int[]{
            R.layout.fragment_game_2,
            R.layout.fragment_game_3,
            R.layout.fragment_game_4,
            R.layout.fragment_game_5
    };
    private int playerCount; //HARDCODED



    private int ownCard;
    private int turnCount;
    private int winOrLoss; // 1 = win, 0 = loss
    private boolean hasWonOrLost; // if this is true the endscreen will be created after the compare animation

    private int[] enemyCardsToDisplay;
    private int winnerID;
    private String winnersToBeMarked;

    private ArrayList<Integer> winnersToDisplay;


    private int attributeToCheck;

    private CardAnimator cardAnimator;

    private AnimationHolder playerAnimation;
    private AnimationHolder playerDeckAnimation;
    private AnimationHolder[] enemieAnimations[];
    private AnimationHolder[] enemieAnimAnimations;
    private TextView[][] enemieDeckTV;
    private ImageView[][] enemieDeckIV;
    private AnimationHolder[][] enemieDeckAS;

    private int green = Color.parseColor("#c9c6c5");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("GAMEFRAGSTART", "ALLA");
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("GAMEFRAGSTART", "START12");

        //ACTIVITY HOLEN UND SPIELERZAHL AUSLESEN
        gA = (GameActivity) getActivity();
        playerCount = gA.playerItems.size();
        enemyCardsToDisplay = new int[playerCount];
        winnersToDisplay = new ArrayList<>();
        //playerCount = gA.server.playerCount();
        /*switch (playerCount){
            case 2: return inflater.inflate(game_fragments[0], container, false);
            case 3: return inflater.inflate(game_fragments[1], container, false);
            case 4: return inflater.inflate(game_fragments[2], container, false);
            case 5: return inflater.inflate(game_fragments[3], container, false);
        }*/
        Log.d("PLAYERCOUNT", "" + playerCount);
        return inflater.inflate(game_fragments[playerCount-2], container, false);//HARDCODED
    }

    @Override
    public void onActivityCreated(Bundle saveInstandesState) {
        gA = (GameActivity) getActivity();
        //gA = (MainActivity) getActivity();
        gA.gameFragment = this;
        view = getView();

        cardAnimator = new CardAnimator(gA);

        initializePlayerFrag();
        initializeEnemieFrags();
        Log.d("GAMEFRAGSTART", "START");
       // showBackground(background);

        winLoseScreen = gA.findViewById(R.id.win_lose_screen);
        winLoseScreen.setVisibility(View.GONE);



        if(gA.code == 0){
            initAndSortIDArray();
            gA.gameManager.dealOutCards();


            myPlayerItem = gA.gameManager.findPlayerById(getId());

            gA.gameManager.determineCurrentPlayer();
        }
        //TODO ENEMIE FRAG BACKGROUND IMAGE TO BACKSITE OF CARD

        if (gA.code == 1){
            //TODO das eigene playerItem vom gameManager bekommen
        }

        //Log.d("HEIGHT", " " + enemieFrags[0].getMeasuredHeight() + " " + enemieFrags[0].getHeight());
        AsyncTask asyncTask = new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] objects) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                gA.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        createEnemieAnimations();
                        if(playerCount > 3){
                            deactivateAnimFrags();
                        }
                    }
                });

                return null;
            }
        };
        asyncTask.execute();

        AsyncTask animAsyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    Thread.sleep(500);
                } catch (Exception e){
                    System.out.println(e);
                }

                gA.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showCard();
                        gA.updatePlayer(ownCard);
                    }
                });
                return null;
            }
        };
        animAsyncTask.execute();
        super.onActivityCreated(saveInstandesState);
    }

    public void initAndSortIDArray(){
        ArrayList<ServerListener> sLL = gA.server.getServerListeners();
        playerID = new int[sLL.size()];
        Log.d("playeridcheck", "" + playerID.length);
        for(int i = 0; i < sLL.size(); i++){
            playerID[i] = sLL.get(i).getID();
        }
        int uebergabe = 0;
        for(int i = 0; i < playerID.length; i++){
            if(playerID[i] == gA.id){
                if(i == 0){
                    break;
                }
                uebergabe = i;
                break;
            }
        }
        if(uebergabe != 0){
            int id = playerID[0];
            playerID[0] = playerID[uebergabe];
            playerID[uebergabe] = id;
        }
        //gA.gameManager.setPlayer(sLL);
    }

    private void deactivateAnimFrags() {
        for(View enemyFrags : enemieAnimationFrags){
            enemyFrags.setAlpha(0);
        }
    }

    private void initializeEnemieFrags(){

        Resources res = getResources();
        String enemyString = "enemy_fragment";
        String enemyDeckString = "enemy_deck_fragment";

        enemieFrags = new View[playerCount-1];
        enemieTextViews = new TextView[playerCount-1][];
        enemieImageViews = new ImageView[playerCount-1][];
        enemieAnimations = new AnimationHolder[playerCount-1][];


        enemieDeck = new View[playerCount-1];
        enemieDeckTV = new TextView[playerCount-1][];
        enemieDeckIV = new ImageView[playerCount-1][];
        enemieDeckAS = new AnimationHolder[playerCount-1][];



        enemieAnimationDirection = new boolean[playerCount-1];

        for(int i = 0; i < playerCount-1; i++){
            String uebergabe = enemyString + (i+1);
            String uebergabe2 = enemyDeckString + (i+1);
            int id = res.getIdentifier(uebergabe, "id", gA.getPackageName());
            enemieFrags[i] = view.findViewById(id);
            Log.d("SCHONWIEDER", uebergabe + " " + id + " " + enemieFrags[i]);
            enemieTextViews[i] = new TextView[]{
                    enemieFrags[i].findViewById(R.id.cardName),
                    enemieFrags[i].findViewById(R.id.attribut),
                    enemieFrags[i].findViewById(R.id.attributeWert)
            };
            enemieImageViews[i] = new ImageView[]{
                    enemieFrags[i].findViewById(R.id.background),
                    enemieFrags[i].findViewById(R.id.imageView),
                    enemieFrags[i].findViewById(R.id.attributeIcon)
            };
            id = res.getIdentifier(uebergabe2, "id", gA.getPackageName());
            enemieDeck[i] = view.findViewById(id);
            Log.d("SCHONWIEDER", uebergabe + " " + id + " " + enemieFrags[i]);
            enemieDeckTV[i] = new TextView[]{
                    enemieDeck[i].findViewById(R.id.cardName),
                    enemieDeck[i].findViewById(R.id.attribut),
                    enemieDeck[i].findViewById(R.id.attributeWert)
            };
            enemieDeckIV[i] = new ImageView[]{
                    enemieDeck[i].findViewById(R.id.background),
                    enemieDeck[i].findViewById(R.id.imageView),
                    enemieDeck[i].findViewById(R.id.attributeIcon)
            };
            enemieDeck[i].setAlpha(0.0f);


        }
        if(playerCount > 3){
            enemieAnimationFrags = new View[playerCount-1];
            enemieAnimTextViews = new TextView[playerCount-1][];
            enemieAnimImageViews = new ImageView[playerCount-1][];
            enemieAnimAnimations = new AnimationHolder[playerCount-1];
            String enemyAnimString = "enemy_fragment_great";
            for(int i = 0; i < playerCount-1; i++){
                enemieFrags[i].setBackgroundResource(R.drawable.template3);
                String uebergabe = enemyAnimString + (i+1);
                int id = res.getIdentifier(uebergabe, "id", gA.getPackageName());
                enemieAnimationFrags[i] = view.findViewById(id);
                Log.d("ENEMYANIM", "" + enemieAnimationFrags[i]);
                enemieAnimTextViews[i] = new TextView[]{
                        enemieAnimationFrags[i].findViewById(R.id.cardName),
                        enemieAnimationFrags[i].findViewById(R.id.attribut),
                        enemieAnimationFrags[i].findViewById(R.id.attributeWert)
                };
                enemieAnimImageViews[i] = new ImageView[]{
                        enemieAnimationFrags[i].findViewById(R.id.background),
                        enemieAnimationFrags[i].findViewById(R.id.imageView)
                };


            }
        }
    }

    public void createEnemieAnimations(){
        for(int i = 0; i < playerCount-1; i++){


            AnimationHolder cardFlipAnimation = cardAnimator.createCardFlip(enemieFrags[i]);
            AnimationHolder onClickAnimation = null;
            AnimationHolder onClickAnimAnimator;
            switch (playerCount){
                case 2:
                    onClickAnimation = cardAnimator.createSingleEnemyCardAnimation(enemieFrags[i]);
                    break;
                case 3:
                    onClickAnimation = cardAnimator.createTwoEnemyCardAnimation(enemieFrags[i], i);
                    break;
                case 4:
                    onClickAnimation = cardAnimator.createThreeEnemyCardAnimation(enemieFrags[i], i, false);
                    onClickAnimAnimator = cardAnimator.createThreeEnemyCardAnimation(enemieAnimationFrags[i], i, true);
                    enemieAnimAnimations[i] = onClickAnimAnimator;
                    break;
                case 5:
                    onClickAnimation = cardAnimator.createFourEnemyCardAnimation(enemieFrags[i], i, false);
                    onClickAnimAnimator = cardAnimator.createFourEnemyCardAnimation(enemieAnimationFrags[i], i, true);
                    enemieAnimAnimations[i] = onClickAnimAnimator;
                    break;
            }
            enemieAnimations[i] = new AnimationHolder[]{
                    onClickAnimation,
                    cardFlipAnimation
            };

            createDeckAnimation(enemieDeck[i], i, enemieFrags[i], cardFlipAnimation);

            final int finalI = i;
            enemieFrags[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!background){
                        if (!enemieAnimationDirection[finalI]) {
                            checkIfAnimationsAreActive();
                            enemieFrags[finalI].bringToFront();
                            enemieAnimations[finalI][1].start();
                            if(playerCount > 3){
                                Log.d("ENEMYANIM", "THEORIE" + enemieAnimationFrags[finalI]);
                                enemieAnimationFrags[finalI].bringToFront();
                                enemieAnimAnimations[finalI].start();
                            }
                        } else {
                            enemieAnimations[finalI][0].reverse();
                            if(playerCount > 3){enemieAnimAnimations[finalI].reverse();}
                        }
                        enemieAnimationDirection[finalI] = !enemieAnimationDirection[finalI];
                    }
                }
            });
            if(playerCount > 3){
                enemieAnimationFrags[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!background){
                            if (!enemieAnimationDirection[finalI]) {
                                checkIfAnimationsAreActive();
                                enemieFrags[finalI].bringToFront();
                                enemieAnimations[finalI][0].start();
                                enemieAnimAnimations[finalI].start();
                            } else {
                                enemieAnimations[finalI][0].reverse();
                                enemieAnimAnimations[finalI].reverse();
                            }
                            enemieAnimationDirection[finalI] = !enemieAnimationDirection[finalI];
                        }
                    }
                });
            }
        }
    }

    public void flipAllCards(boolean direction){        //TRUE => Aufdecken, FALSE => Verdecken
        for(int i = 0; i < playerCount - 1; i++){
            Log.d("VIEW",   " " + i);
            if(enemyCardsToDisplay[i] != -1){
                if(direction){
                    enemieAnimations[i][1].reverse();
                } else {
                    enemieAnimations[i][1].start();
                }
            }
        }
        background = !direction;
    }

    public void roundEnd(){
        isPlaying = true;
        gA.runOnUiThread(new Runnable() {
            public void run(){
                for (int i = 0; i < playerCount-1; i++){
                    Log.d("tokens2",""+enemyCardsToDisplay[i]);
                    Log.d("stupide", winnersToBeMarked + " | " + winnersToBeMarked.charAt(i+1) + " " + ('1'== winnersToBeMarked.charAt(i+1)));
                    updateEnemieFrag(i, enemyCardsToDisplay[i], attributeToCheck, ('1'==winnersToBeMarked.charAt(i+1)));
                }
            }
        });



        AsyncTask bringCardToBack = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    Thread.sleep(500);
                } catch (Exception e){
                    System.out.println(e);
                }

                gA.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showCard();
                    }
                });
                return null;
            }
        };
        bringCardToBack.execute();

        AsyncTask flipCardsOpen = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    Thread.sleep(500);
                } catch (Exception e){
                    System.out.println(e);
                }

                gA.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        flipAllCards(true);
                    }
                });

                return null;
            }
        };
        flipCardsOpen.execute();

        AsyncTask markWinners = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {

                try {
                    Thread.sleep(1000);
                } catch (Exception e){
                    System.out.println(e);
                }

                gA.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (winnersToBeMarked.charAt(0)=='1'){
                            playerTextViews[0].setBackgroundColor(Color.GREEN);
                        }
                    }
                });
                try {
                    Thread.sleep(3000);
                } catch (Exception e){
                    System.out.println(e);
                }
                if(hasWonOrLost){
                    gA.createEndScreen(winOrLoss, turnCount);
                }else{
                    gA.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            unMarkWinner();
                        }
                    });
                }
                return null;
            }
        };
        markWinners.execute();

        AsyncTask flipCardsShut = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    Thread.sleep(500);
                } catch (Exception e){
                    System.out.println(e);
                }
                gA.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        flipAllCards(false);
                    }
                });
                return null;
            }
        };
        flipCardsShut.execute();

        AsyncTask bringCardToFront = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    Thread.sleep(700);
                } catch (Exception e){
                    System.out.println(e);
                }
                gA.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gA.updatePlayer(ownCard);
                        showCard();
                    }
                });
                isPlaying = false;
                return null;
            }
        };
        bringCardToFront.execute();
    }



    public void initializePlayerFrag(){
        playerFrag = view.findViewById(R.id.player_fragment);
        playerDeckFrag = view.findViewById(R.id.player_deck_fragment);


        imageView = playerFrag.findViewById(R.id.imageView);
        playerBackground = playerFrag.findViewById(R.id.background);

        playerBackground.setVisibility(View.GONE);

        Log.d("IMAGEVIEW", "" + imageView + " " + gA.cardDeck[0].imgID);


        //imageView.setImageResource(gA.cardDeck[0].imgID);

        deckImageView = playerFrag.findViewById(R.id.imageView);
        //deckImageView.setImageResource(gA.cardDeck[0].imgID);

        playerTextViews = new TextView[]{
                playerFrag.findViewById(R.id.cardName),
                playerFrag.findViewById(R.id.attributeWert1),
                playerFrag.findViewById(R.id.attributeWert2),
                playerFrag.findViewById(R.id.attributeWert3),
                playerFrag.findViewById(R.id.attributeWert4),
                playerFrag.findViewById(R.id.attributeWert5),
        };

        playerDeckTextViews = new TextView[]{
                playerDeckFrag.findViewById(R.id.cardName),
                playerDeckFrag.findViewById(R.id.attributeWert1),
                playerDeckFrag.findViewById(R.id.attributeWert2),
                playerDeckFrag.findViewById(R.id.attributeWert3),
                playerDeckFrag.findViewById(R.id.attributeWert4),
                playerDeckFrag.findViewById(R.id.attributeWert5),
        };

        playerDeckFrag.findViewById(R.id.submitBtn).setVisibility(View.GONE);

        tableRows = new TableRow[]{
                playerFrag.findViewById(R.id.row1),
                playerFrag.findViewById(R.id.row2),
                playerFrag.findViewById(R.id.row3),
                playerFrag.findViewById(R.id.row4),
                playerFrag.findViewById(R.id.row5)
        };

        submitBtn = playerFrag.findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0; i < tableRows.length; i++){
                    ColorDrawable background = (ColorDrawable) tableRows[i].getBackground();
                    if(background != null) {
                        if (background.getColor() == green) {
                            gA.supportClass.sendMessage(gA.client, "move " + (i + 1));
                        }
                    }
                    tableRows[i].setBackgroundColor(Color.alpha(0));
                }
            }
        });

        for (int i = 0; i < tableRows.length; i++){
            final int finalI = i;
            tableRows[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(gA.turn){
                        ColorDrawable background = (ColorDrawable) tableRows[finalI].getBackground();
                        int color = 0;
                        if (background != null){color = background.getColor();}
                        if(color == green){
                            tableRows[finalI].setBackgroundColor(Color.alpha(0));
                            colorWasChanged = false;
                        }else{
                            if(colorWasChanged){resetColor();}
                            tableRows[finalI].setBackgroundColor(green);
                            colorWasChanged = true;
                            changed = true;
                        }
                    }else{
                        if(colorWasChanged){
                            resetColor();
                            colorWasChanged = false;
                        }
                    }
                }
            });
        }





        playerAnimation = cardAnimator.createPlayerCardAnimation(playerFrag);
        /*                                                              is this really neccessary?
        playerFrag.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                if(!isPlaying){
                    showCard();
                }
            }
        }); */
    }

    private void updateEnemieFrag(int number, int card, int attribute, boolean hasWon){
        if (card != -1){
            String attributeName;
            TextView textHolder;
            int iconImage;
            switch(attribute){
                case (1):
                    textHolder = playerFrag.findViewById(R.id.attribute1);
                    attributeName = textHolder.getText().toString();
                    iconImage = R.drawable.iconmasse;
                    break;
                case(2):
                    textHolder = playerFrag.findViewById(R.id.attribute2);
                    attributeName = textHolder.getText().toString();
                    iconImage = R.drawable.iconverteidigung;
                    break;
                case(3):
                    textHolder = playerFrag.findViewById(R.id.attribute3);
                    attributeName = textHolder.getText().toString();
                    iconImage = R.drawable.icongeschwindigkeit;
                    break;
                case(4):
                    textHolder = playerFrag.findViewById(R.id.attribute4);
                    attributeName = textHolder.getText().toString();
                    iconImage = R.drawable.icongerissenheit;
                    break;
                case(5):
                    textHolder = playerFrag.findViewById(R.id.attribute5);
                    attributeName = textHolder.getText().toString();
                    iconImage = R.drawable.icongrusel;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + attribute);
            }
            String displayText = "" + gA.cardDeck[card].attributeMap.get("attribute"+attribute);
            enemieTextViews[number][0].setText(gA.cardDeck[card].name);
            if (hasWon){
                enemieTextViews[number][0].setBackgroundColor(Color.GREEN);
            }
            enemieTextViews[number][1].setText(attributeName);
            enemieTextViews[number][2].setText(displayText);

            enemieImageViews[number][0].setImageResource(R.drawable.background1);
            Log.d("tokens4", ""+card);
            enemieImageViews[number][1].setImageResource(gA.cardDeck[card].imgID);
            Log.d("tokens5", ""+card);
            enemieImageViews[number][2].setImageResource(iconImage);
            if(playerCount > 3){
                enemieAnimTextViews[number][0].setText(gA.cardDeck[card].name);
                enemieAnimTextViews[number][1].setText(attribute);
                enemieAnimTextViews[number][2].setText(displayText);

                enemieAnimImageViews[number][0].setImageResource(R.drawable.background2);
                enemieAnimImageViews[number][1].setImageResource(gA.cardDeck[card].imgID);

            }
        }
    }

    public void updatePlayerFrag(int id, int count){
        int card = 0;
        for(int i = 0; i < gA.cardDeck.length; i++){
            if(id == gA.cardDeck[i].id){
                card = i;
                break;
            }
        }
        if(playerTextViews != null){
            Log.d("ALLA", "" + card);
            Log.d("ALLA", "" + gA.cardDeck[card]);
            playerTextViews[0].setText(gA.cardDeck[card].name);
            playerTextViews[1].setText("" + gA.cardDeck[card].attributeMap.get("attribute1"));
            playerTextViews[2].setText("" + gA.cardDeck[card].attributeMap.get("attribute2"));
            playerTextViews[3].setText("" + gA.cardDeck[card].attributeMap.get("attribute3"));
            playerTextViews[4].setText("" + gA.cardDeck[card].attributeMap.get("attribute4"));
            playerTextViews[5].setText("" + gA.cardDeck[card].attributeMap.get("attribute5"));
            imageView.setImageResource(gA.cardDeck[card].imgID);
            submitBtn.setVisibility(gA.turn ? View.VISIBLE : View.GONE);
        }
        else{
            Log.d("REKURSION", "REKURSION" + count);
            updatePlayerFrag(card, count + 1); //TODO MACH ES SCHÖNER DU KNECHT DANKE BITTE
        }

    }

    public void showBackground(boolean show){
        if(show){
            Log.d("ALLA", ""+enemieFrags);
            for(View enemyFrag : enemieFrags){
                enemyFrag.findViewById(R.id.background).setVisibility(View.VISIBLE);
                Log.d("ALLA", "AJA " + enemyFrag);
            }
            if(playerCount > 3){for(View enemyFrag : enemieAnimationFrags){enemyFrag.findViewById(R.id.background).setVisibility(View.VISIBLE);}
            }
        } else{
            for(View enemyFrag : enemieFrags){enemyFrag.findViewById(R.id.background).setVisibility(View.GONE);}
            if(playerCount > 3){
                for(View enemyFrag : enemieAnimationFrags){
                    enemyFrag.findViewById(R.id.background).setVisibility(View.GONE);
                }
            }
        }
    }


    private void resetColor() {
        for (int i = 0; i < tableRows.length; i++) {
            tableRows[i].setBackgroundColor(Color.alpha(0));
        }
    }

    private void checkIfAnimationsAreActive() {
        if(playerCardAnimationPlayed){
            playerAnimation.reverse();
            playerCardAnimationPlayed = !playerCardAnimationPlayed;
        }
        for(int i = 0; i < playerCount-1; i++){
            if(enemieAnimationDirection[i]){
                enemieAnimations[i][0].reverse();
                if(playerCount > 3){enemieAnimAnimations[i].reverse();}
                enemieAnimationDirection[i] = !enemieAnimationDirection[i];
            }
        }
    }

    public void updateAll(String[][] uebergabe) {
        updatePlayerFrag(Integer.parseInt(uebergabe[0][1]), 0);
        //TODO GEGNER ANHAND VON ID ZU UPDATEN
    }

    public void createWinLoseScreen(int flag){
        if(flag == 0){                                                                              //LOSE STATE
            //winLoseScreen.setImageResource();
        }else if(flag == 1){                                                                        //WIN STATE
            //
        }
    }

    public View getPlayerFrag(){
        return playerFrag;
    }

    public View[] getEnemyFrags(){
        return enemieFrags;
    }

    public void startDeckAnimation(){
        for(int i = 0; i < enemieFrags.length; i++){
            enemieDeck[i].bringToFront();
            enemieDeck[i].setAlpha(1.0f);
            enemieDeckAS[i][0].start();
        }
    }

    public void createDeckAnimation(final View view, int count, final View originCard, final AnimationHolder cardFlip){
        final AnimationHolder animationHolder = cardAnimator.createDeckAnimation(view, count+1);
        animationHolder.getObjectAnimators()[0].addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(animationHolder.gotPlayed){
                    animationHolder.gotPlayed = false;
                }else{
                    view.setAlpha(0);
                    originCard.bringToFront();
                    animationHolder.getObjectAnimators()[0].reverse();
                    animationHolder.gotPlayed = true;
                    cardFlip.start();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        if(count != -1){enemieDeckAS[count] = new AnimationHolder[]{animationHolder};}
    }

    public View[] getPlayerTV(){
        return playerTextViews;
    }

    public void showCard(){
        playerFrag.bringToFront();
        if(!playerCardAnimationPlayed){
            checkIfAnimationsAreActive();
            playerAnimation.start();
        }else {
            playerAnimation.reverse();
        }
        playerCardAnimationPlayed = !playerCardAnimationPlayed;
    }
    public int getOwnCard() {
        return ownCard;
    }

    public void setOwnCard(int ownCard) {
        this.ownCard = ownCard;
    }
    public void setEnemyCardsToDisplay(int[] enemyCardsToDisplay) {
        this.enemyCardsToDisplay = enemyCardsToDisplay;
    }
    public void setAttributeToCheck(int attributeToCheck) {
        this.attributeToCheck = attributeToCheck;
    }

    public void setTurnCount(int turnCount) {
        this.turnCount = turnCount;
    }
   public void setWinOrLoss(int winOrLoss) {
        this.winOrLoss = winOrLoss;
    }
    public void setHasWonOrLost(boolean wonOrLost) {
        this.hasWonOrLost = wonOrLost;
    }
    public void setWinnerID(int winnerID){
        this.winnerID = winnerID;
    }

    public void setWinnersToDisplay(ArrayList<Integer> winnersToDisplay) {
        this.winnersToDisplay = winnersToDisplay;
    }

    public void setWinnersToBeMarked(String winnersToBeMarked){
        this.winnersToBeMarked = winnersToBeMarked;
    }

    public void markWinner(){
        for (int i = 0; i < winnersToBeMarked.length(); i++){
            if (i == 0){
                if (winnersToBeMarked.charAt(0) == '1'){
                    playerTextViews[0].setBackgroundColor(Color.GREEN);
                }
            }
            else{
                char c = winnersToBeMarked.charAt(i);
                if (c == '1'){
                    enemieTextViews[i][0].setBackgroundColor(Color.GREEN);
                }
            }
        }
    }
    public void unMarkWinner(){
        for (int i = 0; i < winnersToBeMarked.length(); i++){
            if (i == 0){
                playerTextViews[0].setBackgroundColor(Color.alpha(0));
            }
            else{
                enemieTextViews[i-1][0].setBackgroundColor(Color.alpha(0));
            }
        }
    }

}

//TODO wenn richtig eingebunden in richtiger activity den return button bei erfolgter animation dazu verwenden diese wieder reversen zu lassen und nicht animation zu schlißen, momentan aber noch nicht möglich da zum testen die game activity nicht so gut geeignet ist das der server das spiel nicht starten lässt bisher
