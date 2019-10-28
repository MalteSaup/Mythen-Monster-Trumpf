package com.projectc.mythicalmonstermatch;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

public class MainFragment extends Fragment {
    private Button menuBt;
    private Button showCardBt;
    private Button hostBt;
    private Button joinBt;
    public boolean isInMenu;
    public boolean isInCards;
    private String name;
    private MainActivity mainActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity = (MainActivity)getActivity();
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onActivityCreated(Bundle saveInstandesState) {
        menuBt = getView().findViewById(R.id.settingsBt);
        menuBt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                MenuFragment menuFrag = (MenuFragment) Fragment.instantiate(getActivity(), MenuFragment.class.getName(), null);

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.mainActivityLayout, menuFrag);
                ft.commit();
                isInMenu = true;
            }
        });
        showCardBt = getView().findViewById(R.id.cards);
        showCardBt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ShowCardFragment menuFrag = (ShowCardFragment) Fragment.instantiate(getActivity(), ShowCardFragment.class.getName(), null);

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.mainActivityLayout, menuFrag);
                ft.commit();
                isInCards = true;
            }
        });
        hostBt = getView().findViewById(R.id.hostGameBt);
        hostBt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mainActivity.startGameActivity(0);


                /*StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
                WifiHelper wH = new WifiHelper();
                ArrayList<String> aL = wH.getDeviceList();
                for (String a: aL) {
                    Log.d("ADDRESS", a);
                }*/ //Vorbereitung für später
            }
        });

        joinBt = getView().findViewById(R.id.findGameBt);
        joinBt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mainActivity.startGameActivity(1);
            }
        });

        super.onActivityCreated(saveInstandesState);
    }





}
