package com.gigabytedevelopersinc.apps.sonshub.services.notification;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import com.gigabytedevelopersinc.apps.sonshub.utils.misc.Configs;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;

import timber.log.Timber;

/**
 * @author Created by Emmanuel Nwokoma (Founder and CEO at Gigabyte Developers) on 12/29/2018
 **/
public class RegistrationService extends IntentService {
    /**
     * Constructor
     */
    public RegistrationService() {
        super("RegistrationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Generate or download the registration 'token'.
        String registrationToken = null;
        try {
            // Get the registration 'token'.
            registrationToken = FirebaseInstanceId.getInstance().getToken("869927257766", "FCM");

            // Subscribe to a topic. The app is able now to receive notifications from this topic.
            FirebaseMessaging.getInstance().subscribeToTopic(Configs.TOPIC_GLOBAL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Timber.tag("Registration Token").e(registrationToken);
    }
}