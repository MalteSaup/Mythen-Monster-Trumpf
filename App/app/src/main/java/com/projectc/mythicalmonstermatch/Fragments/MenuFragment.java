package com.projectc.mythicalmonstermatch.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.projectc.mythicalmonstermatch.MainActivity;
import com.projectc.mythicalmonstermatch.R;


public class MenuFragment extends Fragment {

    private EditText enterName;                                                                     //EINGABE FELD

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
        enterName = getView().findViewById(R.id.editName);                                          //EINGABEFELD AUS FRAGMENT AUSGELESEN
        final MainActivity activity = (MainActivity) getActivity();                                 //MAIN ACTIVITY AUSGELESEN, FINAL DA ER IM TEXTCHANGELISTENER VERWENDET WERDEN MUSS
        String uebergabeString = activity.getName();                                                //FALLS NAME SCHON GESETZT WIRD ER IM EINGABEFELD ANGEZEIGT => DAFÜR NAME AUS MAINACTIVITY GEZOGEN
        if(!uebergabeString.trim().isEmpty()){                                                      //ÜBERPRÜFT OB STRING NICHT LEER IST (trim FÜR LÖSCHEN UNNÖTIGEN WHITESPACE)
            enterName.setText(uebergabeString);
        }
        enterName.addTextChangedListener(new TextWatcher() {                                        //TEXTCHANGELISTENER ZUM EINGABEFELD HINZUGEFÜGT
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}      //BEFOR UND ONTEXTCHANGE MÜSSEN VORHANDEN SEIN FÜR TEXTCHANGELISTENER
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {                                       //WENN TEXT GEÄNDERT WIRD ER IN ACTIVITY GEÄNDERT
                activity.setName(editable.toString());
            }
        });
        super.onActivityCreated(saveInstandesState);
    }
}
