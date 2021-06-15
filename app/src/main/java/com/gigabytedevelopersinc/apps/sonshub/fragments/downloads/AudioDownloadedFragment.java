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

import com.gigabytedevelopersinc.apps.sonshub.BuildConfig;
import com.gigabytedevelopersinc.apps.sonshub.R;
import com.gigabytedevelopersinc.apps.sonshub.activities.MainActivity;
import com.gigabytedevelopersinc.apps.sonshub.adapters.DownloadedAdapter;
import com.gigabytedevelopersinc.apps.sonshub.models.DownloadedModel;
import com.gigabytedevelopersinc.apps.sonshub.utils.DownloadUtils;
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
 * Desc: AudioDownloadedFragment
 **/
public class AudioDownloadedFragment extends Fragment {

    public TextView noDownloadedSongs;
    private RecyclerView recyclerViewAudio;

    public AudioDownloadedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_downloaded_audio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerViewAudio = view.findViewById(R.id.recyclerViewDownloadedAudio);
        noDownloadedSongs = view.findViewById(R.id.noDownload_songs);

        ArrayList<File> totalAudio = getAudioListFiles();
        setAudioListAdapter(parseAudioToFileName(totalAudio));
    }

    // For Downloaded Audio Files
    private ArrayList<DownloadedModel> parseAudioToFileName(ArrayList<File> audioFiles) {
        ArrayList<DownloadedModel> fileNames = new ArrayList<>();
        for (File f : audioFiles) {
            String[] name = f.getAbsolutePath().split("/");
            if (name[(name.length - 1)].endsWith(".mp3"))
                fileNames.add(new DownloadedModel(name[(name.length - 1)]));
        }
        return fileNames;
    }
    private void setAudioListAdapter(List<DownloadedModel> data) {
        DownloadedAdapter adapter = new DownloadedAdapter(getActivity(), data, (view, position, title) -> {
            File fileFolder = MainActivity.commonDocumentDirPath("SonsHub" + "/Music");
            File audioUrl = new File(
                    fileFolder.getPath() + "/" + title
            );
            Uri uri1 = Uri.fromFile(audioUrl);
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri1, DownloadUtils.getMimeType(requireContext(), uri1));
                if (!BuildConfig.DEBUG) {
                    intent.setClassName(
                            "com.gigabytedevelopersinc.apps.sonshub",
                            "com.gigabytedevelopersinc.apps.sonshub.players.music.ui.activities.MusicMainActivity"
                    );
                } else {
                    intent.setClassName(
                            "com.gigabytedevelopersinc.apps.sonshub.dev",
                            "com.gigabytedevelopersinc.apps.sonshub.players.music.ui.activities.MusicMainActivity"
                    );
                }
                MainActivity.player.setPlayWhenReady(false);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.push_up_in, R.anim.hold);
//                startActivity(Intent.createChooser(intent, "Play this song with"));
            } catch (Exception e) {
                Toast.makeText(getContext(), "Sorry, our Music Player cannot open this file",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(audioUrl),"audio/mp3");
                startActivity(Intent.createChooser(intent, "Play this song with"));
                e.printStackTrace();
            }

            /*try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(audioUrl), "audio/mp3");

                Intent chooser = Intent.createChooser(intent, "Play this song with");
                if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
                    startActivity(chooser);
                } else {
                    startActivity(intent);
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), "No Application can open this file", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }*/
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        RecyclerViewEmptyObserver viewEmptyObserver = new RecyclerViewEmptyObserver(recyclerViewAudio, noDownloadedSongs);

        adapter.registerAdapterDataObserver(viewEmptyObserver);
        recyclerViewAudio.setAdapter(adapter);
        recyclerViewAudio.setLayoutManager(linearLayoutManager);
        adapter.notifyDataSetChanged();
    }
    private ArrayList<File> getAudioListFiles() {
        File filePath = MainActivity.commonDocumentDirPath("SonsHub" + "/Music");

        ArrayList<File> fileArrayList = new ArrayList<>();
        File[] listOfFiles = filePath.listFiles();

        if (filePath.exists()) {
            assert listOfFiles != null;
            for (File f : listOfFiles) {
                if (f.getAbsolutePath().endsWith(".mp3"))
                    fileArrayList.add(f);
            }
        } else {
            filePath.mkdir();
        }
        return fileArrayList;
    }
}