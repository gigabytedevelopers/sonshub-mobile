package com.gigabytedevelopersinc.apps.sonshub

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.os.SystemClock
import com.chartboost.sdk.Chartboost
import com.gigabytedevelopersinc.apps.sonshub.Repository.repositoriesModule
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2.Fetch.Impl.setDefaultInstanceConfiguration
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2.FetchConfiguration
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2.HttpUrlConnectionDownloader
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2core.Downloader
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2okhttp.OkHttpDownloader
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2rx.RxFetch.Impl.setDefaultRxInstanceConfiguration
import com.gigabytedevelopersinc.apps.sonshub.players.music.db.roomModule
import com.gigabytedevelopersinc.apps.sonshub.players.music.logging.FabricTree
import com.gigabytedevelopersinc.apps.sonshub.players.music.mainModule
import com.gigabytedevelopersinc.apps.sonshub.players.music.network.lastFmModule
import com.gigabytedevelopersinc.apps.sonshub.players.music.network.lyricsModule
import com.gigabytedevelopersinc.apps.sonshub.players.music.network.networkModule
import com.gigabytedevelopersinc.apps.sonshub.players.music.notifications.notificationModule
import com.gigabytedevelopersinc.apps.sonshub.players.music.playback.mediaModule
import com.gigabytedevelopersinc.apps.sonshub.players.music.prefsModule
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.viewmodels.viewModelsModule
import com.gigabytedevelopersinc.apps.sonshub.services.notification.MyFirebaseMessagingService
import com.gigabytedevelopersinc.apps.sonshub.utils.misc.AnalyticsManager
import com.google.firebase.analytics.FirebaseAnalytics
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber
import timber.log.Timber.DebugTree
import java.util.concurrent.TimeUnit


class App : Application(), Application.ActivityLifecycleCallbacks {

    private val appID = "5ee63675bbee080963db16ae"
    private val appSignature = "71375eccdc048d18acd3cd10d215fd7bfb590e23"

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        instance = this

        disableDeathOnFileUriExposure()
        registerActivityLifecycleCallbacks(instance)

        //Chartboost.getPIDataUseConsent().value
        ///Chartboost.setPIDataUseConsent(instance, Chartboost.CBPIDataUseConsent.YES_BEHAVIORAL)
        Chartboost.startWithAppId(context, appID, appSignature)

    if (!BuildConfig.DEBUG) {
            val builder = VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
            // Enables Crash, Error Reporting for detailed, well structured Error messages on Firebase Console
            Timber.plant(FabricTree())
            // Initialize User Analytics to track usage events within SonsHub Mobile
            AnalyticsManager.initialize(context)
            AnalyticsManager.logEvent(FirebaseAnalytics.Event.APP_OPEN)
            AnalyticsManager.logEvent(FirebaseAnalytics.Event.SEARCH)
            AnalyticsManager.logEvent(FirebaseAnalytics.Event.SHARE)
            // TODO: Grab and Track User ratings too
        } else {
            // Disable Crash, Error Reporting since we can easily make use of the logcat during development
            Timber.plant(DebugTree())
        }
        SystemClock.sleep(TimeUnit.SECONDS.toMillis(1))

        val modules = listOf(
            mainModule,
            mediaModule,
            prefsModule,
            networkModule,
            roomModule,
            notificationModule,
            repositoriesModule,
            viewModelsModule,
            lyricsModule,
            lastFmModule
        )
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(modules)
        }

        val fetchConfiguration =
            FetchConfiguration.Builder(this)
                .enableRetryOnNetworkGain(true)
                .setDownloadConcurrentLimit(3)
                .setHttpDownloader(HttpUrlConnectionDownloader(Downloader.FileDownloaderType.PARALLEL)) // OR
                //.setHttpDownloader(getOkHttpDownloader())
                .build()
        setDefaultInstanceConfiguration(fetchConfiguration)
        setDefaultRxInstanceConfiguration(fetchConfiguration)
    }

    private fun disableDeathOnFileUriExposure() {
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                val m =
                    StrictMode::class.java.getMethod(resources.getString(R.string.runtimeStrictMode))
                m.invoke(null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val okHttpDownloader: OkHttpDownloader
        get() {
            val okHttpClient = OkHttpClient.Builder().build()
            return OkHttpDownloader(
                okHttpClient,
                Downloader.FileDownloaderType.PARALLEL
            )
        }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(
        activity: Activity,
        outState: Bundle?
    ) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        val restartService =
            Intent(context, MyFirebaseMessagingService::class.java)
        val pendingIntent = PendingIntent.getService(
            context, 1,
            restartService, PendingIntent.FLAG_ONE_SHOT
        )
        val alarmManager =
            (getSystemService(Context.ALARM_SERVICE) as AlarmManager)
        alarmManager[AlarmManager.ELAPSED_REALTIME, 5000] = pendingIntent
    }

    companion object {
        private val TAG = App::class.java.simpleName

        /**
         * Returns the application context
         *
         * @return application context
         */
        /**
         * Keeps a reference of the application context
         */
        @SuppressLint("StaticFieldLeak")
        var context: Context? = null
            private set

        @SuppressLint("StaticFieldLeak")
        var instance: App? = null
            private set

    }
}