package com.projectc.mythicalmonstermatch.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.projectc.mythicalmonstermatch.PlayerItem;
import com.projectc.mythicalmonstermatch.R;

public class GameFragment extends Fragment {

    private Button compareButton;
    private RadioGroup statOptions;
    private RadioButton stat1, stat2, stat3, stat4;
    private ImageView ownCard;
    private ImageView enemyCard;
    private PlayerItem myPlayerItem;
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
