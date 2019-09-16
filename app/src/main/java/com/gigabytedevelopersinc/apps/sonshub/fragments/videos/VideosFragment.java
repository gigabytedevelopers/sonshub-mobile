package com.gigabytedevelopersinc.apps.sonshub.fragments.videos;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.gigabytedevelopersinc.apps.sonshub.R;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideosFragment extends Fragment {
    private SmartTabLayout smartTabLayout;
    private ViewPager viewPager;


    public VideosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_videos, container, false);
        smartTabLayout = view.findViewById(R.id.videos_smart_tab);
        smartTabLayout.setDistributeEvenly(true);
        viewPager = view.findViewById(R.id.videos_view_pager);
        FragmentPagerItemAdapter fragmentPagerItemAdapter = new FragmentPagerItemAdapter(getChildFragmentManager(), FragmentPagerItems.with(getContext())
                .add(R.string.nav_video_movies, MoviesFragment.class)
                .add(R.string.nav_video_music_videos, MusicVideosFragment.class)
                .create());
        viewPager.setAdapter(fragmentPagerItemAdapter);
        smartTabLayout.setViewPager(viewPager);
        return view;
    }

}
