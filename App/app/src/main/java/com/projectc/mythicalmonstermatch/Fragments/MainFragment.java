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
    private Button menuBt;
    private Button showCardBt;
    private Button hostBt;
    private Button joinBt;
    public boolean isInMenu;
    public boolean isInCards;
    public boolean onCard = false;
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
