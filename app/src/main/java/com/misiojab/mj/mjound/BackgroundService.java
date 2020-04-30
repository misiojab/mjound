package com.misiojab.mj.mjound;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

public class BackgroundService extends Service {

    public static final int NOTIF_ID = 1;
    public static final String NOTIF_CHANNEL_ID = "ChannelID";
    private static NotificationManager notificationManager;
    private static Notification noti;






    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId){

        createNotificationChannel();
        startForeground();

     return super.onStartCommand(intent, flags, startId);
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_desc);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIF_CHANNEL_ID, name, importance);
            channel.setDescription(description);

            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);


        }
    }

    @SuppressLint("ResourceAsColor")
    public void startForeground() {

        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);


        startForeground(NOTIF_ID, noti = new NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_thunder)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Selected mode: "
                        + SavedData.readString(SavedData.SELECTED_PRESET, this))
                .setContentIntent(pendingIntent)
                .setColorized(true)
                .setColor(R.color.notification_background)    //kolor
                .build());
    }

    @SuppressLint("ResourceAsColor")
    public static void updateNotification(Context c){
        Intent notificationIntent = new Intent(c, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(c, 0,
                notificationIntent, 0);

        notificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        noti = new NotificationCompat.Builder(c, NOTIF_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_thunder)
                .setContentIntent(pendingIntent)
                .setContentText("Selected mode: "
                        + SavedData.readString(SavedData.SELECTED_PRESET, c))
                .setColorized(true)
                .setColor(R.color.notification_background)
                .build();

        notificationManager.notify(NOTIF_ID, noti);

    }


    






}
