package com.projectc.mythicalmonstermatch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.projectc.mythicalmonstermatch.Fragments.MainFragment;

public class MainActivity extends FragmentActivity {

    public MainFragment mainFrag;
    public String name = "";
    private CardClass[] cardDeck = new CardClass[30];

    private SharedPreferences data;
    private SharedPreferences.Editor data_editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainFrag = (MainFragment) Fragment.instantiate(this, MainFragment.class.getName(), null);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.mainActivityLayout, mainFrag);
        ft.commit();

        createCardDeck();

        
        data = getApplicationContext().getSharedPreferences("user_name", 0);

        if(data.getString("user_name", null) != null){
            name = data.getString("user_name", null);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && (mainFrag.isInMenu || mainFrag.isInCards)){
            if(!mainFrag.onCard){
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.mainActivityLayout, mainFrag);
                ft.commit();
                mainFrag.isInMenu = false;
                return true;
            } else {
                View frag = findViewById(R.id.fragment);
                frag.setVisibility(View.GONE);
                Button close = findViewById(R.id.closeCard);
                close.setVisibility(View.GONE);
                mainFrag.onCard = false;
                return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
        data_editor = data.edit();
        data_editor.putString("user_name", name);
        data_editor.apply();
    }
    public CardClass[] getCardDeck(){return  cardDeck;}

    public void createCardDeck(){
        BitmapFactory bf = new BitmapFactory();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap[] b = {
                bf.decodeResource(getResources(), R.drawable.image01),
                bf.decodeResource(getResources(), R.drawable.image02),
                bf.decodeResource(getResources(), R.drawable.image03),
                bf.decodeResource(getResources(), R.drawable.image04),
                bf.decodeResource(getResources(), R.drawable.image05),
                bf.decodeResource(getResources(), R.drawable.image06),
                bf.decodeResource(getResources(), R.drawable.image07),
                bf.decodeResource(getResources(), R.drawable.image08),
                bf.decodeResource(getResources(), R.drawable.image09),
        };

        Integer[] imgIDs = {
                R.drawable.chimaere,
                R.drawable.dschinn,
                R.drawable.einhorn,
                R.drawable.medusa,
                R.drawable.minotaur,
                R.drawable.pegasus,
                R.drawable.satyr,
                R.drawable.zyklop
        };

        cardDeck[0] = new CardClass(0, "Chimäre", 4, 6, 6, 5, 7, imgIDs[0]);
        cardDeck[1] = new CardClass(1, "Dschinn", 1, 9 , 1, 10, 2, imgIDs[1]);
        cardDeck[2] = new CardClass(2, "Einhorn", 3, 2, 7 , 2, 1, imgIDs[2]);
        cardDeck[3] = new CardClass(3, "Medusa", 2, 8 , 3 , 7, 7, imgIDs[3]);
        cardDeck[4] = new CardClass(4, "Minotaur", 4, 3, 3, 3, 6, imgIDs[4]);
        cardDeck[5] = new CardClass(5, "Pegasus", 3, 3, 10, 2, 1, imgIDs[5]);
        cardDeck[6] = new CardClass(6, "Satyr", 2, 1, 5, 7, 2, imgIDs[6]);
        cardDeck[7] = new CardClass(7, "Zyklop", 5, 3, 2, 1, 4, imgIDs[7]);

        for(int i = 8; i < 30; i++){
            cardDeck[i] = new CardClass(i, ("card" + i), i, i, i, i, i, b[i%9]);
        }

    }

    public void startGameActivity(int code){
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("USERNAME", this.name);
        intent.putExtra("CODE", code);                                                        //code 0 == Host, code 1 == Client

        startActivity(intent);
    }

}
