package com.gigabytedevelopersinc.apps.sonshub;

import android.annotation.SuppressLint;
import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.SystemClock;

import com.gigabytedevelopersinc.apps.sonshub.Repository.RepositoriesModuleKt;
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2.Fetch;
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2.FetchConfiguration;
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2.HttpUrlConnectionDownloader;
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2core.Downloader;
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2okhttp.OkHttpDownloader;
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2rx.RxFetch;
import com.gigabytedevelopersinc.apps.sonshub.players.music.MainModuleKt;
import com.gigabytedevelopersinc.apps.sonshub.players.music.PrefsModuleKt;
import com.gigabytedevelopersinc.apps.sonshub.players.music.db.RoomModuleKt;
import com.gigabytedevelopersinc.apps.sonshub.players.music.logging.FabricTree;
import com.gigabytedevelopersinc.apps.sonshub.players.music.network.LastFmModuleKt;
import com.gigabytedevelopersinc.apps.sonshub.players.music.network.LyricsModuleKt;
import com.gigabytedevelopersinc.apps.sonshub.players.music.network.NetworkModuleKt;
import com.gigabytedevelopersinc.apps.sonshub.players.music.notifications.NotificationsModuleKt;
import com.gigabytedevelopersinc.apps.sonshub.players.music.playback.MediaModuleKt;
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.viewmodels.ViewModelsModuleKt;
import com.gigabytedevelopersinc.apps.sonshub.services.notification.MyFirebaseMessagingService;
import com.gigabytedevelopersinc.apps.sonshub.utils.misc.AnalyticsManager;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.FirebaseDatabase;

import kotlin.collections.MapsKt;
import kotlin.jvm.functions.Function1;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import org.koin.android.ext.android.ComponentCallbacksExtKt;
import org.koin.android.logger.AndroidLogger;
import timber.log.Timber;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class App extends Application implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = App.class.getSimpleName();
    /**
     * Keeps a reference of the application context
     */
    @SuppressLint("StaticFieldLeak")
    private static Context sContext;
    @SuppressLint("StaticFieldLeak")
    private static App sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        sInstance = this;
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        registerActivityLifecycleCallbacks(sInstance);

        if (!BuildConfig.DEBUG) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            // Enables Crash, Error Reporting for detailed, well structured Error messages on Firebase Console
            Timber.plant(new FabricTree());

            if (Build.VERSION.SDK_INT >= 24) {
                try {
                    Method m = StrictMode.class.getMethod(getResources().getString(R.string.runtimeStrictMode));
                    m.invoke(null);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            // Initialize User Analytics to track usage events within SonsHub Mobile
            AnalyticsManager.initialize(sContext);
            AnalyticsManager.logEvent(FirebaseAnalytics.Event.APP_OPEN);
            AnalyticsManager.logEvent(FirebaseAnalytics.Event.SEARCH);
            AnalyticsManager.logEvent(FirebaseAnalytics.Event.SHARE);
            // TODO: Grab and Track User ratings too
        } else {
            // Disable Crash, Error Reporting since we can easily make use of the logcat during development
            Timber.plant(new Timber.DebugTree());
        }
        SystemClock.sleep(TimeUnit.SECONDS.toMillis(1));

        List<Function1<org.koin.core.KoinContext, org.koin.dsl.context.ModuleDefinition>> modules = Arrays.asList(
                MainModuleKt.getMainModule(),
                MediaModuleKt.getMediaModule(),
                PrefsModuleKt.getPrefsModule(),
                NetworkModuleKt.getNetworkModule(),
                RoomModuleKt.getRoomModule(),
                NotificationsModuleKt.getNotificationModule(),
                RepositoriesModuleKt.getRepositoriesModule(),
                ViewModelsModuleKt.getViewModelsModule(),
                LyricsModuleKt.getLyricsModule(),
                LastFmModuleKt.getLastFmModule()
        );
        ComponentCallbacksExtKt.startKoin(
                this, sContext, modules, MapsKt.emptyMap(), false, new AndroidLogger());

        final FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(this)
                .enableRetryOnNetworkGain(true)
                .setDownloadConcurrentLimit(3)
                .setHttpDownloader(new HttpUrlConnectionDownloader(Downloader.FileDownloaderType.PARALLEL))
                // OR
                //.setHttpDownloader(getOkHttpDownloader())
                .build();
        Fetch.Impl.setDefaultInstanceConfiguration(fetchConfiguration);
        RxFetch.Impl.setDefaultRxInstanceConfiguration(fetchConfiguration);
    }

    private OkHttpDownloader getOkHttpDownloader() {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        return new OkHttpDownloader(okHttpClient,
                Downloader.FileDownloaderType.PARALLEL);
    }

    /**
     * Returns the application context
     *
     * @return application context
     */
    public static Context getContext() {
        return sContext;
    }

    public static App getInstance() {
        return sInstance;
    }

    @Override
    public void onActivityCreated(@NotNull Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NotNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NotNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NotNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NotNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NotNull Activity activity, @NotNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NotNull Activity activity) {
        Intent restartService = new Intent(sContext, MyFirebaseMessagingService.class);
        PendingIntent pendingIntent = PendingIntent.getService(sContext, 1,
                restartService, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME, 5000, pendingIntent);
    }
}
