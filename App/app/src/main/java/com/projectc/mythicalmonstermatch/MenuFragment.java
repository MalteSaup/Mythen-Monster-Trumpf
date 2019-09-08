package com.projectc.mythicalmonstermatch;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


public class MenuFragment extends Fragment {

    private EditText enterName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onActivityCreated(Bundle saveInstandesState) {
        enterName = getView().findViewById(R.id.editName);
        final MainActivity activity = (MainActivity) getActivity();
        String uebergabeString = activity.getName();
        if(!uebergabeString.isEmpty()){
            enterName.setText(uebergabeString);
        }
        enterName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                activity.setName(editable.toString());
            }
        });
        super.onActivityCreated(saveInstandesState);
    }
}
