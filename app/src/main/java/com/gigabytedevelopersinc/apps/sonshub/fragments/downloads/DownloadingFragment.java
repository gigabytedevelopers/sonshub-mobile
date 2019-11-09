package com.gigabytedevelopersinc.apps.sonshub.fragments.downloads;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.gigabytedevelopersinc.apps.sonshub.App;
import com.gigabytedevelopersinc.apps.sonshub.R;
import com.gigabytedevelopersinc.apps.sonshub.adapters.DownloadFileAdapter;
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2.*;
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2.Error;
import com.gigabytedevelopersinc.apps.sonshub.utils.ActionListener;
import com.gigabytedevelopersinc.apps.sonshub.utils.misc.Data;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.gigabytedevelopersinc.apps.sonshub.activities.MainActivity.fetch;

/**
 * Project - SonsHub
 * Created with Android Studio
 * Company: Gigabyte Developers
 * User: Emmanuel Nwokoma
 * Title: Founder and CEO
 * Day: Wednesday, 06
 * Month: March
 * Year: 2019
 * Date: 06 Mar, 2019
 * Time: 11:15 AM
 * Desc: DownloadingFragment
 **/
public class DownloadingFragment extends Fragment implements ActionListener {

    private static final long UNKNOWN_REMAINING_TIME = -1;
    private static final long UNKNOWN_DOWNLOADED_BYTES_PER_SECOND = 0;
    private static final int GROUP_ID = "listGroup".hashCode();
    private static final String FETCH_NAMESPACE = "Downloading";

    private static Context appContext = App.getContext();
    public static RecyclerView recyclerView;
    private DownloadFileAdapter fileAdapter;

    public DownloadingFragment() {
        // Required empty public constructor
        super();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_downloading, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.downloadingRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        fileAdapter = new DownloadFileAdapter(this);
        recyclerView.setAdapter(fileAdapter);

        final SwitchCompat networkSwitch = view.findViewById(R.id.networkSwitch);
        networkSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                fetch.setGlobalNetworkType(NetworkType.WIFI_ONLY);
            } else {
                fetch.setGlobalNetworkType(NetworkType.ALL);
            }
        });
    }

    public static void enqueueDownload() {
        final List<Request> requests = Data.getFetchRequestWithGroupId(GROUP_ID);
        fetch.enqueue(requests, updatedRequests -> {

        });
    }

    public void scanFile(Context ctxt, String x, String mimeType) {
        MediaScannerConnection.scanFile(ctxt, new String[] {x}, new String[] {mimeType}, null);
    }

    private final FetchListener fetchListener = new AbstractFetchListener() {
        @Override
        public void onAdded(@NotNull Download download) {
            fileAdapter.addDownload(download);
        }

        @Override
        public void onQueued(@NotNull Download download, boolean waitingOnNetwork) {
            fileAdapter.update(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
        }

        @Override
        public void onCompleted(@NotNull Download download) {
            File file = new File(download.getFile());
            String fileName = file.getName();
            if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
                String fileExtension = fileName.substring(fileName.lastIndexOf(".")+1);
                scanFile(appContext, download.getFile(), fileExtension);
            }
            fileAdapter.update(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
        }

        @Override
        public void onError(@NotNull Download download, @NotNull Error error, @Nullable Throwable throwable) {
            super.onError(download, error, throwable);
            fileAdapter.update(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
        }

        @Override
        public void onProgress(@NotNull Download download, long etaInMilliseconds, long downloadedBytesPerSecond) {
            fileAdapter.update(download, etaInMilliseconds, downloadedBytesPerSecond);
        }

        @Override
        public void onPaused(@NotNull Download download) {
            fileAdapter.update(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
        }

        @Override
        public void onResumed(@NotNull Download download) {
            fileAdapter.update(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
        }

        @Override
        public void onCancelled(@NotNull Download download) {
            fileAdapter.update(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
        }

        @Override
        public void onRemoved(@NotNull Download download) {
            fileAdapter.update(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
        }

        @Override
        public void onDeleted(@NotNull Download download) {
            fileAdapter.update(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
        }
    };

    @Override
    public void onPauseDownload(int id) {
        fetch.pause(id);
    }

    @Override
    public void onResumeDownload(int id) {
        fetch.resume(id);
    }

    @Override
    public void onRemoveDownload(int id) {
        fetch.remove(id);
    }

    @Override
    public void onRetryDownload(int id) {
        fetch.retry(id);
    }



    @Override
    public void onResume() {
        super.onResume();
        fetch.getDownloadsInGroup(GROUP_ID, downloads -> {
            final ArrayList<Download> list = new ArrayList<>(downloads);
            Collections.sort(list, (first, second) -> Long.compare(first.getCreated(), second.getCreated()));
            for (Download download : list) {
                fileAdapter.addDownload(download);
            }
        }).addListener(fetchListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        fetch.removeListener(fetchListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
