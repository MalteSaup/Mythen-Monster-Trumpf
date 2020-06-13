package com.projectc.mythicalmonstermatch.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.projectc.mythicalmonstermatch.MainActivity;
import com.projectc.mythicalmonstermatch.R;

public class MainFragment extends Fragment {
    private Button menuBt;                                                                          //MENU BUTTON IN LAYOUT
    private Button showCardBt;                                                                      //SHOW CARD BUTTON IN LAYOUT
    private Button hostBt;                                                                          //HOST GAME BUTTON IN LAYOUT
    private Button joinBt;                                                                          //FIND GAME BUTTON IN LAYOUT
    public boolean isInMenu;                                                                        //MENU FLAG UM ZU ÜBERPRÜFEN WO APP SICH GERADE BEFINDET
    public boolean onCard = false;                                                                  //CARD FLAG UM ZU ÜBERPRÜFEN WO APP SICH GERADE BEFINDET
    private MainActivity mainActivity;                                                              //MAIN ACTIVITY

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity = (MainActivity)getActivity();                                                 //MAIN ACTIVITY WIRD GESETZT
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onActivityCreated(Bundle saveInstandesState) {

        //BUTTON WERDEN AUS LAYOUT GESUCHT UND MIT ONCLICKLISTENER VERSEHEN
        menuBt = getView().findViewById(R.id.settingsBt);
        menuBt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //STARTET MENUFRAGMENT UND SETZT MENU FLAG
                MenuFragment menuFrag = (MenuFragment) Fragment.instantiate(getActivity(), MenuFragment.class.getName(), null);

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.mainActivityLayout, menuFrag);
                //GameFragment gF = (GameFragment) Fragment.instantiate(getActivity(), GameFragment.class.getName(), null);
                //ft.replace(R.id.mainActivityLayout, gF);
                ft.commit();
                isInMenu = true;
            }
        });

        showCardBt = getView().findViewById(R.id.cards);
        showCardBt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //STARTET SHOWCARDFRAGMENT UND SETZT MENU FLAG
                ShowCardFragment menuFrag = (ShowCardFragment) Fragment.instantiate(getActivity(), ShowCardFragment.class.getName(), null);

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.mainActivityLayout, menuFrag);
                ft.commit();
                isInMenu = true;
            }
        });

        hostBt = getView().findViewById(R.id.hostGameBt);
        hostBt.setOnClickListener(new View.OnClickListener(){
            //STARTET GAMEACTIVITY ALS HOST
            @Override
            public void onClick(View v){
                mainActivity.startGameActivity(0);
            }
        });

        joinBt = getView().findViewById(R.id.findGameBt);
        joinBt.setOnClickListener(new View.OnClickListener(){
            //STARET GAMEACTIVITY ALS CLIENT
            @Override
            public void onClick(View v){
                mainActivity.startGameActivity(1);

            }
        });

        super.onActivityCreated(saveInstandesState);
    }





}
