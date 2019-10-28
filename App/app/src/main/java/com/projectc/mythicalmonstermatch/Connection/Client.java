package com.projectc.mythicalmonstermatch.Connection;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import com.projectc.mythicalmonstermatch.WifiHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;

import static android.content.Context.WIFI_SERVICE;

public class Client extends Thread{

    private Context context;

    private String address;                                                                         //Network IP Addresse fürs Verbinden
    private String serverName;                                                                      //???
    private String login;                                                                           //Username

    //private int serverPort;                                                                       //PORT
    private int count = 0;                                                                          //IP-Address Zusatz fürs durch gehen
    private int code;                                                                               //0 = ASK ; 1 = JOIN
    private int startIP = 0;                                                                        //IP-Address Zusatz fürs durch gehen bei ASK damit mehrere Server Angezeigt werden können

    private boolean gameStarted = false;                                                            //Zeigt an ob das Spiel gestartet wurde

    private Socket socket;                                                                          //Socket

    private ArrayList<Integer> cardList = new ArrayList<>();                                        //Liste an Karten die im Deck sind

    private OutputStream outputStream;                                                              //Für Kommunikation zwischen Client und Server
    private InputStream inputStream;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public Client (String serverName, String login, int code, int startIP, Context context) {
        this.serverName = serverName;
        this.login = login;
        this.code = code;
        this.context = context;
        this.startIP = startIP;
    }

    @Override
    public void run(){
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);             //Initialisierung WifiManager => IP Address erfahren
        address = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());        //IP-Address abfragen und in passende Form bringen
        String[] uebergabe = address.split("\\.");                                            //Split IP-Address in Zahlenblöcke

        if(startIP > 0){
            count = startIP;                                                                        //Start IP wird als Count gesetzt für Erfragung mehrerer Server
        }
        address = address.substring(0, address.length()-uebergabe[uebergabe.length-1].length());    //von IP-Address wird letzter Zahlenblock abgeschnitten

        if(address != null){
            connect(code, count);                                                                   //Connect
        }else{
            //TODO WLAN AKTIVIEREN TOAST ODER VORHER PRÜFEN
        }

    }

    private void sendMessage(String msg) {                                                          //Funktion fürs Senden von Nachrichten an Server
        try {
            bufferedWriter.write(msg + "\r\n");                                                 //Message wird erstellt
            bufferedWriter.flush();                                                                 //Message wird zum Server gesendet
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessageToAll(String msg){
        //TODO Message an alle CMD überlegen und integrieren
    }


    public void connect(int code, int count) {
            try {
                socket = new Socket(address + count, 8080);                              //Verbindung auf Server wird hergestellt
                inputStream = socket.getInputStream();                                              //Kriegt In- und Outputstream und versieht diese mit Buffern fürs Lesen und Schreiben für Kommunikation
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                outputStream = socket.getOutputStream();
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));



                //WICHTIG \r\n am Ende der Zeile sonst nicht gelesen

                String line;

                while((line = bufferedReader.readLine()) != null){                                  //While Schleife für Nachrichten verarbeitung
                    String[] tokens = line.split(" ");                                        //Splited Nachricht auf. 1 Nachrichten Block ist Command Token
                    if(tokens != null && tokens.length > 0) {
                        String cmd = tokens[0];
                        if("denied".equalsIgnoreCase(cmd)){                                         //Wenn Denied Command (Nachfolgenden IF Statements dasselbe bloß anderes Command mit anderer Funktion)
                            handleDenie();
                        } else if("answer".equalsIgnoreCase(cmd)){
                            handleAnswer(tokens);
                        } else if("accept".equalsIgnoreCase(cmd)){
                            handleAccept();
                        } else if("start".equalsIgnoreCase(cmd)){
                            handleStart(tokens);
                        } else if("setName".equalsIgnoreCase(cmd)){
                            tokens = line.split(" ", 2);                                //Erneute splitung von String da Nachricht im Inhaltsblock Leerzeichen enthalten darf
                            handleNameChange(tokens[1]);
                        }
                    }
                }

            } catch (ConnectException e){
                Log.d("ERROR", "CONNECTION FAILED");
                if(count < 255) {
                    connect(code,count+1);
                } else {
                    //TODO evtl GAME COUNT einführen und wenn dieser 0 ist dann TOAST für keinen Server gefunden
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.d("ERROR", "UFF");
                e.printStackTrace();
            }
    }



    private void leave() {                                                                          //Sendet Leave Nachricht an Server
        sendMessage("leave");
    }

    private void handleNameChange(String token) {                                                   //Setzt Namenstoken aus Nachricht als Namen
        login = token;
    }

    private void handleStart(String[] tokens) {                                                     //Wird aufgerufen wenn Spiel gestartet wird von Host
        for(int i = 1; i < tokens.length; i++){
            String uebergabe = tokens[i].replace("[^\\d]", "");
            cardList.add(Integer.parseInt(uebergabe));
            //TODO START GAME FRAGMENT || ACTIVITY
        }
        gameStarted = true;
    }

    private void handleAccept() {                                                                   //Wird aufgerufen wenn der Server den Join akzeptiert
        //TODO START LOBBY FRAGMENT
    }

    private void handleAnswer(String[] tokens) {                                                    //Verarbeitet Abfrageergebnis aus ASK Abfrage
        String serverName = tokens[1];
        String uebergabe = tokens[2].replace("[^\\d]", "");
        int playerCount = Integer.parseInt(uebergabe);
        //TODO RECYCLER VIEW HINZUFÜGEN SERVER
    }

    private void handleDenie() {                                                                    //Wird aufgerufen wenn der Server den Join verweigert
        //TODO Toast mit Denied Message oder Fragment
    }
}
