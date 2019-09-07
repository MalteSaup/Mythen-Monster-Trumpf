package com.projectc.mythicalmonstermatch;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class ShowCardFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show_card, container, false);
    }

    @Override
    public void onActivityCreated(Bundle saveInstandesState) {
        CardFragment card1 = (CardFragment) Fragment.instantiate(getActivity(), CardFragment.class.getName(), null);
        CardFragment card2 = (CardFragment) Fragment.instantiate(getActivity(), CardFragment.class.getName(), null);
        CardFragment card3 = (CardFragment) Fragment.instantiate(getActivity(), CardFragment.class.getName(), null);

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragmentCardCenter, card1);
        ft.add(R.id.fragmentCardLeft, card2);
        ft.add(R.id.fragmentCardRight, card3);

        ft.commit();


        super.onActivityCreated(saveInstandesState);
    }
}
