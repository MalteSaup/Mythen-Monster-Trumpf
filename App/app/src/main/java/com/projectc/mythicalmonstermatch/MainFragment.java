package com.projectc.mythicalmonstermatch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MainFragment extends Fragment {

    private Button menuBt;
    private Button showCardBt;
    public boolean isInMenu;
    public boolean isInCards;
    private String name;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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
        super.onActivityCreated(saveInstandesState);
    }





}
