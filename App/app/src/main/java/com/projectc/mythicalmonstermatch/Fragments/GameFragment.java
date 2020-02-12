package com.projectc.mythicalmonstermatch.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.projectc.mythicalmonstermatch.GameActivity;
import com.projectc.mythicalmonstermatch.PlayerItem;
import com.projectc.mythicalmonstermatch.R;

public class GameFragment extends Fragment {

    private Button compareButton;
    private PlayerItem myPlayerItem;

    private View playerFrag;
    private View enemie1Frag;

    private GameActivity gA;

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
        gA = (GameActivity) getActivity();
        View view = getView();
        compareButton = view.findViewById(R.id.compareButton);
        compareButton.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View view) { gA.submit();
                                             }
                                         });

        compareButton.setVisibility(View.GONE);

        playerFrag = view.findViewById(R.id.player_fragment);
        enemie1Frag = view.findViewById(R.id.enemy1_fragment);

        playerFrag.findViewById(R.id.submitBtn).setVisibility(View.GONE);
        TextView[] playerTextViews = {
                playerFrag.findViewById(R.id.cardName),
                playerFrag.findViewById(R.id.attributeWert1),
                playerFrag.findViewById(R.id.attributeWert2),
                playerFrag.findViewById(R.id.attributeWert3),
                playerFrag.findViewById(R.id.attributeWert4),
                playerFrag.findViewById(R.id.attributeWert5),
        };

        playerTextViews[0].setText(gA.cardDeck[0].name);
        playerTextViews[1].setText("" + gA.cardDeck[0].attributeMap.get("attribute1"));
        playerTextViews[2].setText("" + gA.cardDeck[0].attributeMap.get("attribute2"));
        playerTextViews[3].setText("" + gA.cardDeck[0].attributeMap.get("attribute3"));
        playerTextViews[4].setText("" + gA.cardDeck[0].attributeMap.get("attribute4"));
        playerTextViews[5].setText("" + gA.cardDeck[0].attributeMap.get("attribute5"));

        enemie1Frag.findViewById(R.id.submitBtn).setVisibility(View.GONE);
        TextView[] enemie1TextViews = {
                enemie1Frag.findViewById(R.id.cardName),
                enemie1Frag.findViewById(R.id.attributeWert1),
                enemie1Frag.findViewById(R.id.attributeWert2),
                enemie1Frag.findViewById(R.id.attributeWert3),
                enemie1Frag.findViewById(R.id.attributeWert4),
                enemie1Frag.findViewById(R.id.attributeWert5),
        };

        for(int i = 0; i < playerTextViews.length; i++){
            enemie1TextViews[i].setText("  ");
        }
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

}
