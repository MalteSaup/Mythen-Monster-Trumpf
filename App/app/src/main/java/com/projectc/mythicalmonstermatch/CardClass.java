package com.projectc.mythicalmonstermatch;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.Map;

public class CardClass{
    public int id;                                                                             //ID zwischen 1-30 zur leichten eindeutigen identifiezierung der Karte
    public String name;                                                                        //Name des Monster (z.B. Medusa, Golem etc)
    public Map<String, Integer> attributeMap = new HashMap<String, Integer>();                     //Attribute Map zum schlanken Vergleich Werte da über String Callbar
    public Bitmap img = null;                                                                          //Anzeige Bild, evtl auslagern TODO Nachforschen Möglichkeiten anzeigen
    public Integer imgID = null;

    public CardClass(int id, String name, int a1, int a2, int a3, int a4, int a5, Bitmap img){
        this.id = id;
        this.name = name;
        this.attributeMap.put("attribute1", a1);                                    //Platzhalterattributesnamen da noch nicht ausgewählt welche genommen werden
        this.attributeMap.put("attribute2", a2);
        this.attributeMap.put("attribute3", a3);
        this.attributeMap.put("attribute4", a4);
        this.attributeMap.put("attribute5", a5);
        this.img = img;
    }

    public CardClass(int id, String name, int a1, int a2, int a3, int a4, int a5,  Integer img){
        //Log.d("IMAGEVIEW", "" + img);
        this.id = id;
        this.name = name;
        this.attributeMap.put("attribute1", a1);                                    //Platzhalterattributesnamen da noch nicht ausgewählt welche genommen werden
        this.attributeMap.put("attribute2", a2);
        this.attributeMap.put("attribute3", a3);
        this.attributeMap.put("attribute4", a4);
        this.attributeMap.put("attribute5", a5);
        this.imgID = img;
    }

    //Auskommentiert muss woanders hin
    /*boolean compareAttributes(String attribute, float enemyAttribute, boolean mode){    //Vergleicht Attribute, Mode dafür da ob der Wert höher oder tiefer sein soll
        if(mode){
            if(attributeMap.get(attribute) > enemyAttribute){return true;}
            else{return false;}
        }
        else{
            if(attributeMap.get(attribute) < enemyAttribute){return true;}
            else{return false;}
        }
    }*/
}
