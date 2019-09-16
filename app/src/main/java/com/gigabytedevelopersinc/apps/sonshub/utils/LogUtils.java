package com.gigabytedevelopersinc.apps.sonshub.utils;

import android.util.Log;
import com.gigabytedevelopersinc.apps.sonshub.BuildConfig;
import timber.log.Timber;

/**
 * @author Created by Emmanuel Nwokoma (Founder and CEO at Gigabyte Developers) on 12/15/2018
 **/
public class LogUtils {
    private static final String LOG_PREFIX = "sonsHub_log";
    private static final int LOG_PREFIX_LENGTH = LOG_PREFIX.length();
    private static final int MAX_LOG_TAG_LENGTH = 23;

    private static String makeLogTag(String str) {
        if (str.length() > MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH) {
            return LOG_PREFIX + str.substring(0, MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH - 1);
        }

        return LOG_PREFIX + str;
    }

    /**
     * Don't use this when obfuscating class names!
     */
    public static String makeLogTag(Class cls) {
        return makeLogTag(cls.getSimpleName());
    }

    public static void LOGD(final String tag, String message) {
        // noinspection PointlessBooleanExpression,ConstantConditions
        if (BuildConfig.DEBUG || Log.isLoggable(tag, Log.DEBUG)) {
            Timber.tag(tag).d(message);
        }
    }

    public static void LOGD(final String tag, String message, Throwable cause) {
        // noinspection PointlessBooleanExpression,ConstantConditions
        if (BuildConfig.DEBUG || Log.isLoggable(tag, Log.DEBUG)) {
            Timber.tag(tag).d(cause, message);
        }
    }

    public static void LOGV(final String tag, String message) {
        // noinspection PointlessBooleanExpression,ConstantConditions
        if (BuildConfig.DEBUG && Log.isLoggable(tag, Log.VERBOSE)) {
            Timber.tag(tag).v(message);
        }
    }

    public static void LOGV(final String tag, String message, Throwable cause) {
        // noinspection PointlessBooleanExpression,ConstantConditions
        if (BuildConfig.DEBUG && Log.isLoggable(tag, Log.VERBOSE)) {
            Timber.tag(tag).v(cause, message);
        }
    }

    public static void LOGI(final String tag, String message) {
        Timber.tag(tag).i(message);
    }

    public static void LOGI(final String tag, String message, Throwable cause) {
        Timber.tag(tag).i(cause, message);
    }

    public static void LOGW(final String tag, String message) {
        Timber.tag(tag).w(message);
    }

    public static void LOGW(final String tag, String message, Throwable cause) {
        Timber.tag(tag).w(cause, message);
    }

    public static void LOGE(final String tag, String message) {
        Timber.tag(tag).e(message);
    }

    public static void LOGE(final String tag, String message, Throwable cause) {
        Timber.tag(tag).e(cause, message);
    }

    private LogUtils() {
    }
}
