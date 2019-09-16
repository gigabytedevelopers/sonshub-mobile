package com.gigabytedevelopersinc.apps.sonshub.utils.misc;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import com.gigabytedevelopersinc.apps.sonshub.BuildConfig;
import com.gigabytedevelopersinc.apps.sonshub.utils.LogUtils;
import com.google.firebase.analytics.FirebaseAnalytics;
import org.jetbrains.annotations.Contract;

/**
 * @author Created by Emmanuel Nwokoma (Founder and CEO at Gigabyte Developers) on 12/15/2018
 **/
public class AnalyticsManager {
    private static Context sAppContext = null;

    private static FirebaseAnalytics mFirebaseAnalytics;
    private final static String TAG = LogUtils.makeLogTag(AnalyticsManager.class);

    public static String FILE_TYPE = "file_type";
    public static String FILE_COUNT = "file_count";
    public static String FILE_MOVE = "file_move";

    private static boolean canSend() {
        return sAppContext != null && mFirebaseAnalytics != null && !BuildConfig.DEBUG;
    }

    public static synchronized void initialize(Context context) {
        sAppContext = context;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);

        setProperty("DeviceType", Configs.getDeviceType(context));
        setProperty("Rooted", Boolean.toString(Configs.isRooted()));
    }

    private static void setProperty(String propertyName, String propertyValue){
        if (!canSend()) {
            return;
        }
        mFirebaseAnalytics.setUserProperty(propertyName, propertyValue);
    }

    public static void logEvent(String eventName){
        if (!canSend()) {
            return;
        }
        mFirebaseAnalytics.logEvent(eventName, new Bundle());
    }

    public static void logEvent(String eventName, Bundle params){
        if (!canSend()) {
            return;
        }
        mFirebaseAnalytics.logEvent(eventName, params);
    }

    public static void setCurrentScreen(Activity activity, String screenName){
        if (!canSend()) {
            return;
        }

        if (null != screenName) {
            mFirebaseAnalytics.setCurrentScreen(activity, screenName, screenName);
        }
    }
}
