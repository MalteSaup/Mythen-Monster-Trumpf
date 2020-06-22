package com.projectc.mythicalmonstermatch.Connection;

import android.os.AsyncTask;

public class AsyncSupportClass {
    public AsyncSupportClass(){
    }
    public void sendMessage(final ServerListener sL, final String msg){
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                sL.sendMessage(msg);
                return null;
            }
        };
        asyncTask.execute();
    }
    public void sendMessage(final Client client, final String msg){
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                client.sendMessage(msg);
                return null;
            }
        };
        asyncTask.execute();
    }
}

