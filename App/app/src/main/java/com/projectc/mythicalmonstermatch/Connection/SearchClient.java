package com.projectc.mythicalmonstermatch.Connection;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

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

public class SearchClient extends Thread{

    private Context context;

    private boolean start3StelleAktuell = true;
    private boolean joined = false;

    private String address;                                                                         //Network IP Addresse fürs Verbinden
    private String serverName;                                                                      //???
    private String[] addressUebergabe;
    private String startIP;                                                                        //IP-Address Zusatz fürs durch gehen bei ASK damit mehrere Server Angezeigt werden können

    private int count3 = 1;                                                                         //IP-Address 3. Zusatz fürs durch gehen
    private int count4 = 1;                                                                         //IP-Address 4. Zusatz fürs durch gehen
    private int start3Stelle;

    private Socket socket;                                                                          //Socket

    private ArrayList<Integer> cardList = new ArrayList<>();                                        //Liste an Karten die im Deck sind

    private OutputStream outputStream;                                                              //Für Kommunikation zwischen Client und Server
    private InputStream inputStream;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public SearchClient () {
    }

    @Override
    public void run(){
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);             //Initialisierung WifiManager => IP Address erfahren
        address = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());        //IP-Address abfragen und in passende Form bringen
        addressUebergabe = address.split("\\.");                                              //Split IP-Address in Zahlenblöcke
        start3Stelle = Integer.parseInt(addressUebergabe[2]);
        if(startIP == null) {
            startIP = address;
        }

        if(address != null){
            connect(count3, count4);                                                                   //Connect
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

    public void connect(int count3, int count4) {
        try {
            socket = new Socket(addressUebergabe[0] + "." + addressUebergabe[1] + "." + count3 + "." + count4, 8080);                              //Verbindung auf Server wird hergestellt
            inputStream = socket.getInputStream();                                              //Kriegt In- und Outputstream und versieht diese mit Buffern fürs Lesen und Schreiben für Kommunikation
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            outputStream = socket.getOutputStream();
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

            sendMessage("ASK");

            //WICHTIG \r\n am Ende der Zeile sonst nicht gelesen

            String line;

            while((line = bufferedReader.readLine()) != null){                                  //While Schleife für Nachrichten verarbeitung
                String[] tokens = line.split(" ");                                        //Splited Nachricht auf. 1 Nachrichten Block ist Command Token
                if(tokens != null && tokens.length > 0) {
                    String cmd = tokens[0];
                    if("answer".equalsIgnoreCase(cmd)){
                        handleAnswer(line.split(" ", 4));
                    }
                }
            }
            nextAddress(count3, count4);
        } catch (ConnectException e){
            Log.d("ERROR", "CONNECTION FAILED");
            nextAddress(count3, count4);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.d("ERROR", "UFF");
            e.printStackTrace();
        }
    }

    private void handleAnswer(String[] tokens) {                                                    //Verarbeitet Abfrageergebnis aus ASK Abfrage
        if(tokens[1].equalsIgnoreCase("0")){
            int playerCount = Integer.parseInt(tokens[2]);
            String serverName = tokens[3];
        }

        //TODO RECYCLER VIEW HINZUFÜGEN SERVER
    }
    private void nextAddress(int count3, int count4){
        if(!joined) {
            if (count4 >= 255) {
                if (count3 < 255) {
                    if (start3StelleAktuell) {
                        start3StelleAktuell = false;
                        connect(1, 1);
                    } else {
                        if ((count3 + 1) == start3Stelle) {
                            connect(count3 + 2, 1);
                        } else {
                            connect(count3++, 1);
                        }

                    }
                } else if (count3 >= 255) {
                    //TODO ZUENDE TOAST EVTL
                }
            } else {
                connect(count3, count4++);
            }
        }
    }

    public void joinServer(){
        joined = true;
    }
}
