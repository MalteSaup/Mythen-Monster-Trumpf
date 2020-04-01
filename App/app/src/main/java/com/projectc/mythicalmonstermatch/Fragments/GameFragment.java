package com.projectc.mythicalmonstermatch.Fragments;

import android.animation.Animator;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import java.util.ArrayList;

public class GameFragment extends Fragment {

    private Button compareButton;
    private PlayerItem myPlayerItem;

    private ImageView imageView;
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
    private boolean background = false;

    private TextView[] enemieTextViews[];
    private ImageView[] enemieImageViews[];
    private TextView[] playerTextViews;
    private TextView[] playerDeckTextViews;

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

    private CardAnimator cardAnimator;

    private AnimationHolder playerAnimation;
    private AnimationHolder playerDeckAnimation;
    private AnimationHolder[] enemieAnimations[];
    private AnimationHolder[] enemieAnimAnimations;
    private TextView[][] enemieDeckTV;
    private ImageView[][] enemieDeckIV;
    private AnimationHolder[][] enemieDeckAS;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //ACTIVITY HOLEN UND SPIELERZAHL AUSLESEN
        gA = (GameActivity) getActivity();
        playerCount = gA.playerItems.size();
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

        showBackground(background);

        winLoseScreen = gA.findViewById(R.id.win_lose_screen);
        winLoseScreen.setVisibility(View.GONE);

        initAndSortIDArray();
        //TODO ENEMIE FRAG BACKGROUND IMAGE TO BACKSITE OF CARD


        //Log.d("HEIGHT", " " + enemieFrags[0].getMeasuredHeight() + " " + enemieFrags[0].getHeight());
        AsyncTask asyncTask = new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] objects) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                createEnemieAnimations();
                if(playerCount > 3){
                    deactivateAnimFrags();
                }
                return null;
            }
        };
        asyncTask.execute();
        super.onActivityCreated(saveInstandesState);
    }

    public void initAndSortIDArray(){
        ArrayList<ServerListener> sLL = gA.server.getServerListeners();
        playerID = new int[sLL.size()];
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
                    enemieFrags[i].findViewById(R.id.imageView)
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
                    enemieDeck[i].findViewById(R.id.imageView)
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
                    /*onClickAnimation = cardAnimator.createTwoEnemyCardAnimation(enemieFrags[i], i+2);*/
                    onClickAnimation = cardAnimator.createCardFlip(enemieFrags[i]);
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
                            enemieAnimations[finalI][0].start();
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

    public void initializePlayerFrag(){
        playerFrag = view.findViewById(R.id.player_fragment);
        playerDeckFrag = view.findViewById(R.id.player_deck_fragment);


        imageView = playerFrag.findViewById(R.id.imageView);

        Log.d("IMAGEVIEW", "" + gA.cardDeck[0].imgID);


        imageView.setImageResource(gA.cardDeck[0].imgID);

        deckImageView = playerFrag.findViewById(R.id.imageView);
        deckImageView.setImageResource(gA.cardDeck[0].imgID);

        playerTextViews = new TextView[]{
                playerFrag.findViewById(R.id.cardName),
                playerFrag.findViewById(R.id.attributeWert),
                playerFrag.findViewById(R.id.attributeWert2),
                playerFrag.findViewById(R.id.attributeWert3),
                playerFrag.findViewById(R.id.attributeWert4),
                playerFrag.findViewById(R.id.attributeWert5),
        };

        playerDeckTextViews = new TextView[]{
                playerDeckFrag.findViewById(R.id.cardName),
                playerDeckFrag.findViewById(R.id.attributeWert),
                playerDeckFrag.findViewById(R.id.attributeWert2),
                playerDeckFrag.findViewById(R.id.attributeWert3),
                playerDeckFrag.findViewById(R.id.attributeWert4),
                playerDeckFrag.findViewById(R.id.attributeWert5),
        };

        tableRows = new TableRow[]{
                playerFrag.findViewById(R.id.row1),
                playerFrag.findViewById(R.id.row2),
                playerFrag.findViewById(R.id.row3),
                playerFrag.findViewById(R.id.row4),
                playerFrag.findViewById(R.id.row5)
        };

        for (int i = 0; i < tableRows.length; i++){
            final int finalI = i;
            tableRows[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ColorDrawable background = (ColorDrawable) tableRows[finalI].getBackground();
                    int green = Color.parseColor("#00FF00");
                    int color = 0;
                    if (background != null){color = background.getColor();}
                    if(color == green){
                        tableRows[finalI].setBackgroundColor(Color.parseColor("#262733"));
                        colorWasChanged = false;
                    }else{
                        if(colorWasChanged){resetColor();}
                        tableRows[finalI].setBackgroundColor(green);
                        colorWasChanged = true;
                    }

                }
            });
        }





        playerAnimation = cardAnimator.createPlayerCardAnimation(playerFrag);
        playerFrag.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                playerFrag.bringToFront();
                if(!playerCardAnimationPlayed){
                    checkIfAnimationsAreActive();
                    playerAnimation.start();
                }else {
                    playerAnimation.reverse();
                }
                playerCardAnimationPlayed = !playerCardAnimationPlayed;
            }
        });
    }

    private void updateEnemieFrag(int number, int card, String attribute){
        Log.d("NUMBER", "" + number);
        enemieTextViews[number][0].setText(gA.cardDeck[card].name);
        enemieTextViews[number][1].setText(attribute);
        enemieTextViews[number][2].setText("" + gA.cardDeck[card].attributeMap.get("attribute2"));

        enemieImageViews[number][0].setImageResource(R.drawable.background);
        enemieImageViews[number][1].setImageResource(gA.cardDeck[card].imgID);

        if(playerCount > 3){
            enemieAnimTextViews[number][0].setText(gA.cardDeck[card].name);
            enemieAnimTextViews[number][1].setText(attribute);
            enemieAnimTextViews[number][2].setText("" + gA.cardDeck[card].attributeMap.get("attribute2"));

            enemieAnimImageViews[number][0].setImageResource(R.drawable.background);
            enemieAnimImageViews[number][1].setImageResource(gA.cardDeck[card].imgID);
        }
    }

    private void updatePlayerFrag(int card){
        playerTextViews[0].setText(gA.cardDeck[0].name);
        playerTextViews[1].setText("" + gA.cardDeck[0].attributeMap.get("attribute1"));
        playerTextViews[2].setText("" + gA.cardDeck[0].attributeMap.get("attribute2"));
        playerTextViews[3].setText("" + gA.cardDeck[0].attributeMap.get("attribute3"));
        playerTextViews[4].setText("" + gA.cardDeck[0].attributeMap.get("attribute4"));
        playerTextViews[5].setText("" + gA.cardDeck[0].attributeMap.get("attribute5"));
    }

    private void showBackground(boolean show){
        if(show){
            for(View enemyFrag : enemieFrags){enemyFrag.findViewById(R.id.background).setVisibility(View.VISIBLE);}
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
            tableRows[i].setBackgroundColor(Color.parseColor("#262733"));
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
        updatePlayerFrag(Integer.parseInt(uebergabe[0][1]));
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

}

//TODO wenn richtig eingebunden in richtiger activity den return button bei erfolgter animation dazu verwenden diese wieder reversen zu lassen und nicht animation zu schlißen, momentan aber noch nicht möglich da zum testen die game activity nicht so gut geeignet ist das der server das spiel nicht starten lässt bisher
