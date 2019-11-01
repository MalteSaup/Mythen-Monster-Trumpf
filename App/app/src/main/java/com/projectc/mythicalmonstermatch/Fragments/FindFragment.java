package com.projectc.mythicalmonstermatch.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.projectc.mythicalmonstermatch.R;
import com.projectc.mythicalmonstermatch.ServerAdapter;
import com.projectc.mythicalmonstermatch.ServerItem;

import java.util.ArrayList;


public class FindFragment extends Fragment {
    private RecyclerView serverRecyclerView;
    private RecyclerView.Adapter serverAdapter;

    private ArrayList<ServerItem> serverList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find, container, false);
    }

    @Override
    public void onActivityCreated(Bundle saveInstandesState) {
        View view = getView();
        serverList = new ArrayList<>();
        serverList.add(new ServerItem("Hans", 1));
        serverList.add(new ServerItem("John", 2));
        serverList.add(new ServerItem("Jürgen", 3));
        serverList.add(new ServerItem("Glürgen", 4));
        serverList.add(new ServerItem("Dürgen", 5));
        serverRecyclerView = view.findViewById(R.id.serverView);
        serverRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        serverAdapter = new ServerAdapter(getContext(), serverList);
        serverRecyclerView.setAdapter(serverAdapter);
        super.onActivityCreated(saveInstandesState);
    }

    public void addServer(String servername, int playercount){
        serverList.add(new ServerItem(servername, playercount));
    }

    public void removeServer(String servername){
        for(int i = 0; i < serverList.size(); i++){
            if(serverList.get(i).getServername().equals(servername)){
                serverList.remove(i);
                break;
            }
        }
    }
}
