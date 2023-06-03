package com.saqibdb.YahrtzeitsOfGedolim;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;

import android.util.Log;

import com.saqibdb.YahrtzeitsOfGedolim.activity.SplashActivity;

import java.text.SimpleDateFormat;


public class BroadcastManager extends BroadcastReceiver {
    private SimpleDateFormat dateFormatEvent = new SimpleDateFormat("EEE, d MMM, yyyy");
    private SimpleDateFormat dateFormatTime = new SimpleDateFormat("h:mm a");
    public static String NOTIFICATION_CHANNEL_ID = "10001";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("MyAPP", "onReceive() called");
        try {
            String message = intent.getStringExtra("msg");
            String date = intent.getStringExtra("date");
            int id = intent.getIntExtra("id", 99);
            createNotification(context, "Today's Yartzeits!", message, id);
            Log.d("eventTesting22", date + " " + message);

        } catch (Exception e) {
            Log.i("date", "error == " + e.getMessage());
        }
    }

    public void createNotification(Context mContext, String title, String message, int id) {
        /**Creates an explicit intent for an Activity in your app**/
        Intent resultIntent = new Intent(mContext, SplashActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext,
                id, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(false)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setSound(null, null);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(id, mBuilder.build());
    }
}