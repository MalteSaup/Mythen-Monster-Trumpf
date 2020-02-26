package com.projectc.mythicalmonstermatch.Fragments;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.projectc.mythicalmonstermatch.GameActivity;
import com.projectc.mythicalmonstermatch.MainActivity;
import com.projectc.mythicalmonstermatch.PlayerItem;
import com.projectc.mythicalmonstermatch.R;

public class GameFragment extends Fragment {

    private Button compareButton;
    private PlayerItem myPlayerItem;

    private ImageView imageView;

    private View playerFrag;
    private View enemie1Frag;

    private TableRow[] tableRows;

    private GameActivity gA;

    private boolean animationPlayed = false;
    private boolean colorWasChanged = false;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game_2, container, false);
    }

    @Override
    public void onActivityCreated(Bundle saveInstandesState) {
        //gA = (GameActivity) getActivity();
        MainActivity gA = (MainActivity) getActivity();
        View view = getView();


        playerFrag = view.findViewById(R.id.player_fragment);
        enemie1Frag = view.findViewById(R.id.enemy1_fragment);

        imageView = playerFrag.findViewById(R.id.imageView);
        imageView.setImageResource(gA.cardDeck[0].imgID);

        compareButton = playerFrag.findViewById(R.id.submitBtn);
        compareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //gA.submit();
            }
        });

        compareButton.setVisibility(View.GONE);

        TextView[] playerTextViews = {
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
                        compareButton.setVisibility(View.GONE);
                        colorWasChanged = false;
                    }else{
                        if(colorWasChanged){resetColor();}
                        tableRows[finalI].setBackgroundColor(green);
                        compareButton.setVisibility(View.VISIBLE);
                        colorWasChanged = true;
                    }

                }
            });
        }

        int green = Color.parseColor("#262733");
        Log.d("GREEN", "" + green);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;

        final ObjectAnimator oA1 = ObjectAnimator.ofFloat(playerFrag, "translationY", -(height/2));
        oA1.setDuration(500);

        playerFrag.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(!animationPlayed){
                    oA1.start();
                    animationPlayed = true;
                }else {
                    oA1.reverse();
                    animationPlayed = false;
                }
            }
        });

        playerTextViews[0].setText(gA.cardDeck[0].name);
        playerTextViews[1].setText("" + gA.cardDeck[0].attributeMap.get("attribute1"));
        playerTextViews[2].setText("" + gA.cardDeck[0].attributeMap.get("attribute2"));
        playerTextViews[3].setText("" + gA.cardDeck[0].attributeMap.get("attribute3"));
        playerTextViews[4].setText("" + gA.cardDeck[0].attributeMap.get("attribute4"));
        playerTextViews[5].setText("" + gA.cardDeck[0].attributeMap.get("attribute5"));

        TextView[] enemie1TextViews = {
                enemie1Frag.findViewById(R.id.cardName),
                enemie1Frag.findViewById(R.id.attribut),
                enemie1Frag.findViewById(R.id.attributeWert)
        };

        ImageView[] enemieImageViews = {
                enemie1Frag.findViewById(R.id.background),
                enemie1Frag.findViewById(R.id.imageView)
        };


        enemie1TextViews[0].setText(gA.cardDeck[4].name);
        enemie1TextViews[1].setText("Scarriness");
        enemie1TextViews[2].setText("" + gA.cardDeck[4].attributeMap.get("attribute2"));

        enemieImageViews[0].setImageResource(R.drawable.background);
        enemieImageViews[1].setImageResource(gA.cardDeck[4].imgID);

        //TODO ENEMIE FRAG BACKGROUND IMAGE TO BACKSITE OF CARD




        /*compareButton = getView().findViewById(R.id.compareButton);
        compareButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (myPlayerItem.getAllowedToPlay()){
                    HostFragment.manager.pushResult(myPlayerItem.getCard(0).attributeMap.get("attribute"+ myPlayerItem.getSelectedStat()).intValue(), myPlayerItem);

                }
            }
        });

        statOptions = (RadioGroup) getView().findViewById(R.id.statOptions);

        statOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (myPlayerItem.getAllowedToPlay()) {
                    if (checkedId == R.id.stat1) {
                        Toast.makeText(getContext(), "choice: Mass",
                                Toast.LENGTH_SHORT).show();
                        myPlayerItem.setSelectedStat(1);
                    } else if (checkedId == R.id.stat2) {
                        Toast.makeText(getContext(), "choice: Speed",
                                Toast.LENGTH_SHORT).show();
                        myPlayerItem.setSelectedStat(2);
                    } else if (checkedId == R.id.stat3) {
                        Toast.makeText(getContext(), "choice: Cunning",
                                Toast.LENGTH_SHORT).show();
                        myPlayerItem.setSelectedStat(3);
                    } else if (checkedId == R.id.stat4) {
                        Toast.makeText(getContext(), "choice: Scariness",
                                Toast.LENGTH_SHORT).show();
                        myPlayerItem.setSelectedStat(4);
                    }
                }
                else {

                }
            }

        });

        stat1 = (RadioButton) getView().findViewById(R.id.stat1);
        stat2 = (RadioButton) getView().findViewById(R.id.stat2);
        stat3 = (RadioButton) getView().findViewById(R.id.stat3);
        stat4 = (RadioButton) getView().findViewById(R.id.stat4);
*/
        super.onActivityCreated(saveInstandesState);
    }
    private void resetColor() {
        for (int i = 0; i < tableRows.length; i++) {
            tableRows[i].setBackgroundColor(Color.parseColor("#262733"));
        }
    }

}
