package com.projectc.mythicalmonstermatch;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class ShowCardFragment extends Fragment {

    private CardFragment[] cardDeck = new CardFragment[30];
    private int count = 0;
    private CardClass[] ccDeck;

    private ConstraintLayout leftFrag;
    private ConstraintLayout rightFrag;
    private ConstraintLayout centerFrag;

    private TextView[] rightCardView;
    private TextView[] centerCardView;
    private TextView[] leftCardView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        createCardDeck();
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show_card, container, false);
    }

    @Override
    public void onActivityCreated(Bundle saveInstandesState) {

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragmentCardCenter, new CardFragment());
        ft.add(R.id.fragmentCardLeft, new CardFragment());
        ft.add(R.id.fragmentCardRight, new CardFragment());
        ft.commit();

        leftFrag = getView().findViewById(R.id.fragmentCardLeft);
        rightFrag = getView().findViewById(R.id.fragmentCardRight);
        centerFrag = getView().findViewById(R.id.fragmentCardCenter);

        leftCardView = getCardViews(leftFrag);
        rightCardView = getCardViews(rightFrag);
        centerCardView = getCardViews(centerFrag);

        setCardText(count % 30, leftCardView);
        setCardText(count + 2 % 30, rightCardView);
        setCardText(count + 1 % 30, centerCardView);

        leftFrag.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                count = ((count - 1) % 30);
                if(count < 0){
                    count += 30;
                }
                Log.d("NUMBERS", (count%30) + " : " + ((count + 2) % 30) + " : " + ((count + 1) % 30));
                setCardText((count % 30), leftCardView);
                setCardText((count + 2) % 30, rightCardView);
                setCardText((count + 1) % 30, centerCardView);
            }
        });

        rightFrag.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                count = (count + 1) % 30;

                setCardText(count % 30, leftCardView);
                setCardText((count + 2) % 30, rightCardView);
                setCardText((count + 1) % 30, centerCardView);
            }
        });

        View[] views = {getView(), centerFrag};

        for(int i = 0; i < 2; i++){
            views[i].setOnTouchListener(new OnSwipeTouchListener(getActivity()){
                @Override
                public void onSwipeLeft(){
                    count = ((count - 1) % 30);
                    if(count < 0){
                        count += 30;
                    }
                    Log.d("NUMBERS", (count%30) + " : " + ((count + 2) % 30) + " : " + ((count + 1) % 30));
                    setCardText((count % 30), leftCardView);
                    setCardText((count + 2) % 30, rightCardView);
                    setCardText((count + 1) % 30, centerCardView);
                }

                @Override
                public void onSwipeRight(){
                    count = (count + 1) % 30;

                    setCardText(count % 30, leftCardView);
                    setCardText((count + 2) % 30, rightCardView);
                    setCardText((count + 1) % 30, centerCardView);
                }
            });
        }

        getView().setOnTouchListener(new OnSwipeTouchListener(getActivity()){
            @Override
            public void onSwipeLeft(){
                count = ((count - 1) % 30);
                if(count < 0){
                    count += 30;
                }
                Log.d("NUMBERS", (count%30) + " : " + ((count + 2) % 30) + " : " + ((count + 1) % 30));
                setCardText((count % 30), leftCardView);
                setCardText((count + 2) % 30, rightCardView);
                setCardText((count + 1) % 30, centerCardView);
            }

            @Override
            public void onSwipeRight(){
                count = (count + 1) % 30;

                setCardText(count % 30, leftCardView);
                setCardText((count + 2) % 30, rightCardView);
                setCardText((count + 1) % 30, centerCardView);
            }
        });


        super.onActivityCreated(saveInstandesState);
    }

    public void createCardDeck(){
        MainActivity activity = (MainActivity) getActivity();
        ccDeck = activity.getCardDeck();
    }

    public void setCardText(int number, TextView[] textViewArray){

        textViewArray[0].setText(ccDeck[number].name);
        textViewArray[1].setText("Attribute 1: " + ccDeck[number].attributeMap.get("attribute1"));
        textViewArray[2].setText("Attribute 2: " + ccDeck[number].attributeMap.get("attribute2"));
        textViewArray[3].setText("Attribute 3: " + ccDeck[number].attributeMap.get("attribute3"));
        textViewArray[4].setText("Attribute 4: " + ccDeck[number].attributeMap.get("attribute4"));
        textViewArray[5].setText("Attribute 5: " + ccDeck[number].attributeMap.get("attribute5"));

    }

    public TextView[] getCardViews(ConstraintLayout cL){
        TextView tVname = cL.findViewById(R.id.cardName);
        TextView tV1 = cL.findViewById(R.id.attribute1);
        TextView tV2 = cL.findViewById(R.id.attribute2);
        TextView tV3 = cL.findViewById(R.id.attribute3);
        TextView tV4 = cL.findViewById(R.id.attribute4);
        TextView tV5 = cL.findViewById(R.id.attribute5);
        TextView[] tVArray = {tVname, tV1, tV2, tV3, tV4, tV5};
        return tVArray;
    }


}
