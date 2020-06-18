package com.projectc.mythicalmonstermatch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CardSupportClass {                                                                     //ERSTELLT DAS KARTEN DECK, AUSGELAGERT FÜR BESSERE LESBARKEIT
    private Context context;
    private CardClass[] cardDeck = new CardClass[27];
    public CardSupportClass(Context context){
        this.context = context;
    }

    public CardClass[] createDeck(){
        BitmapFactory bf = new BitmapFactory();

        /*
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
        */


        Integer[] imgIDs = {                                                                        //BILD ID IN ARRAY ZUM ABRUFEN
                R.drawable.chimaere,
                R.drawable.dschinn,
                R.drawable.einhorn,
                R.drawable.medusa,
                R.drawable.minotaur,
                R.drawable.pegasus,
                R.drawable.satyr,
                R.drawable.zyklop,
                R.drawable.basilisk,
                R.drawable.fenrir,
                R.drawable.longg, // had to be spelt with 2 gs because long is a reserved java word lmao
                R.drawable.cthulhu,
                R.drawable.gargoyle,
                R.drawable.golem,
                R.drawable.succubus,
                R.drawable.fafnir,
                R.drawable.reiter,
                R.drawable.poltergeist,
                R.drawable.leviathan,
                R.drawable.cerberus,
                R.drawable.sphinx,
                R.drawable.hydra,
                R.drawable.sirene,
                R.drawable.kraken,
                R.drawable.harpyie,
                R.drawable.phoenix,
                R.drawable.greif


        };

        //ERSTELLUNG DER EINZELNEN KARTEN       ID, NAME, ATTRIBUT 1, ATTRIBUT 2, ATTRIBUT 3, ATTRIBUT 4, ATTRIBUT 5, BILD ID
        cardDeck[0]  = new CardClass(0, "Chimäre",          4,  6,   6,    5,  7, imgIDs[0]);
        cardDeck[1]  = new CardClass(1, "Dschinn",          1,  9,   1,    10, 2, imgIDs[1]);
        cardDeck[2]  = new CardClass(2, "Einhorn",          3,  2,   7,    2,  1, imgIDs[2]);
        cardDeck[3]  = new CardClass(3, "Medusa",           2,  8,   3,    7,  7, imgIDs[3]);
        cardDeck[4]  = new CardClass(4, "Minotaur",         4,  3,   3,    3,  6, imgIDs[4]);
        cardDeck[5]  = new CardClass(5, "Pegasus",          3,  3,   10,   2,  1, imgIDs[5]);
        cardDeck[6]  = new CardClass(6, "Satyr",            2,  1,   5,    7,  2, imgIDs[6]);
        cardDeck[7]  = new CardClass(7, "Zyklop",           5,  3,   2,    1,  4, imgIDs[7]);
        cardDeck[8]  = new CardClass(8, "Basilisk",         1,  3,   5,    7,  4, imgIDs[8]);
        cardDeck[9]  = new CardClass(9, "Fenrir",           10, 10,  2,    3,  8, imgIDs[9]);
        cardDeck[10] = new CardClass(10,"Long",             7,  8,   9,    1,  3, imgIDs[10]);
        cardDeck[11] = new CardClass(11,"Cthulhu",          10, 9,   2,    3,  8, imgIDs[11]);
        cardDeck[12] = new CardClass(12,"Gargoyle",         2,  4,   1,    4,  5, imgIDs[12]);
        cardDeck[13] = new CardClass(13,"Golem",            5,  5,   1,    3,  4, imgIDs[13]);
        cardDeck[14] = new CardClass(14,"Succubus",         2,  3,   3,    8,  1, imgIDs[14]);
        cardDeck[15] = new CardClass(15,"Fafnir",           7,  7,   8,    7,  6, imgIDs[15]);
        cardDeck[16] = new CardClass(16,"Kopfloser Reiter", 4,  6,   7,    5,  8, imgIDs[16]);
        cardDeck[17] = new CardClass(17,"Poltergeist",      1,  3,   4,    6,  9, imgIDs[17]);
        cardDeck[18] = new CardClass(18,"Leviathan",        9,  9,   5,    4,  7, imgIDs[18]);
        cardDeck[19] = new CardClass(19,"Cerberus",         5,  6,   5,    5,  6, imgIDs[19]);
        cardDeck[20] = new CardClass(20,"Sphinx",           4,  4,   6,    9,  5, imgIDs[20]);
        cardDeck[21] = new CardClass(21,"Hydra",            8,  9,   2,    4,  9, imgIDs[21]);
        cardDeck[22] = new CardClass(22,"Sirene",           2,  1,   3,    8,  3, imgIDs[22]);
        cardDeck[23] = new CardClass(23,"Kraken",           9,  8,   4,    1,  8, imgIDs[23]);
        cardDeck[24] = new CardClass(24,"Harpyie",          2,  2,   8,    6,  4, imgIDs[24]);
        cardDeck[25] = new CardClass(25,"Phönix",           6,  10,  9,    1,  2, imgIDs[25]);
        cardDeck[26] = new CardClass(26,"Greif",            3,  5,   8,    2,  3, imgIDs[26]);

        /*
        //PLATZHALTER KARTEN
        for(int i = 8; i < 30; i++){
            cardDeck[i] = new CardClass(i, ("card" + i), i, i, i, i, i, b[i%9]);
        }*/

        return  cardDeck;

    }
}
