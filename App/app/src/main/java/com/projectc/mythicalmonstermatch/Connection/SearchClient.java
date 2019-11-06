package com.projectc.mythicalmonstermatch.Connection;

import android.content.Context;
import android.util.Log;

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

    private Context context;
    private FindFragment findFragment;

    private String address;

    private Socket socket;                                                                          //Socket

    private OutputStream outputStream;                                                              //Für Kommunikation zwischen Client und Server
    private InputStream inputStream;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public SearchClient(FindFragment findFragment, Context context, String address){
        this.findFragment = findFragment;
        this.context = context;
        this.address = address;
    }

    @Override
    public void run(){
        connect();
    }

    private void sendMessage(String msg) {                                                          //Funktion fürs Senden von Nachrichten an Server
        try {
            Log.d("JETZT FLUSH VORHER", msg + "\r\n");
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            bufferedWriter.write(msg + " \r\n");                                                 //Message wird erstellt
            bufferedWriter.flush();                                                                 //Message wird zum Server gesendet
            Log.d("JETZT FLUSH", msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connect() {
        try {
            Log.d("JETZT2", address);
            socket = new Socket();                                         //Verbindung auf Server wird hergestellt
            socket.connect(new InetSocketAddress(address, 8080), 1000);

            Log.d("JETZT", "CONNECTED");
            inputStream = socket.getInputStream();                                                      //Kriegt In- und Outputstream und versieht diese mit Buffern fürs Lesen und Schreiben für Kommunikation
            Log.d("JETZT", " " + inputStream);
            outputStream = socket.getOutputStream();
            //bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

            Log.d("JETZT ALLES ANDERE", " " + (bufferedReader!= null) + " " + bufferedWriter);
            //WICHTIG \r\n am Ende der Zeile sonst nicht gelesen

            String line;
            sendMessage("ASK");
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            Log.d("JETZT", "" + bufferedReader + "");
            while((line = bufferedReader.readLine()) != null){                                  //While Schleife für Nachrichten verarbeitung
                Log.d("JETZT", "NACHER");
                String[] tokens = line.split(" ");                                        //Splited Nachricht auf. 1 Nachrichten Block ist Command Token
                Log.d("JETZTMSG", line);
                if(tokens != null && tokens.length > 0) {
                    String cmd = tokens[0];
                    if("ANSWER".equalsIgnoreCase(cmd)){
                        handleAnswer(line.split(" ", 4));
                    }
                }
            }
        } catch (ConnectException e){
            Log.d("JETZT", "CONNECTION FAILED");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.d("JETZT", "UFF" + e);
            e.printStackTrace();
        }
    }

    private void handleAnswer(String[] tokens) {                                                //Verarbeitet Abfrageergebnis aus ASK Abfrage
        Log.d("JETZT", "HANDLE");
        if(tokens[1].equalsIgnoreCase("0")){
            int playerCount = Integer.parseInt(tokens[2]);
            String serverName = tokens[3];
            findFragment.uebergabeArray.add(new ServerItem(serverName, playerCount, address));
        }
        sendMessage("ok");

        //TODO RECYCLER VIEW HINZUFÜGEN SERVER
    }
}
