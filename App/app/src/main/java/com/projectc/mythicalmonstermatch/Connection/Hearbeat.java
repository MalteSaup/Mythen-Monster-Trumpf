package com.projectc.mythicalmonstermatch.Connection;

public class Hearbeat extends Thread {                                                              //HEARTBEAT UM VERBINDUNG MIT CLIENTS ZU PRÜFEN UM VERBINDUNGSABBRUCH FESTZUSTELLEN
    ServerListener sL = null;

    public boolean running = true;                                                                  //RUNNING FLAG UM HEARBEAT BEENDEN ZU KÖNNEN

    public Hearbeat(ServerListener sL) {

        this.sL = sL;

    }

    @Override
    public void run() {
        if(sL != null) {
            if(sL.getLogin() != null){                                                              //CHECKT OB USERNAME != NULL IST DA SONST EIN FALSCHER HEARBEAT IM HINTERGRUND LAUFEN KANN
                while (running) {
                    try {
                        if (sL != null) {
                            sL.sendMessage("heartbeat");                                       //SENDET HEARBEAT NACHRICHT AN CLIENT
                            if(sL.getLogin().equals("localhost")){                                  //CHECKT OB USERNAME "localhost" IST DA DAS AUCH EIN SCHÖNER FEHLER WAR DER EIN UNNÖTIGEN HEARBEAT LAUFEN LASSEN HAT
                                running = false;
                            }
                        }
                        this.sleep(1000);                                                     //HEARTBEAT SCHLÄFT 1s
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }




}
