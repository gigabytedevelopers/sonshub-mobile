package com.gigabytedevelopersinc.apps.sonshub.services.notification;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.crashlytics.android.Crashlytics;
import com.gigabytedevelopersinc.apps.sonshub.activities.MainActivity;
import com.gigabytedevelopersinc.apps.sonshub.utils.NotificationUtil;
import com.gigabytedevelopersinc.apps.sonshub.utils.misc.Configs;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import org.json.JSONException;
import org.json.JSONObject;
import timber.log.Timber;

import java.io.IOException;

import static com.gigabytedevelopersinc.apps.sonshub.utils.misc.Configs.TOPIC_GLOBAL;

/**
 * @author Created by Emmanuel Nwokoma (Founder and CEO at Gigabyte Developers) on 12/10/2018
 **/
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtil notificationUtils;

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        /*Timber.tag("NEW_TOKEN").e(token);
        Timber.tag("Refreshed token:").e(token);
        String refreshedToken = Objects.requireNonNull(FirebaseInstanceId.getInstance().getInstanceId().getResult()).getToken();

        // Saving reg id to shared preferences
        storeRegIdInPref(refreshedToken);

        // sending reg id to your server
        sendRegistrationToServer(refreshedToken);

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Configs.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);*/

        try {
            // Get updated InstanceID token.
            String refreshedToken = null;
            refreshedToken = FirebaseInstanceId.getInstance().getToken("211008111337", "FCM");
            Timber.tag(TAG).d("Refreshed token: %s", refreshedToken);

            // now subscribe to `global` topic to receive app wide notifications
            FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_GLOBAL);

            // If you want to send messages to this application instance or
            // manage this apps subscriptions on the server side, send the
            // Instance ID token to your app server.
            storeRegIdInPref(refreshedToken);
            sendRegistrationToServer(refreshedToken);

            // Notify UI that registration has completed, so the progress indicator can be hidden.
            Intent registrationComplete = new Intent(Configs.REGISTRATION_COMPLETE);
            registrationComplete.putExtra("token", refreshedToken);
            LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Timber.tag(TAG).e("From: %s", remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Timber.tag(TAG).e("Notification Body: %s", remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Timber.tag(TAG).e("Data Payload: %s", remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Timber.tag(TAG).e("Exception: %s", e.getMessage());
                Crashlytics.log(1, TAG, "NotificationException: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message) {
        if (!NotificationUtil.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Configs.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtil notificationUtils = new NotificationUtil(getApplicationContext());
            notificationUtils.playNotificationSound();
        } else {
            // If the app is in background, allow Firebase itself to handle the notification
        }
    }

    private void handleDataMessage(JSONObject json) {
        Timber.tag(TAG).e("push json: %s", json.toString());

        try {
            JSONObject data = json.getJSONObject("data");

            String title = data.getString("title");
            String message = data.getString("message");
            boolean isBackground = data.getBoolean("is_background");
            String imageUrl = data.getString("image");
            String timestamp = data.getString("timestamp");
            JSONObject payload = data.getJSONObject("payload");

            Timber.tag(TAG).e("title: %s", title);
            Timber.tag(TAG).e("message: %s", message);
            Timber.tag(TAG).e("isBackground: %s", isBackground);
            Timber.tag(TAG).e("payload: %s", payload.toString());
            Timber.tag(TAG).e("imageUrl: %s", imageUrl);
            Timber.tag(TAG).e("timestamp: %s", timestamp);


            if (!NotificationUtil.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Configs.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtil notificationUtils = new NotificationUtil(getApplicationContext());
                notificationUtils.playNotificationSound();
            } else {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                resultIntent.putExtra("message", message);

                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                }
            }
        } catch (JSONException e) {
            Timber.tag(TAG).e("Json Exception: %s", e.getMessage());
        } catch (Exception e) {
            Timber.tag(TAG).e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtil(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtil(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        Timber.tag(TAG).e("sendRegistrationToServer: %s", token);
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Configs.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.apply();
    }
}