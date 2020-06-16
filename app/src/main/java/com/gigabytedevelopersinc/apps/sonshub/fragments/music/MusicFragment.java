package com.gigabytedevelopersinc.apps.sonshub.fragments.music;


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
public class MusicFragment extends Fragment {

    public MusicFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_music, container, false);
        SmartTabLayout smartTabLayout = view.findViewById(R.id.smart_tab);
        ViewPager viewPager = view.findViewById(R.id.music_view_pager);
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(getChildFragmentManager(), FragmentPagerItems.with(getContext())
                .add( R. string.nav_music_african, AfricanFragment.class)
                .add(R.string.nav_music_album, AlbumFragment.class)
                .add(R.string.nav_music_hiphop, HipHopFragment.class)
                .add(R.string.nav_music_mixtape, MixtapeFragment.class)
                .add(R.string.nav_music_western, WesternFragment.class)
                .create());
        viewPager.setAdapter(adapter);
        smartTabLayout.setViewPager(viewPager);
        return  view;

    }

}
