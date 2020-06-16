package com.gigabytedevelopersinc.apps.sonshub.fragments.gist;


import android.os.Bundle;
import androidx.annotation.NonNull;
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
public class GistFragment extends Fragment {

    public GistFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_gist, container, false);
        SmartTabLayout smartTabLayout = view.findViewById(R.id.gist_smart_tab);
        smartTabLayout.setDistributeEvenly(true);
        ViewPager viewPager = view.findViewById(R.id.gist_view_pager);
        FragmentPagerItemAdapter fragmentPagerItemAdapter = new FragmentPagerItemAdapter(getChildFragmentManager(), FragmentPagerItems.with(getContext())
                .add(R.string.nav_gist_article, ArticleFragment.class)
                .add(R.string.nav_gist_event, EventsFragment.class)
                .add(R.string.nav_gist_news, NewsFragment.class)
                .add(R.string.fiction_frag, Fiction.class)
                .add(R.string.photogal_frag, PhotoGallery.class)
                .create());
        viewPager.setAdapter(fragmentPagerItemAdapter);
        smartTabLayout.setViewPager(viewPager);

        return view;
    }

}
