package com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2.database.FetchDatabaseManager;
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2.database.FetchDatabaseManagerImpl;
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2.database.DownloadDatabase;
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2.database.migration.Migration;
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2.downloader.DownloadManager;
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2.downloader.DownloadManagerImpl;
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2.downloader.DownloadManagerCoordinator;
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2.fetch.LiveSettings;
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2.provider.GroupInfoProvider;
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2core.DefaultStorageResolver;
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2core.Downloader;
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2core.FetchCoreDefaults;
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2core.FetchCoreUtils;
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2core.FetchLogger;
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2core.FileServerDownloader;
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2core.HandlerWrapper;
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2.fetch.ListenerCoordinator;
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2.helper.DownloadInfoUpdater;
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2.helper.PriorityListProcessor;
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2.helper.PriorityListProcessorImpl;
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2.provider.DownloadProvider;
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2.provider.NetworkInfoProvider;
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2.util.FetchDefaults;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DownloadPriorityIteratorProcessorTest {

    private PriorityListProcessor<Download> priorityListProcessorImpl;

    @Before
    public void useAppContext() throws Exception {
        final Context appContext = InstrumentationRegistry.getTargetContext();
        final HandlerThread handlerThread = new HandlerThread("test");
        handlerThread.start();
        final String namespace = "fetch2DatabaseTest";
        final FetchLogger fetchLogger = new FetchLogger(true, namespace);
        final Migration[] migrations = DownloadDatabase.getMigrations();
        final LiveSettings liveSettings = new LiveSettings(namespace);
        DefaultStorageResolver defaultStorageResolver = new DefaultStorageResolver(appContext, FetchCoreUtils.getFileTempDir(appContext));
        final FetchDatabaseManager fetchDatabaseManager = new FetchDatabaseManagerImpl(appContext, namespace, migrations, liveSettings,
                false, defaultStorageResolver);
        final Downloader client = FetchDefaults.getDefaultDownloader();
        final FileServerDownloader serverDownloader = FetchDefaults.getDefaultFileServerDownloader();
        final long progessInterval = FetchCoreDefaults.DEFAULT_PROGRESS_REPORTING_INTERVAL_IN_MILLISECONDS;
        final int concurrentLimit = FetchDefaults.DEFAULT_CONCURRENT_LIMIT;
        final NetworkInfoProvider networkInfoProvider = new NetworkInfoProvider(appContext);
        final boolean retryOnNetworkGain = false;
        final Handler uiHandler = new Handler(Looper.getMainLooper());
        final DownloadInfoUpdater downloadInfoUpdater = new DownloadInfoUpdater(fetchDatabaseManager);
        final String tempDir = FetchCoreUtils.getFileTempDir(appContext);
        final DownloadManagerCoordinator downloadManagerCoordinator = new DownloadManagerCoordinator(namespace);
        final DownloadProvider downloadProvider = new DownloadProvider(fetchDatabaseManager);
        final GroupInfoProvider groupInfoProvider = new GroupInfoProvider(namespace, downloadProvider);
        final ListenerCoordinator listenerCoordinator = new ListenerCoordinator(namespace, groupInfoProvider, downloadProvider, uiHandler);
        final DefaultStorageResolver storageResolver = new DefaultStorageResolver(appContext, tempDir);
        final DownloadManager downloadManager = new DownloadManagerImpl(client, concurrentLimit,
                progessInterval, fetchLogger, networkInfoProvider, retryOnNetworkGain,
                 downloadInfoUpdater, downloadManagerCoordinator,
                listenerCoordinator, serverDownloader, false, storageResolver, appContext, namespace, groupInfoProvider);
        priorityListProcessorImpl = new PriorityListProcessorImpl(
                new HandlerWrapper(namespace, null),
                new DownloadProvider(fetchDatabaseManager),
                downloadManager,
                new NetworkInfoProvider(appContext),
                fetchLogger,
                listenerCoordinator,
                concurrentLimit,
                appContext,
                namespace);
    }

    @Test
    public void pause() throws Exception {
        priorityListProcessorImpl.stop();
        priorityListProcessorImpl.start();
        priorityListProcessorImpl.pause();
        assertTrue(priorityListProcessorImpl.isPaused());
    }

    @Test
    public void resume() throws Exception {
        priorityListProcessorImpl.stop();
        priorityListProcessorImpl.start();
        priorityListProcessorImpl.pause();
        priorityListProcessorImpl.resume();
        assertFalse(priorityListProcessorImpl.isPaused());
    }

    @Test
    public void start() throws Exception {
        priorityListProcessorImpl.stop();
        priorityListProcessorImpl.start();
        assertFalse(priorityListProcessorImpl.isStopped());
    }

    @Test
    public void stop() throws Exception {
        priorityListProcessorImpl.stop();
        priorityListProcessorImpl.start();
        priorityListProcessorImpl.stop();
        assertTrue(priorityListProcessorImpl.isStopped());
    }

    @After
    public void cleanup() throws Exception {
        priorityListProcessorImpl.stop();
    }

}
