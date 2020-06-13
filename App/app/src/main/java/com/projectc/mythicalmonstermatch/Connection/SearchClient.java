package com.projectc.mythicalmonstermatch.Connection;

import android.content.Context;

import com.projectc.mythicalmonstermatch.Fragments.FindFragment;
import com.projectc.mythicalmonstermatch.ServerItem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SearchClient extends Thread{

    private InterruptedException iE = new InterruptedException();                                   //CUSTOM EXCEPTION FÜRS CLIENT KILLEN
    private Context context;
    private FindFragment findFragment;                                                              //FIND FRAGMENT UM ZUGRIFF AUF RECYCLER VIEW ZU BEKOMMEN

    private String address;                                                                         //IP ADDRESSE AN DER WIR MOMENTAN SIND

    private Socket socket;                                                                          //SOCKET

    private OutputStream outputStream;                                                              //IN- UND OUTPUT STREAM(BUFFERED READER UND WRITER) FÜR KOMMUNIKATION VON CLIENT UND SERVER
    private InputStream inputStream;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    private boolean notEnded = true;                                                                //NOT ENDED FLAG
    private boolean directConnect = false;

    public SearchClient(FindFragment findFragment, Context context, String address){
        this.findFragment = findFragment;
        this.context = context;
        this.address = address;
    }

    public SearchClient(FindFragment findFragment, Context context, String address, boolean directConnect){
        this.findFragment = findFragment;
        this.context = context;
        this.address = address;
        this.directConnect = directConnect;
    }

    @Override
    public void run(){                                                                              //STARTET SUCH FUNKTION
        try {
            connect();
        } catch (InterruptedException e) {
            if(directConnect){findFragment.directConnectNow();}
            e.printStackTrace();
        }
    }

    private void sendMessage(String msg) {                                                          //SENDET NACHRICHT AN SERVER
        try {
            bufferedWriter.write(msg + " \r\n");                                                //NACHRICHT GESCHRIEBEN; \r\n UM ENDE DER NACHRICHT ANZUZEIGEN
            bufferedWriter.flush();                                                                 //NACHRICHT SICHER GESENDET
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connect() throws InterruptedException {                                             //SUCH FUNKTION
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(address, 8080), 500);                 //VERBINDUNG ZU SERVER AN ANGEGEBENER IP ADDRESSE WIRD AUFGEBAUT, TIMEOUT FALLS SERVER NICHT INNERHALB 500ms ANTWORTET

            inputStream = socket.getInputStream();                                                  //IN UND OUTPUT STREMA WERDEN VOM SOCKET AUSGELESEN FÜR NACHRICHTVERKEHR
            outputStream = socket.getOutputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

            String line;

            sendMessage("ASK");                                                                //SENDET ANFRAGE NACHRICHT WIE DER SERVER STATUS IST

            while((line = bufferedReader.readLine()) != null && notEnded){                          //WHILE SCHLEIFE FÜR NACHRICHT VERARBEITUNG
                String[] tokens = line.split(" ");                                            //NACHRICHT IN SEGMENTE AUFGETEILT, 1. SEGMENT IST DER COMMAND
                if(tokens != null && tokens.length > 0) {
                    String cmd = tokens[0];
                    //COMMAND VERARBEITUNG
                    if("ANSWER".equalsIgnoreCase(cmd)){                                             //ANTWORT VON SERVER AUF STATUS ANFRAGE
                        handleAnswer(line.split(" ", 4));
                    }
                }
            }
        } catch (ConnectException e){
            if(directConnect){
                findFragment.directConnectFailed();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleAnswer(String[] tokens) throws InterruptedException {                        //VERARBEITET ANTWORT
        int playerCount = Integer.parseInt(tokens[2]);                                              //ANZAHL GEJOINTER SPIELER
        String serverName = tokens[3];                                                              //SERVER NAME
        int startState = Integer.parseInt(tokens[1]);                                               //OB SERVER SCHON GESTARTET IST
        if(!directConnect){findFragment.uebergabeArray.add(new ServerItem(serverName, playerCount, address, startState));}  //FÜGT SERVER DER SERVERLISTE HINZU
        else{findFragment.directConnectItem = new ServerItem(serverName, playerCount, address, startState);}
        throw iE;                                                                                   //SCHMEIßT EXCEPTION WENN FERTIG
    }
}
