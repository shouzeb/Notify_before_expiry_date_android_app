package com.example.bayan_oh.inspect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class NotifyMeReceiver extends BroadcastReceiver {
    int pid;
    String pname;
    String xpdate;
    String ntdate;
    String remaining;
    byte[] image;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        // Get Extras form Previous Activity
        Bundle extras = intent.getExtras();
        if (extras != null) {
            pid = extras.getInt("pid");
            pname = extras.getString("pname");
            xpdate = extras.getString("xpdate");
            ntdate = extras.getString("ntdate");
            remaining = extras.getString("remaining");
            image = extras.getByteArray("image");
        }
        else {

        }

        // Start and trigger a service
        Intent k = new Intent(context, NotifyMeService.class);
        k.putExtra("pid", pid);
        k.putExtra("pname", pname);
        k.putExtra("xpdate", xpdate);
        k.putExtra("ntdate", ntdate);
        k.putExtra("remaining", remaining);
        k.putExtra("image", image);
        context.startService(k);

    }
}
