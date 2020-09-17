package com.scrappers.notepadsnippet.PushNotifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Vibrator;

import com.scrappers.notepadsnippet.MainScreens.MainActivity;
import com.scrappers.notepadsnippet.R;

import java.util.Date;

import androidx.core.app.NotificationCompat;

public class PushNotifications extends Service {

    @Override
    public void onCreate() {
//
//        registerReceiver(new BroadCastReciever(),new IntentFilter(Intent.ACTION_TIME_TICK));
//        registerReceiver(new BroadCastReciever(),new IntentFilter(Intent.ACTION_BOOT_COMPLETED));

        defineNotificationChannelID("2020");
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStart(Intent intent, int startId) {


        //getting system time
        Date d = new Date();
        long hours = d.getHours();
        final long minutes = d.getMinutes();
        long seconds = d.getSeconds();
        //setting time component
        final String timeComponent = hours + ":" + minutes + ":" + seconds;

            CountDownTimer ctd=new CountDownTimer(1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                if(hours==9 && minutes==30 && seconds==0){

                    Notify("Don't Forget To Take Your Today's Notes"
                            ,"BY SCRAPPERS", R.drawable.write_paper_ink,"2020",2020,"");


                }

            }

            @Override
            public void onFinish() {

                this.cancel();
                Runtime.getRuntime().freeMemory();
                System.gc();
                sendBroadcast(new Intent(getApplicationContext(),BroadCastReciever.class));
            }
        }.start();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public IBinder onBind(Intent intent) {
        sendBroadcast(new Intent(this,BroadCastReciever.class));
        return null;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        sendBroadcast(new Intent(this,BroadCastReciever.class));
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        sendBroadcast(new Intent(this,BroadCastReciever.class));
    }

    @Override
    public boolean onUnbind(Intent intent) {
        sendBroadcast(new Intent(this,BroadCastReciever.class));
        return super.onUnbind(intent);
    }





    public NotificationManager nm;
    public NotificationCompat.Builder notifBuilder;


    public void Notify(String title, String Messege, int icon, String CHANNEL_ID, int notifyId, String packagename) {
        vibrator((Vibrator) getSystemService(VIBRATOR_SERVICE));

//        //create a Custom Notification
//        RemoteViews notificationMinimizedState = new RemoteViews(getPackageName(), R.layout.shrinked_notification);
//        RemoteViews notificationExapndState = new RemoteViews(getPackageName(), R.layout.expanded_notification);
//
//
//        Intent i=new Intent("NotifyBtn.stop");
//        PendingIntent Pi= PendingIntent.getBroadcast(getApplicationContext(),22,i,PendingIntent.FLAG_UPDATE_CURRENT);
//        notificationExapndState.setOnClickPendingIntent(R.id.StoprecordingNotif,Pi);
//
//        notificationExapndState.setOnClickPendingIntent(R.id.PauserecordingNotif,Pi);

        //In order to open this activity on press
        Intent intent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);


        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notifBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(icon)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
//                .setCustomContentView(notificationMinimizedState)
//                .setCustomBigContentView(notificationExapndState)
//                .setCustomHeadsUpContentView(notificationMinimizedState)
                .setContentTitle(title)
                .setContentText(Messege)
                .setContentIntent(pendingIntent)
                .setOngoing(false)
                .setAutoCancel(true);
        nm.notify(notifyId, notifBuilder.build());


    }

    public void vibrator(Vibrator v) {
        v.vibrate(20);
    }


    public void defineNotificationChannelID(String CHANNEL_ID) {
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            nm = getSystemService(NotificationManager.class);
            nm.createNotificationChannel(channel);
        }
    }

}
