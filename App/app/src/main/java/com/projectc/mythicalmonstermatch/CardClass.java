package com.projectc.mythicalmonstermatch;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;

import java.util.HashMap;
import java.util.Map;

public class CardClass {
    public int id;                                                                             //ID zwischen 1-30 zur leichten eindeutigen identifiezierung der Karte
    public String name;                                                                        //Name des Monster (z.B. Medusa, Golem etc)
    public Map<String, Float> attributeMap = new HashMap<String, Float>();                     //Attribute Map zum schlanken Vergleich Werte da über String Callbar
    public Bitmap img;                                                                          //Anzeige Bild, evtl auslagern TODO Nachforschen Möglichkeiten anzeigen

    public CardClass(int id, String name, float a1, float a2, float a3, float a4, float a5, Bitmap img){
        this.id = id;
        this.name = name;
        this.attributeMap.put("attribute1", a1);                                    //Platzhalterattributesnamen da noch nicht ausgewählt welche genommen werden
        this.attributeMap.put("attribute2", a2);
        this.attributeMap.put("attribute3", a3);
        this.attributeMap.put("attribute4", a4);
        this.attributeMap.put("attribute5", a5);
        this.img = img;
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
