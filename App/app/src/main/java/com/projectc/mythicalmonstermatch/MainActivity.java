package com.projectc.mythicalmonstermatch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;

public class MainActivity extends FragmentActivity {

    private MainFragment mainFrag;
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

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainActivityLayout, mainFrag);
            ft.commit();
            mainFrag.isInMenu = false;
            return true;
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
        for(int i = 0; i < 30; i++){
            cardDeck[i] = new CardClass(i, ("card" + i), i, i, i, i, i);
        }

    }

    public void startGameActivity(int code){
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("USERNAME", this.name);
        intent.putExtra("CODE", code);                          //code 0 == Host, code 1 == Client
        startActivity(intent);
    }

}
