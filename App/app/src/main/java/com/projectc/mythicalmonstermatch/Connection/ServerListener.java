package com.projectc.mythicalmonstermatch.Connection;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ServerListener extends Thread{
    private Socket socket;                                                                          //SOCKET
    private Server server;                                                                          //SERVER UM ZUGRIFF AUF DESSEN VARIABLEN ZU BEKOMMEN
    private String login;                                                                           //USERNAME
    private boolean rejoin;                                                                         //REJOIN FLAG
    private Hearbeat hearbeat;                                                                      //HEARTBEAT
    private int id = -1;                                                                            //USER ID
    private int count = 0;                                                                          //COUNT UM TIMEOUT ZU BEMERKEN
    public boolean connectionLoss = false;

    private BufferedReader bufferedReader;                                                          //BUFFERED READER UND WRITER FÜR KOMMUNIKATION VON SERVER UND CLIENT
    private BufferedWriter bufferedWriter;

    public ServerListener(Server server, Socket socket, boolean rejoin){
        this.server = server;                                                                       //SERVER UND SOCKET VARIABLEN GESETZT
        this.socket = socket;
        this.rejoin = rejoin;                                                                       //REJOIN FLAG GESETZT
        hearbeat = new Hearbeat(this);                                                          //HEARTBEAT ERSTELLT, ABER NOCH NICHT GESTARTET
    }

    @Override
    public void run(){
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));    //BUFFERED READER UND WRITER WERDEN ERSTELLT
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String line;
            while((line = bufferedReader.readLine()) != null){                                      //WHILE SCHLEIFE FÜR NACHRICHT VERARBEITUNG
                String tokens[] = line.split(" ");                                            //NACHRICHT IN SEGMENTE AUFGETEILT, 1. SEGMENT IST DER COMMAND
                if(tokens != null && tokens.length > 0){
                    String cmd = tokens[0];
                    Log.d("SERVER CMD", cmd);
                    //COMMAND VERARBEITUNG
                    if(cmd.equalsIgnoreCase("ask")){                                    //ASK ANFRAGE WIRD BEARBEITET
                        handleAsk();
                    }else if(cmd.equalsIgnoreCase("join")){                             //JOIN ANFRAGE WIRD BEARBEITET
                        String[] joinTokens = line.split(" ", 3);                       //NACHRICHT NOCHMAL AUGETEILT UM ID UND NAME ABZUTRENNEN
                        if(!handleJoin(joinTokens)){
                            sendMessage("denied");                                             //SENDET DENIED NACHRICHT FALLS ABGELEHNT
                            break;
                        }
                    }else if(cmd.equalsIgnoreCase("leave")){                            //LEAVE NACHRICHT WIRD BEARBEITET
                        leave();
                        break;                                                                      //WHILE SCHLEIFE BEENDET
                    }else if(cmd.equalsIgnoreCase("start")){                            //SPIEL WIRD GESTARTET
                        handleStart(login);
                    } else if(cmd.equalsIgnoreCase("getplayer")){                       //SPIELER ANFRAGE WIRD BEARBEITET
                        handlePlayerRequest();
                    }else if(cmd.equalsIgnoreCase("playerremoved")){                    //SPIELER HAT LOBBY VERLASSEN WIRD BEARBEITET
                        handlePlayerRemove();
                    }else if(cmd.equalsIgnoreCase("playeradded")){                      //SPIELER HAT LOBBY BETRETEN WIRD BEARBEITET
                        handlePlayerAdded();
                    }else if(cmd.equalsIgnoreCase("heartbeat")){                        //HEARTBEAT NACHRICHT VOM CLIENT UM FESTZUSTELLEN FALLS EIN CLIENT DIE VERBINDUNG VERLOREN HAT
                        handleHeartbeat();
                    }else if(cmd.equalsIgnoreCase("nextturn")){
                        handleNextTurn(); 
                    }

                }

            }
            socket.close();                                                                         //SOCKET WIRD AM ENDE GESCHLOSSEN

        } catch (IOException e) {
            Log.d("IOEXCEPTION", "JETZT");
            handleConnectionLost();                                                                 //CONNECTION LOST WIRD BEARBEITET
            e.printStackTrace();
        } catch (Exception e){
            Log.d("IOEXCEPTION", "UNCATCHED");
        }
    }

    private void handleNextTurn() {
        server.setNextTurn(this.id);
    }

    private void handleHeartbeat() {                                                                //COUNT WIRD DEKREMENTIERT WENN HEARTBEAT ERHALTEN
        count--;
    }

    private String createPlayerInfo(String cmd){
        ArrayList<ServerListener> serverListeners = server.getServerListeners();
        for(ServerListener sL : serverListeners){
            cmd += sL.login + ":" + sL.id + ";";                                                    //NACHRICHT ERSTELLUNG MIT CMD UND ALLEN USERNAMEN UND IDs VON GEJOINEDTEN CLIENTS
        }
        return cmd;
    }
    private void handlePlayerAdded() {                                                              //NACHRICHT AN CLIENT WENN SPIELER GEJOINED IST
        sendMessage(createPlayerInfo("playeradded ;"));
    }

    private void handlePlayerRemove() {                                                             //NACHRICHT AN CLIENT WENN SPIELER GELEAVED IST
        sendMessage(createPlayerInfo("playerremoved ;"));
    }

    private void handlePlayerRequest() {                                                            //NACHRICHT AN CLIENT NACH PLAYER REQUEST
        sendMessage(createPlayerInfo("playeranswer ;"));
    }

    private void handleStart(String login) {                                                        //SPIEL WIRD GESTARTET ???
        if(server.startGame(login)){
            ArrayList<ServerListener> listenerList = server.getServerListeners();
            for(ServerListener sL : listenerList) {
                sL.sendMessage("start");
            }
        }
    }

    private void joinPlayerAdded(){                                                                 //NACH ERFOLGREICHEN JOIN SENDET NACHRICHT AN ALLE CLIENTS DAS ER GEJOINED IST
        sendMessage("accept " + this.id);                                                      //JOIN BESTÄTIGUNG AN CLIENT
        if(!rejoin){server.addPlayer(this);}                                                    //FALLS KEIN REJOIN WIRD NEUES ITEM IN PLAYERLIST HINZUGEFÜGT
        else{                                                                                       //BEI REJOIN WIRD ANHAND DER ID DER SERVERLISTENER AN EIN VORHANDENDES PLAYERITEM GEKNÜPFT
            ArrayList<ServerListener> sLL = server.getServerListeners();
            for(int i = 0; i < sLL.size(); i++){
                if(sLL.get(i).getID() == this.id){
                    //TODO BEI REJOIN KOPPLUNG AN VORHANDENDES ITEM IN PLAYERLIST
                }
            }
        }
        for(ServerListener sL : server.getServerListeners()){
            if(sL != this){sL.handlePlayerAdded();}                                                 //NACHRICHT AN ALLE CLIENTS
        }
    }

    private boolean handleJoin(String[] tokens) {                                                   //VERARBEITUNG JOIN ANFRAGE
        if(server.playerCount() >= 5 && !server.getStartState()){                                   //WENN SPIELER COUNT GRÖßER GLEICH 5 UND SPIEL NICHT GESTARTET ANFRAGE ABGELEHNT
            return false;
        } else if(server.getStartState() && Integer.parseInt(tokens[1]) != -1){                     //WENN SPIEL GESTARTET UND ID VORHANDEN -> ÜBERPRÜFT OB ES EIN REJOIN IST ANHAND ID UND USERNAME
            if(checkID(tokens[1], tokens[2])){
                rejoin = true;                                                                      //REJOIN FLAG GESETZT
                this.id = Integer.parseInt(tokens[1]);                                              //ID GESETZT
                this.login = tokens[2];                                                             //USERNAME GESETZT
                joinPlayerAdded();
                hearbeat.start();                                                                   //HEARTBEAT GESTARTET
                return true;
            }
            return false;                                                                           //FALLS KEIN REJOIN, DANN ABEGELEHNT
        }
        else{                                                                                       //WENN SPIELER COUNT KLEINER ALS 5 UND SPIEL NICHT GESTARTET ANFRAGE ANGENOMMEN
            this.login = tokens[2];                                                                 //USERNAMEN GESETZT
            this.id = generateID();                                                                 //ID ERZEUGT
            hearbeat.start();                                                                       //HEARTBEAT GESTARTET
            joinPlayerAdded();

            return true;
        }
    }

    private boolean checkID(String id, String usrName) {                                            //ÜBERPRÜFT OB VORHANDENE ID UND USERNAME ÜBEREINSTIMMT MIT ÜBERMITTELTER ID UND USERNAME
        int id_numb = Integer.parseInt(id);
        ArrayList<ServerListener> sLL = server.getServerListeners();                                //RUFT DAFÜR PLAYERLIST AB
        for(int i = 0; i < sLL.size(); i++){
            ServerListener sL = sLL.get(i);
            if(id_numb == sL.id){
                if(usrName.equals(sL.login)){
                    return true;
                }
            }
        }
        return false;
    }

    public void sendMessage(String msg){                                                            //SENDET NACHRICHT AN CLIENT
        try {
            bufferedWriter.write(msg + "\r\n");
            bufferedWriter.flush();
            if(msg.equalsIgnoreCase("heartbeat")){
                count++;                                                                            //INKREMENTIERT COUNT
                if(count > 3){                                                                      //WENN COUNT GRÖßER ALS 3 IST WIRD DIE VERBINDUNG AUFGELÖST DA SCHEINBAR VERLOREN
                    handleConnectionLost();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            handleConnectionLost();                                                                 //CONNECTION LOST WIRD BEARBEITET
        }
    }

    private void handleAsk() {                                                                      //ANFRAGE WIRD BEARBEITET
        int started = 0;
        if(server.getStartState()){started = 1;}
        sendMessage("ANSWER " + started + " " + server.playerCount() + " " + server.serverName);    //SENDET NACHRICHT MIT SERVERNAME PLAYERCOUNT UND OB DAS SPIEL GESTARTET IST
    }

    private void handleConnectionLost(){                                                            //CONNECTION LOST WIRD BEARBEITET
        //TODO DIFFERENT BEHAVIOUR WHEN GAME STARTED
        Log.d("KILL", "KILL");
        connectionLoss = true;
        leave();                                                                                    //LEAVE WIRD GESTARTET
    }

    private void leave(){                                                                           //LEAVE WIRD BEARBEITET
        Log.d("SERVER", "LEAVE");
        hearbeat.running = false;                                                                   //HEARTBEAT WIRD BEENDET
        server.removeItems(this);                                                               //SERVER LÖSCHT SERVER LISTENER AUS LISTEN
        for(ServerListener sL : server.getServerListeners()){
            if(server.getStartState()){                                                             //TODO ANDERES VERHALTEN WENN SPIEL GESTARTET
                //TODO CONNECTION LOSS MESSAGE TO EVERYONE
            } else {
                if(sL != this){sL.handlePlayerRemove();}
            }
        }

    }

    private void kill(){                                                                            //MOMENTAN NICHT BENÖTIGT EVTL SPÄTER GELÖSCHT
        server.removeItems(this);
        server.removePlayer(this);
    }

    public String getLogin(){
        return login;
    }
    public int getID(){return id;}

    private int generateID() {                                                                      //ID GENERATOR
        int id = -1;                                                                                //PLATZHALTER ID
        boolean idGenerated = false;                                                                //ID GENERATED FLAG
        ArrayList<ServerListener> sLL = server.getServerListeners();
        while (!idGenerated) {                                                                      //ERZEUGUNG NEUER ID SOLANGE BIS VALIDE ID ENTSTANDEN IST
            id = (int)(Math.random() * 899999999 + 100000000);                                      //ID IMMER ZWISCHEN 100000000 UND 999999999
            idGenerated = true;                                                                     //ID GENERATED FLAG GESETZT
            for (int i = 0; i < sLL.size(); i++) {                                                  //VERGLEICH OB GENERIERTE ID SCHON VORHANDEN
                if (sLL.get(i).id == id) {
                    if(!rejoin || (rejoin && !this.login.equals(sLL.get(i).getLogin()))) {          //WENN VORHANDEN FLAG WIEDER GELÖSCHT
                        idGenerated = false;
                    }
                }
            }
        }
        if (id != -1) {
            return id;
        } else {                                                                                    //FALLS IWIE KEINE ID GENERIERT WURDE REKURSIVER AUFRUF
            Log.d("ERROR", "HOFFENTLICH NIE VORHANDEN");
            return generateID();
        }
    }

    public void closeHeartbeat() {                                                                  //HEARTBEAT BEENDEN FUNKTION 
        hearbeat.running = false;
    }
}
