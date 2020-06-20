package com.projectc.mythicalmonstermatch.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.projectc.mythicalmonstermatch.CardClass;
import com.projectc.mythicalmonstermatch.MainActivity;
import com.projectc.mythicalmonstermatch.R;

import org.w3c.dom.Text;


public class ShowCardFragment extends Fragment {

    private CardClass[] ccDeck;                                                                     //KARTEN DECK

    private int pageCount = 1;                                                                      //PAGE COUNT UM ZU WISSEN AUF WELCHER SEITE MAN SICH BEFINDET

    private ImageView[] imageViews;                                                                 //ARRAY FÜR IMAGEVIEWS UM BILDER ANZEIGEN ZU KÖNNEN
    private TextView onPage;                                                                        //TEXT VIEW UM ANZUZEIGEN AUF WELCHER SEITE MAN SICH BEFINDET
    private View cardView;                                                                          //CARD VIEW WENN EINE KARTE AUSGEWÄHLT WIRD

    private ImageButton leftButton;                                                                 //LEFT UND RIGHT BUTTON UM DURCH DIE SEITEN ZU BLÄTTERN
    private ImageButton rightButton;
    private Button close;                                                                           //CLOSE BUTTON UM KARTE ZU SCHLIEßEN


    @Override
    public void onCreate(Bundle savedInstanceState) {
        getCardDeck();                                                                              //KARTEN DECK WIRD INITIALISIERT
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show_card, container, false);
    }

    @Override
    public void onActivityCreated(Bundle saveInstandesState) {
        View view = getView();
        final MainActivity mainActivity = (MainActivity)getActivity();                              //MAINACTIVITY WIRD ABGERUFEN, FINAL DA ES IM ONCLICK LISTENER BENÖTIGT WIRD
        imageViews = new ImageView[]{                                                               //IMAGE VIEWS UM DIE 9 VORSCHAUBILDER ANZUZEIGEN
                view.findViewById(R.id.imageView1),
                view.findViewById(R.id.imageView2),
                view.findViewById(R.id.imageView3),
                view.findViewById(R.id.imageView4),
                view.findViewById(R.id.imageView5),
                view.findViewById(R.id.imageView6),
                view.findViewById(R.id.imageView7),
                view.findViewById(R.id.imageView8),
                view.findViewById(R.id.imageView9)
        };
        onPage = view.findViewById(R.id.page);                                                      //ONPAGE WIRD GESETZT
        onPage.setText(pageCount + "/3");

        updateImages();                                                                             //BILDER WERDEN GESETZT

        //ONCLICKLISTENER FÜR RIGHT UND LEFT BUTTON
        rightButton = view.findViewById(R.id.buttonRight);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageCount++;
                if(pageCount % 4 == 0){                                                             // % 4 DA NUR 3 SEITEN VORHANDEN SIND
                    pageCount = 1;
                }
                onPage.setText(pageCount + "/3");                                                   //SEITENZAHL GEUPDATED
                updateImages();                                                                     //BILDER GEUPDATET
        }
        });                                                                                         //LEFT BUTTON GLEICH, AUßER DAS SEITENZAHL DEKREMENTIERT WIRD

        leftButton = view.findViewById(R.id.buttonLeft);

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageCount--;
                if(pageCount  == 0){
                    pageCount = 3;
                }
                onPage.setText(pageCount + "/3");
                updateImages();
            }
        });

        cardView = view.findViewById(R.id.fragment);                                                //CARD VIEW WIRD GESETZT UND SOFORT UNSICHTBAR GEMACHT
        cardView.setVisibility(View.GONE);

        cardView.setOnClickListener(new View.OnClickListener() {                                    //ONCLICKLISTENER FÜR CARDVIEW DAMIT DRUNTER LIEGENDE BILDER NICHT KLICKBAR SIND WENN KARTE GEÖFFNET IST
            @Override
            public void onClick(View view) {

            }
        });

        for(int i = 0; i < 9; i++){                                                                 //IMAGEVIEWS BEKOMMEN ONCLICKLISTENER UM KARTEN ZU ÖFFNEN FALLS AUSGEWÄHLT
            final int count = i;
            imageViews[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    activateFrag(count);

                    cardView.setVisibility(View.VISIBLE);                                           //CARD VIEW SICHTBAR GEMACHT
                    close.setVisibility(View.VISIBLE);                                              //CLOSE BUTTON SICHTBAR GEMACHT
                    mainActivity.mainFrag.onCard = true;                                            //ONCARD FLAG GESETZT
                }
            });
        }

        close = view.findViewById(R.id.closeCard);                                                  //CLOSE BUTTON MIT ONCLICKLISTENER VERSEHEN
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardView.setVisibility(View.GONE);                                                  //CARD VIEW UMD CLOSE BUTTON WIEDER UNSICHTBAR MACHEN
                close.setVisibility(View.GONE);
                mainActivity.mainFrag.onCard = false;                                               //ONCARD FLAG WIEDER FALSE SETZEN
            }
        });
        close.setVisibility(View.GONE);
        super.onActivityCreated(saveInstandesState);
    }

    public void getCardDeck(){                                                                      //HOLT SICH KARTEN DECK AUS MAIN ACTIVITY
        MainActivity activity = (MainActivity) getActivity();
        ccDeck = activity.getCardDeck();
    }

    public void updateImages(){                                                                     //UPDATED BILDER BEI SEITEN ÄNDERUNG
        int countStart = 9 * (pageCount - 1);
        for(int i = 0; i < 9; i++){                                                                 //UPDATED IMAGE VIEWS
            if(ccDeck[countStart+i].img != null){imageViews[i].setImageBitmap(ccDeck[countStart+i].img);}
            else if(ccDeck[countStart+i].imgID != null){imageViews[i].setImageResource(ccDeck[countStart+i].imgID);}
        }
    }

    public void activateFrag(int uebergabe){                                                        //BEI ONCLICK AUF IMAGE VIEW WIRD KARTEN FRAG AKTIVIERT
        int count = (9 * (pageCount - 1)) + uebergabe;                                              //FINDET RAUS WELCHE KARTE ANGEKLICKT WURDE
        ImageView iV = cardView.findViewById(R.id.imageView);
        cardView.findViewById(R.id.submitBtn).setVisibility(View.GONE);                             //MACHT SUBMIT BUTTON UNSICHTBAR (WIRD NUR FÜRS SPIEL BENÖTIGT)

        TableLayout attributeHolder = cardView.findViewById(R.id.attributeHolder);

        TextView[] textViews = new TextView[6];
        textViews[0] = cardView.findViewById(R.id.cardName);
        for (int i = 0; i<attributeHolder.getChildCount(); i++){
            TableRow row = (TableRow) attributeHolder.getChildAt(i);
            textViews[i+1] = (TextView) row.getChildAt(row.getChildCount()-1);
        }

        textViews[0].setText(ccDeck[count].name);                                                   //SETZT WERTE IN ENTSPRECHENDE TV's
        textViews[1].setText("" + ccDeck[count].attributeMap.get("attribute1"));
        textViews[2].setText("" + ccDeck[count].attributeMap.get("attribute2"));
        textViews[3].setText("" + ccDeck[count].attributeMap.get("attribute3"));
        textViews[4].setText("" + ccDeck[count].attributeMap.get("attribute4"));
        textViews[5].setText("" + ccDeck[count].attributeMap.get("attribute5"));

        if(ccDeck[count].img != null){iV.setImageBitmap(ccDeck[count].img);}                        //SETZT BILD => MOMENTAN MIT IF; ELSE IF DA PLATZHALTENBILDER NOCH VORHANDEN UND ANDERS AUFGERUFEN
        else if(ccDeck[count].imgID != null){iV.setImageResource(ccDeck[count].imgID);}
    }
}
