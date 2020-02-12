package com.projectc.mythicalmonstermatch.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.projectc.mythicalmonstermatch.CardClass;
import com.projectc.mythicalmonstermatch.MainActivity;
import com.projectc.mythicalmonstermatch.R;


public class ShowCardFragment extends Fragment {

    private CardClass[] ccDeck;
    private CardFragment cF;

    private int pageCount = 1;

    private ImageView[] imageViews;
    private TextView onPage;
    private View cardView;

    private ImageButton leftButton;
    private ImageButton rightButton;
    private Button close;


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
        View view = getView();
        final MainActivity mainActivity = (MainActivity)getActivity();
        imageViews = new ImageView[]{
                view.findViewById(R.id.imageView1),
                view.findViewById(R.id.imageView2),
                view.findViewById(R.id.imageView3),
                view.findViewById(R.id.imageView4),
                view.findViewById(R.id.imageView5),
                view.findViewById(R.id.imageView6),
                view.findViewById(R.id.imageView7),
                view.findViewById(R.id.imageView8),
                view.findViewById(R.id.imageView9)
        };
        onPage = view.findViewById(R.id.page);
        onPage.setText(pageCount + "/3");

        updateImages();

        rightButton = view.findViewById(R.id.buttonRight);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageCount++;
                if(pageCount % 4 == 0){
                    pageCount = 1;
                }
                onPage.setText(pageCount + "/3");
                updateImages();
        }
        });

        leftButton = view.findViewById(R.id.buttonLeft);

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageCount--;
                if(pageCount  == 0){
                    pageCount = 3;
                }
                onPage.setText(pageCount + "/3");
                updateImages();
            }
        });

        cardView = view.findViewById(R.id.fragment);
        cardView.setVisibility(View.GONE);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        for(int i = 0; i < 9; i++){
            final int count = i;
            imageViews[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    activateFrag(count);

                    cardView.setVisibility(View.VISIBLE);
                    close.setVisibility(View.VISIBLE);
                    mainActivity.mainFrag.onCard = true;
                }
            });
        }

        close = view.findViewById(R.id.closeCard);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardView.setVisibility(View.GONE);
                close.setVisibility(View.GONE);
                mainActivity.mainFrag.onCard = false;
            }
        });
        close.setVisibility(View.GONE);
        Log.d("WAS", " "  + view.findViewById(R.id.ueberschrift));
        super.onActivityCreated(saveInstandesState);
    }

    public void createCardDeck(){
        MainActivity activity = (MainActivity) getActivity();
        ccDeck = activity.getCardDeck();
    }

    public void updateImages(){
        int countStart = 9 * (pageCount - 1);
        for(int i = 0; i < 9; i++){
            if(ccDeck[countStart+i].img != null){imageViews[i].setImageBitmap(ccDeck[countStart+i].img);}
            else if(ccDeck[countStart+i].imgID != null){imageViews[i].setImageResource(ccDeck[countStart+i].imgID);}

        }
    }

    public void activateFrag(int uebergabe){
        int count = (9 * (pageCount - 1)) + uebergabe;
        ImageView iV = cardView.findViewById(R.id.imageView);
        cardView.findViewById(R.id.submitBtn).setVisibility(View.GONE);
        TextView[] textViews = {
                cardView.findViewById(R.id.cardName),
                cardView.findViewById(R.id.attributeWert1),
                cardView.findViewById(R.id.attributeWert2),
                cardView.findViewById(R.id.attributeWert3),
                cardView.findViewById(R.id.attributeWert4),
                cardView.findViewById(R.id.attributeWert5),
        };

        textViews[0].setText(ccDeck[count].name);
        textViews[1].setText("" + ccDeck[count].attributeMap.get("attribute1"));
        textViews[2].setText("" + ccDeck[count].attributeMap.get("attribute2"));
        textViews[3].setText("" + ccDeck[count].attributeMap.get("attribute3"));
        textViews[4].setText("" + ccDeck[count].attributeMap.get("attribute4"));
        textViews[5].setText("" + ccDeck[count].attributeMap.get("attribute5"));


        /*for(int i = 1; i < 6; i++){
            textViews[i].setText("Masse: " + i + ": " + ccDeck[count].attributeMap.get("attribute" + i));
        }*/

        if(ccDeck[count].img != null){iV.setImageBitmap(ccDeck[count].img);}
        else if(ccDeck[count].imgID != null){iV.setImageResource(ccDeck[count].imgID);}

        Log.d("ONCLICK", ccDeck.length + " " + count);
    }
}
