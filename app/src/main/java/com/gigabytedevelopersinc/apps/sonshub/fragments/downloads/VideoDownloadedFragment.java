package com.gigabytedevelopersinc.apps.sonshub.fragments.downloads;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.gigabytedevelopersinc.apps.sonshub.R;
import com.gigabytedevelopersinc.apps.sonshub.activities.MainActivity;
import com.gigabytedevelopersinc.apps.sonshub.adapters.DownloadedAdapter;
import com.gigabytedevelopersinc.apps.sonshub.models.DownloadedModel;
import com.gigabytedevelopersinc.apps.sonshub.utils.RecyclerViewEmptyObserver;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Project - SonsHub
 * Created with Android Studio
 * Company: Gigabyte Developers
 * User: Emmanuel Nwokoma
 * Title: Founder and CEO
 * Day: Wednesday, 03
 * Month: July
 * Year: 2019
 * Date: 03 Jul, 2019
 * Time: 12:33 PM
 * Desc: VideoDownloadedFragment
 **/
public class VideoDownloadedFragment extends Fragment {

    public TextView noDownloadedVideos;
    private RecyclerView recyclerViewVideo;

    public VideoDownloadedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_downloaded_video, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerViewVideo = view.findViewById(R.id.recyclerViewDownloadedVideo);
        noDownloadedVideos = view.findViewById(R.id.noDownload_videos);

        ArrayList<File> totalVideo = getVideoListFiles();
        setVideoListAdapter(parseVideoToFileName(totalVideo));
    }

    // For Downloaded Video Files
    private ArrayList<DownloadedModel> parseVideoToFileName(ArrayList<File> videoFiles) {
        ArrayList<DownloadedModel> fileNames = new ArrayList<>();
        for (File f : videoFiles) {
            String[] name = f.getAbsolutePath().split("/");
            if (name[(name.length - 1)].endsWith(".mp4"))
                fileNames.add(new DownloadedModel(name[(name.length - 1)]));
        }
        return fileNames;
    }
    private void setVideoListAdapter(List<DownloadedModel> data) {
        DownloadedAdapter adapter = new DownloadedAdapter(getActivity(), data, (view, position, title) -> {
            try {
                File fileFolder = MainActivity.commonDocumentDirPath("SonsHub" + "/Videos");
                File videoUrl = new File(
                        fileFolder.getPath() + "/" + title
                );
                /*File fileFolder = Environment.getExternalStorageDirectory();
                File videoUrl = new File(
                        fileFolder.getPath() + "/SonsHub/Videos/" + title
                );*/
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(videoUrl), "video/mp4");
                startActivity(Intent.createChooser(intent, "Play this Video with"));
            } catch (Exception e) {
                Toast.makeText(getContext(), "No Application can open this file",
                        Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        RecyclerViewEmptyObserver viewEmptyObserver = new RecyclerViewEmptyObserver(recyclerViewVideo, noDownloadedVideos);

        adapter.registerAdapterDataObserver(viewEmptyObserver);
        recyclerViewVideo.setAdapter(adapter);
        recyclerViewVideo.setLayoutManager(linearLayoutManager);
        adapter.notifyDataSetChanged();
    }
    private ArrayList<File> getVideoListFiles() {
        File filePath = MainActivity.commonDocumentDirPath("SonsHub" + "/Videos");

        ArrayList<File> fileArrayList = new ArrayList<>();
        File[] listOfFiles = filePath.listFiles();

        if (filePath.exists()) {
            assert listOfFiles != null;
            for (File f : listOfFiles) {
                if (f.getAbsolutePath().endsWith(".mp4"))
                    fileArrayList.add(f);
            }
        } else {
            filePath.mkdir();
        }
        return fileArrayList;
    }
}