package com.test.shyam.trackie;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class LocationTimer extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "simply",Toast.LENGTH_SHORT ).show();
    }
}
