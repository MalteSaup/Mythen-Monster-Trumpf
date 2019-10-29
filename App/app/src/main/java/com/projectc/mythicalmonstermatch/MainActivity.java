package com.projectc.mythicalmonstermatch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;

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
                ImageButton close = findViewById(R.id.closeCard);
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
        Bitmap[] b = {
                bf.decodeResource(getResources(), R.drawable.image01),
                bf.decodeResource(getResources(), R.drawable.image02),
                bf.decodeResource(getResources(), R.drawable.image03),
                bf.decodeResource(getResources(), R.drawable.image04),
                bf.decodeResource(getResources(), R.drawable.image05),
                bf.decodeResource(getResources(), R.drawable.image06),
                bf.decodeResource(getResources(), R.drawable.image07),
                bf.decodeResource(getResources(), R.drawable.image08),
                bf.decodeResource(getResources(), R.drawable.image09)
        };
        for(int i = 0; i < 30; i++){
            cardDeck[i] = new CardClass(i, ("card" + i), i, i, i, i, i, b[i%9]);
        }

    }

    public void startGameActivity(int code){
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("USERNAME", this.name);
        intent.putExtra("CODE", code);                          //code 0 == Host, code 1 == Client
        startActivity(intent);
    }

}
