package com.projectc.mythicalmonstermatch.Fragments;


import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.projectc.mythicalmonstermatch.GameActivity;
import com.projectc.mythicalmonstermatch.GameManager;
import com.projectc.mythicalmonstermatch.MainActivity;
import com.projectc.mythicalmonstermatch.PlayerAdapter;
import com.projectc.mythicalmonstermatch.R;

import static android.content.Context.WIFI_SERVICE;

public class EndScreenFragment extends Fragment {

    private Button playAgainButton;
    private Button exitButton;

    private TextView resultText;
    private TextView turnCountText;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_endscreen, container, false);

    }

    @Override
    public void onActivityCreated(Bundle saveInstandesState) {
        final GameActivity gA = (GameActivity) getActivity();
        View view = getView();
        playAgainButton = getView().findViewById(R.id.playAgainButton);
        resultText = getView().findViewById(R.id.resultText);
        turnCountText = getView().findViewById(R.id.turnCountText);

        if (gA.code == 0){

            playAgainButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    gA.gameManager = new GameManager(gA.cardDeck, gA.playerItems, gA.server);
                    FragmentTransaction ft = gA.getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.gameActivityLayout, gA.hostFrag);
                    ft.commit();
                }
            });;
        }
        else{
            playAgainButton.setAlpha(0.5f);
            playAgainButton.setClickable(false);
        }


        if (getArguments().getInt("result") == 1){ //1 means you win, 0 means you lose
            resultText.setText("You won all the cards, so you are the winner!");
            resultText.setTextColor(Color.GREEN);
        }
        else if (getArguments().getInt("result") == 0){
            resultText.setText("You lost all your cards, you are a loser...");
            resultText.setTextColor(Color.RED);
        }

        turnCountText.setText("For you the game lasted " + getArguments().getInt("turnCount") + " turns.");

        exitButton = getView().findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //TODO: if you were the host, this breaks the lobby for everyone and if not you leave the game
             }
        });

        super.onActivityCreated(saveInstandesState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}

