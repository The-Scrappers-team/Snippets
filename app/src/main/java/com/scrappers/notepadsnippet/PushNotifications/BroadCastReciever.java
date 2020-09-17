package com.scrappers.notepadsnippet.PushNotifications;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class BroadCastReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {


        intent.getAction();
        try {
            @SuppressLint("StaticFieldLeak") AsyncTask<Object, Object, Object> execute = new AsyncTask<Object, Object, Object>() {
                @Override
                protected Object doInBackground(Object[] objects) {
                    context.startService(new Intent(context, PushNotifications.class));
                    return null;
                }
            }.execute();


        }catch (IllegalStateException ex){
            ex.printStackTrace();
        }
    }


}
