package com.projectc.mythicalmonstermatch.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.projectc.mythicalmonstermatch.R;


public class EnemyCardFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_enemy, container, false);
    }

    @Override
    public void onActivityCreated(Bundle saveInstandesState) {
        super.onActivityCreated(saveInstandesState);
    }

}