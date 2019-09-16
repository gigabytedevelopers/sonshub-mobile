package com.gigabytedevelopersinc.apps.sonshub.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.gigabytedevelopersinc.apps.sonshub.services.notification.MyFirebaseMessagingService;
import com.gigabytedevelopersinc.apps.sonshub.utils.Utils;

import java.util.Objects;

/**
 * @author Created by Emmanuel Nwokoma (Founder and CEO at Gigabyte Developers) on 11/30/2018
 **/
// BEGIN_INCLUDE(autostart)
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.requireNonNull(intent.getAction()).equals("android.intent.action.BOOT_COMPLETED")) {
            Intent firebaseService = new Intent(context, MyFirebaseMessagingService.class);
            Utils.enqueueWork(context, firebaseService);
        }
    }
}
//END_INCLUDE(autostart)