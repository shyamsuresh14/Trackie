package com.test.shyam.trackie;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.sip.SipSession;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsMessage;
import android.telephony.gsm.SmsManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    private String TRACK = "Friends";
    private String REQ = "requests";
    private String TRACE = "Trace";
    private boolean r_status = false;

    public SmsBroadcastReceiver(){

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                // get sms objects
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus.length == 0) {
                    return;
                }
                // large message might be broken into many
                SmsMessage[] messages = new SmsMessage[pdus.length];
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    sb.append(messages[i].getMessageBody());
                }
                String sender = messages[0].getOriginatingAddress();
                String message = sb.toString();
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                r_status = receivedStatus(context, sender);

                if(message.equals("loc?")) {
                    if (r_status) {
                        sendLoc(context, sender);
                    } else {
                        SmsManager.getDefault().sendTextMessage(sender, null, "You don't have permission to track the user", null, null);

                    }
                }
                else if (message.equals("req_loc?")) {
                    showNotification(context,"Track Request Received", "You received a request from " + sender, sender ,1);
                }

                else if (message.split(":")[0].equals("latlon")) {
                    representLoc(context, message);
                }
                else if (message.equals("acc")) {
                    showNotification(context, "Track Request Accepted", sender + " has accepted your request", sender, 0);
                    SharedPreferences pref = context.getSharedPreferences(REQ, 0);
                    Set<String> req = pref.getStringSet(REQ, new HashSet<String>());
                    SharedPreferences preferences = context.getSharedPreferences(TRACE, 0);
                    Set<String> trace = preferences.getStringSet(TRACE, new HashSet<String>());
                    for(String r: req){
                        if((r.split(",")[1].equals(sender)) || (("+91" + r.split(",")[1]).equals(sender))){
                            trace.add(r);
                            preferences.edit().clear().apply();
                            preferences.edit().putStringSet(TRACE, trace).apply();
                            break;
                        }
                    }
                    removeRequest(context,sender);
                }
                else if (message.equals("rej")) {
                    showNotification(context, "Track Request Rejected", sender + " has declined your request", sender, 0);
                    removeRequest(context, sender);
                }

                //playRingtone(context);
                // prevent any other broadcast receivers from receiving broadcast
                r_status = false;
                abortBroadcast();
            }
        }
    }

    private boolean receivedStatus(Context context, String sender){
        SharedPreferences pref = context.getSharedPreferences(TRACK, 0);
        Set<String> friends = pref.getStringSet(TRACK, new HashSet<String>());
        for(String friend : friends){
            if(friend.split(",")[1].equals(sender) || ("+91" + friend.split(",")[1]).equals(sender))
                return true;
        }
        return false;
    }

    private void showNotification(Context context, String title, String message, String sender, int opt){
        Intent intent1;
        if(opt == 1) {
            intent1 = new Intent(context, RequestPage.class);
            intent1.putExtra("sender", sender);
        }
        else
            intent1 = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context, "1001")
                .setAutoCancel(true)
                .setTicker(message)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setSound(RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
                .build();

        NotificationManagerCompat nm = NotificationManagerCompat.from(context);
        nm.notify(100, notification);
    }

    private void representLoc(Context context, String message){
        /*String[] loc = message.split(":")[1].split(",");
        String lat = loc[0], lon = loc[1];
        String uri = "geo:<lat>,<lon>?q=" + lat + "," + lon + "(Label+Name)";
        //String uri = String.format(Locale.ENGLISH, "geo:%f,%f", lat, lon);*/
        String loc = message.split(":")[1];
        Intent intent1 = new Intent(context, MapsActivity.class);
        intent1.putExtra("Loc", loc);
        //Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        context.startActivity(intent1);
    }

    private void sendLoc(Context context, String sender){
        GPSTracker tracker = new GPSTracker(context);
        String lat = String.valueOf(tracker.getLatitude()), lon = String.valueOf(tracker.getLongitude());
        String loc = "latlon:" + lat + "," + lon;
        Log.d("Trackie", "loc sent: " + lat + "," + lon);
        SmsManager.getDefault().sendTextMessage(sender, null,loc,null,null);
    }

    private void removeRequest(Context context, String sender){
        SharedPreferences pref = context.getSharedPreferences(REQ, 0);
        Set<String> requests = pref.getStringSet(REQ, new HashSet<String>());
        for(String req: requests){
            if(req.split(",")[1].equals(sender) || ("+91" + req.split(",")[1]).equals(sender)){
                requests.remove(req);
                break;
            }
        }
        pref.edit().clear().apply();
        pref.edit().putStringSet(REQ, requests).apply();
    }

    private void playRingtone(Context context){
        MediaPlayer player = MediaPlayer.create(context, Settings.System.DEFAULT_RINGTONE_URI);
        player.start();
        if(player.getDuration() == 10000)
            player.stop();
    }

}
