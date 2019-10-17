package com.projectc.mythicalmonstermatch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Client {

    private String serverName;
    private String login;
    private int serverPort;
    private Socket socket;
    private OutputStream serverOut;
    private InputStream serverIn;
    private BufferedReader bufferedIn;

    private ArrayList<Integer> cardList = new ArrayList<>();
    private ArrayList<UserStatusListener> userStatusListener = new ArrayList<>();
    private ArrayList<MessageListener> messageListener = new ArrayList<>();

    public Client (String serverName, int serverPort, String login, int code) {
        this.serverName = serverName;
        this.serverPort = serverPort;
        this.login = login;

        if(!connect(code)) {
            System.out.println("KEINE VERBINDUNG");
        }else {
            System.out.println("VERBINDUNG");

        }

    }



    /*public static void main(String[] args) {
        Client client = new Client("localhost", 8080);

        client.addListener(new UserStatusListener() {
            @Override
            public void online(String login) {
                System.out.println("ONLINE " + login);
            }
            @Override
            public void offline(String login) {
                System.out.println("OFFLINE " + login);
            }
        });

        client.addMessageListener(new MessageListener() {
            @Override
            public void onMessage(String fromLogin, String msg) {
                System.out.println("You got a message from " + fromLogin + " ===> " + msg);
            }
        });

        if(!client.connect()) {
            System.out.println("KEINE VERBINDUNG");
        }else {
            System.out.println("VERBINDUNG");

            if(client.login("guest", "guest")) {
                System.out.println("Login successfull");

                client.msg("jim", "Hello World");

            } else {
                System.out.println("Login failed");
            }

            //client.logoff();

        }
    }*/


    private void msg(String sendTo, String msg) {
        String cmd = "msg " + sendTo + " " + msg;
        try {
            System.out.println(cmd);
            serverOut.write(cmd.getBytes());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void sendMessage(String msg){
        try {
            serverOut.write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public boolean connect(int code) {

        try {
            this.socket = new Socket(serverName, serverPort);
            this.serverOut = socket.getOutputStream();
            this.serverIn = socket.getInputStream();
            this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));

            startMessageReader();

            Thread.sleep(100);

            if(code == 0){
                sendMessage("ask");
                //RECYCLER VIEW HINZUFÜGEN SERVER
            } else if(code == 1){
                sendMessage("join " + login);
            }
            return true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }



    private void leave() {
        sendMessage("leave");
    }

    private void startMessageReader() {
        Thread t = new Thread() {
            @Override
            public void run() {
                readMessageLoop();
            }
        };
        t.start();
    }

    private void readMessageLoop() {
        try {
            String line;
            while((line = bufferedIn.readLine()) != null) {
                String[] tokens = line.split(" ");
                if(tokens != null && tokens.length > 0) {
                    String cmd = tokens[0];
                    if("denied".equalsIgnoreCase(cmd)){
                        handleDenie();
                    } else if("answer".equalsIgnoreCase(cmd)){
                        handleAnswer(tokens);
                    } else if("accept".equalsIgnoreCase(cmd)){
                        handleAccept();
                    } else if("start".equalsIgnoreCase(cmd)){
                        handleStart(tokens);
                    } else if("setName".equalsIgnoreCase(cmd)){
                        tokens = line.split(" ", 2);
                        handleNameChange(tokens[1]);
                    }
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

    private void handleNameChange(String token) {
        login = token;
    }

    private void handleStart(String[] tokens) {
        for(int i = 1; i < tokens.length; i++){
            String uebergabe = tokens[i].replace("[^\\d]", "");
            cardList.add(Integer.parseInt(uebergabe));
            //TODO START GAME FRAGMENT || ACTIVITY
        }
    }

    private void handleAccept() {
        //TODO START LOBBY FRAGMENT
    }

    private void handleAnswer(String[] tokens) {
        String serverName = tokens[1];
        String uebergabe = tokens[2].replace("[^\\d]", "");
        int playerCount = Integer.parseInt(uebergabe);
        //TODO RECYCLER VIEW HINZUFÜGEN SERVER
    }

    private void handleDenie() {
        //TODO Toast mit Denied Message oder Fragment
    }


    private void handleMessage(String[] tokens) {
        if(tokens.length >= 3) {
            String login = tokens[1];
            String msg = tokens[2];

            for(MessageListener listener: messageListener) {
                listener.onMessage(login, msg);
            }
        }

    }
    private void handleOffline(String[] tokens) {
        String login = tokens[1];
        for(UserStatusListener listener : userStatusListener) {
            listener.offline(login);
        }
    }
    private void handleOnline(String[] tokens) {
        String login = tokens[1];
        for(UserStatusListener listener : userStatusListener) {
            listener.online(login);
        }
    }
    public void addListener(UserStatusListener listener) {
        userStatusListener.add(listener);
    }
    public void removeListener(UserStatusListener listener) {
        userStatusListener.remove(listener);
    }
    public void addMessageListener(MessageListener listener) {
        messageListener.add(listener);
    }
    public void removeMessageListener(MessageListener listener) {
        messageListener.remove(listener);
    }

}
