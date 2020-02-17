package com.projectc.mythicalmonstermatch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CardSupportClass {                                                                     //ERSTELLT DAS KARTEN DECK, AUSGELAGERT FÜR BESSERE LESBARKEIT
    private Context context;
    private CardClass[] cardDeck = new CardClass[30];
    public CardSupportClass(Context context){
        this.context = context;
    }

    public CardClass[] createDeck(){
        BitmapFactory bf = new BitmapFactory();

        Bitmap[] b = {                                                                              //PLATZHALTER BILDER IN ARRAY
                bf.decodeResource(context.getResources(), R.drawable.image01),
                bf.decodeResource(context.getResources(), R.drawable.image02),
                bf.decodeResource(context.getResources(), R.drawable.image03),
                bf.decodeResource(context.getResources(), R.drawable.image04),
                bf.decodeResource(context.getResources(), R.drawable.image05),
                bf.decodeResource(context.getResources(), R.drawable.image06),
                bf.decodeResource(context.getResources(), R.drawable.image07),
                bf.decodeResource(context.getResources(), R.drawable.image08),
                bf.decodeResource(context.getResources(), R.drawable.image09),
        };

        Integer[] imgIDs = {                                                                        //BILD ID IN ARRAY ZUM ABRUFEN
                R.drawable.chimaere,
                R.drawable.dschinn,
                R.drawable.einhorn,
                R.drawable.medusa,
                R.drawable.minotaur,
                R.drawable.pegasus,
                R.drawable.satyr,
                R.drawable.zyklop
        };

        //ERSTELLUNG DER EINZELNEN KARTEN       ID, NAME, ATTRIBUT 1, ATTRIBUT 2, ATTRIBUT 3, ATTRIBUT 4, ATTRIBUT 5, BILD ID
        cardDeck[0] = new CardClass(0, "Chimäre",   4,  6,   6,    5,  7, imgIDs[0]);
        cardDeck[1] = new CardClass(1, "Dschinn",   1,  9,   1,    10, 2, imgIDs[1]);
        cardDeck[2] = new CardClass(2, "Einhorn",   3,  2,   7,    2,  1, imgIDs[2]);
        cardDeck[3] = new CardClass(3, "Medusa",    2,  8,   3,    7,  7, imgIDs[3]);
        cardDeck[4] = new CardClass(4, "Minotaur",  4,  3,   3,    3,  6, imgIDs[4]);
        cardDeck[5] = new CardClass(5, "Pegasus",   3,  3,   10,   2,  1, imgIDs[5]);
        cardDeck[6] = new CardClass(6, "Satyr",     2,  1,   5,    7,  2, imgIDs[6]);
        cardDeck[7] = new CardClass(7, "Zyklop",    5,  3,   2,    1,  4, imgIDs[7]);

        //PLATZHALTER KARTEN
        for(int i = 8; i < 30; i++){
            cardDeck[i] = new CardClass(i, ("card" + i), i, i, i, i, i, b[i%9]);
        }

        return  cardDeck;

    }
}
