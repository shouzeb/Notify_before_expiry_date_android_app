package com.example.bayan_oh.inspect;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;

public class NotifyMeService extends Service {


    DBHandler dbHandler = new DBHandler(this, null, null, 1);

    int pid;
    String pname;
    String xpdate;
    String ntdate;
    String remaining;
    byte[] image;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful
        super.onStartCommand(intent,flags, startId);
        // Get Extras form Previous Activity
        Bundle extras = intent.getExtras();
        if (extras != null) {
            pid = intent.getIntExtra("pid", 0);
            pname = intent.getStringExtra("pname");
            xpdate = intent.getStringExtra("xpdate");
            ntdate = intent.getStringExtra("ntdate");
            remaining = intent.getStringExtra("remaining");
            image = intent.getByteArrayExtra("image");
        }
        else {

        }

        // Build a notification
        NotificationCompat.Builder notification;
        notification = new NotificationCompat.Builder(getApplicationContext());
        notification.setAutoCancel(true); // Remove notification while app lunching
        notification.setSmallIcon(R.drawable.logo); // General Properties
        notification.setContentTitle(pname);
        notification.setWhen(System.currentTimeMillis());
        notification.setSound(MediaStore.Audio.Media.getContentUriForPath("notification/sound1.wav"));
        notification.setLargeIcon(BitmapFactory.decodeByteArray(image, 0, image.length));
        notification.setTicker("Product Expires in: " + xpdate);

        // Set Message
        String[] separated = remaining.split(":");
        String msg = "";

        if (Integer.valueOf(separated[0]) != 0)
            msg = msg + separated[0] + " Year ";
        if (Integer.valueOf(separated[1]) != 0)
            msg = msg + separated[1] + " Month ";
        if (Integer.valueOf(separated[2]) != 0)
            msg = msg + separated[2] + " Day ";
        if (Integer.valueOf(separated[0]) == 0 && Integer.valueOf(separated[1]) == 0 && Integer.valueOf(separated[2]) == 0)
            notification.setContentText("Product Expires Today");
        else
            notification.setContentText(msg + "Until Expiration");

        // Go To Home when Notification Clicked
        Intent dstintent = new Intent(getApplicationContext() , MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), pid, dstintent, PendingIntent.FLAG_ONE_SHOT);
        notification.setContentIntent(pendingIntent);

        // Build notification and issue it
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(pid,notification.build());

        return Service.START_NOT_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

