package com.gigabytedevelopersinc.apps.sonshub.fragments.downloads;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.gigabytedevelopersinc.apps.sonshub.ui.LockableViewPager;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import com.gigabytedevelopersinc.apps.sonshub.R;

import org.jetbrains.annotations.NotNull;

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
 * Desc: DownloadedFragment
 **/
public class DownloadedFragment extends Fragment {
    public DownloadedFragment() {
        // Required empty public constructor
        super();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_downloaded, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup downloaded Audio RecyclerView and list downloaded Audio files
        SmartTabLayout smartTabLayout = view.findViewById(R.id.smart_tab);
        LockableViewPager viewPager = view.findViewById(R.id.customViewPager);

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(getChildFragmentManager(),
                FragmentPagerItems.with(getContext())
                        .add("Audio", AudioDownloadedFragment.class)
                        .add("Video", VideoDownloadedFragment.class)
                        .create()
        );
        viewPager.setSwipeLocked(true);
        viewPager.setAdapter(adapter);
        smartTabLayout.setViewPager(viewPager);
    }
}