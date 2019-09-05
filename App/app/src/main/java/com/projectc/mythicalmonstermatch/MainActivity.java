package com.projectc.mythicalmonstermatch;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainFrag = (MainFragment) Fragment.instantiate(this, MainFragment.class.getName(), null);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.mainActivityLayout, mainFrag);
        ft.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && mainFrag.isInMenu){

            MenuFragment menuFragment = (MenuFragment)getSupportFragmentManager().findFragmentById(R.id.mainActivityLayout);

            if(menuFragment.getName() != null){
                name = menuFragment.getName();
            }

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainActivityLayout, mainFrag);
            ft.commit();
            mainFrag.isInMenu = false;
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
