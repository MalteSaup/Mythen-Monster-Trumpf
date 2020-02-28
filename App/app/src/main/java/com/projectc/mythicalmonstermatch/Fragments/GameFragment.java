package com.projectc.mythicalmonstermatch.Fragments;

import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.Guideline;
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
import com.projectc.mythicalmonstermatch.MainActivity;
import com.projectc.mythicalmonstermatch.PlayerItem;
import com.projectc.mythicalmonstermatch.R;

public class GameFragment extends Fragment {

    private Button compareButton;
    private PlayerItem myPlayerItem;

    private ImageView imageView;

    private View playerFrag;
    private View[] enemieFrags;
    private View view;
    private TableRow[] tableRows;

    //private GameActivity gA;
    private MainActivity gA;
    private boolean playerCardAnimationPlayed = false;
    private boolean[] enemieAnimationDirection;
    private boolean colorWasChanged = false;
    private boolean background = false;

    private TextView[] enemieTextViews[];
    private ImageView[] enemieImageViews[];
    private Guideline[] enemieGuideLines[];
    private TextView[] playerTextViews;

    private int[] game_fragments = new int[]{
            R.layout.fragment_game_2,
            R.layout.fragment_game_3
    };
    private int playerCount = 2; //HARDCODED

    private CardAnimator cardAnimator;

    private AnimationHolder playerAnimation;
    private AnimationHolder[] enemieAnimations;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //ACTIVITY HOLEN UND SPIELERZAHL AUSLESEN
        //gA = (GameActivity) getActivity();
        //playerCount = gA.server.playerCount();
        /*switch (playerCount){
            case 2: return inflater.inflate(game_fragments[0], container, false);
            case 3: return inflater.inflate(game_fragments[1], container, false);
            case 4: return inflater.inflate(game_fragments[2], container, false);
            case 5: return inflater.inflate(game_fragments[3], container, false);
        }*/
        return inflater.inflate(R.layout.fragment_game_2, container, false);
    }

    @Override
    public void onActivityCreated(Bundle saveInstandesState) {
        //gA = (GameActivity) getActivity();
        gA = (MainActivity) getActivity();
        view = getView();

        cardAnimator = new CardAnimator(gA);

        initializePlayerFrag();
        initializeEnemieFrags(playerCount);

        updatePlayerFrag(1);
        updateEnemieFrag(0, 4, "Scarriness");
        //updateEnemieFrag(1, 3, "Scarriness");

        showBackground(background);


        //TODO ENEMIE FRAG BACKGROUND IMAGE TO BACKSITE OF CARD


        //Log.d("HEIGHT", " " + enemieFrags[0].getMeasuredHeight() + " " + enemieFrags[0].getHeight());
        switch (playerCount) {
            case 2: {
                AsyncTask asyncTask = new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] objects) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        createEnemieAnimation1();
                        return null;
                    }
                };
                asyncTask.execute();
            }
        }

        super.onActivityCreated(saveInstandesState);
    }

    private void initializeEnemieFrags(int playerCount){


        Resources res = getResources();
        String enemyString = "enemy_fragment";

        enemieFrags = new View[playerCount-1];
        enemieTextViews = new TextView[playerCount-1][];
        enemieImageViews = new ImageView[playerCount-1][];
        enemieGuideLines = new Guideline[playerCount-1][];
        enemieAnimations = new AnimationHolder[playerCount-1];

        enemieAnimationDirection = new boolean[playerCount-1];

        Log.d("ENEMIE22", "" + playerCount + " " + enemieAnimationDirection.length + " " + enemieAnimationDirection[0]);

        for(int i = 0; i < playerCount-1; i++){
            String uebergabe = enemyString + (i+1);
            int id = res.getIdentifier(uebergabe, "id", gA.getPackageName());
            enemieFrags[i] = view.findViewById(id);

            Log.d("ENEMIE", "" + enemieFrags[i]);

            enemieTextViews[i] = new TextView[]{
                    enemieFrags[i].findViewById(R.id.cardName),
                    enemieFrags[i].findViewById(R.id.attribut),
                    enemieFrags[i].findViewById(R.id.attributeWert)
            };
            enemieImageViews[i] = new ImageView[]{
                    enemieFrags[i].findViewById(R.id.background),
                    enemieFrags[i].findViewById(R.id.imageView)
            };

            if(playerCount > 3){
                enemieGuideLines[i] = new Guideline[]{
                    enemieFrags[i].findViewById(R.id.enemyCardLeft),
                    enemieFrags[i].findViewById(R.id.enemyCardRight),
                    enemieFrags[i].findViewById(R.id.enemyCardBot),
                    //enemieFrags[i-1].findViewById(R.id.enemyCardTop) //TODO WENN LAYOUT VORHANDEN FÜR 4 UND MEHR SPIELER
                };
            } else {
                enemieGuideLines[i] = new Guideline[]{
                    enemieFrags[i].findViewById(R.id.enemyCardLeft),
                    enemieFrags[i].findViewById(R.id.enemyCardRight),
                    enemieFrags[i].findViewById(R.id.enemyCardBot),
                };
            }

            Log.d("ARRAYID", "" + enemieTextViews.length + " " + enemieTextViews[i].length);
        }
    }

    public void createEnemieAnimation1(){
        final AnimationHolder animationHolder = cardAnimator.createSingleEnemyCardAnimation(enemieFrags[0]);
        enemieAnimations[0] = animationHolder;
        enemieFrags[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!background){
                    if (!enemieAnimationDirection[0]) {
                        checkIfAnimationsAreActive();
                        enemieFrags[0].bringToFront();
                        enemieAnimations[0].start();
                    } else {
                        enemieAnimations[0].reverse();
                    }
                    enemieAnimationDirection[0] = !enemieAnimationDirection[0];
                }
            }
        });
    }

    public void initializePlayerFrag(){
        playerFrag = view.findViewById(R.id.player_fragment);

        imageView = playerFrag.findViewById(R.id.imageView);
        imageView.setImageResource(gA.cardDeck[0].imgID);

        playerTextViews = new TextView[]{
                playerFrag.findViewById(R.id.cardName),
                playerFrag.findViewById(R.id.attributeWert),
                playerFrag.findViewById(R.id.attributeWert2),
                playerFrag.findViewById(R.id.attributeWert3),
                playerFrag.findViewById(R.id.attributeWert4),
                playerFrag.findViewById(R.id.attributeWert5),
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





        final ObjectAnimator objectAnimator = cardAnimator.createPlayerCardAnimation(playerFrag);
        playerAnimation = new AnimationHolder(objectAnimator);
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
        enemieTextViews[number][0].setText(gA.cardDeck[card].name);
        enemieTextViews[number][1].setText(attribute);
        enemieTextViews[number][2].setText("" + gA.cardDeck[card].attributeMap.get("attribute2"));


        enemieImageViews[number][0].setImageResource(R.drawable.background);
        enemieImageViews[number][1].setImageResource(gA.cardDeck[card].imgID);
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
        if(show){for(View enemyFrag : enemieFrags){enemyFrag.findViewById(R.id.background).setVisibility(View.VISIBLE);}
        } else{for(View enemyFrag : enemieFrags){enemyFrag.findViewById(R.id.background).setVisibility(View.GONE);}}
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
                enemieAnimations[i].reverse();
                enemieAnimationDirection[i] = !enemieAnimationDirection[i];
            }
        }
    }

}
