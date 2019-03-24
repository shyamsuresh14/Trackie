package com.test.shyam.trackie;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.provider.Telephony;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.sql.Date;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ReceiverService extends Service {

    private SmsBroadcastReceiver mReciever;

    public ReceiverService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showNotification("started");
        Calendar c = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = df.format(c.getTime());
        Log.d("Trackie","Service started at " + time);
        mReciever = new SmsBroadcastReceiver();
        registerReceiver(mReciever,new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));
        Log.d("Trackie", "Receiver started");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    private void showNotification(String state){
        Calendar c = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = df.format(c.getTime());
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification mNotification = new NotificationCompat.Builder(this,"1000")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("Background Service " + state + "!")
                .setContentTitle("Trackie Bg Service " + state)
                .setContentText("Bg Service " + state + " at " + time + "." + " Click to restart")
                .setAutoCancel(true)
                .setSound(RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .build();
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(200, mNotification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Calendar c = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = df.format(c.getTime());
        Log.d("Trackie","Service stopped at " + time);
        showNotification("stopped");
        unregisterReceiver(mReciever);
    }
}
