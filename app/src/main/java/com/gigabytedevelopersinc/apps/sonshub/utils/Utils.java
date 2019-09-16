package com.gigabytedevelopersinc.apps.sonshub.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import com.gigabytedevelopersinc.apps.sonshub.services.notification.MyFirebaseMessagingService;

/**
 * @author Created by Emmanuel Nwokoma (Founder and CEO at Gigabyte Developers) on 12/29/2018
 **/
public class Utils extends JobIntentService {
    public static final int JOB_ID = 1;

    public static void enqueueWork(Context context, Intent work) {
        try {
            enqueueWork(context, MyFirebaseMessagingService.class, JOB_ID, work);
        } catch (IllegalArgumentException ignored) {
            //
        }
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        // Service has started and a new intent has been received
        // Do work in here
        //initChannel();
    }

    public void initChannel() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "1";
        String channel2 = "2";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channelId,
                    "Channel 1", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("This is BNT");
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setShowBadge(true);
            notificationManager.createNotificationChannel(notificationChannel);

            NotificationChannel notificationChannel2 = new NotificationChannel(channel2,
                    "Channel 2", NotificationManager.IMPORTANCE_MIN);
            notificationChannel.setDescription("This is bTV");
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setShowBadge(true);
            notificationManager.createNotificationChannel(notificationChannel2);
        }
    }
}
