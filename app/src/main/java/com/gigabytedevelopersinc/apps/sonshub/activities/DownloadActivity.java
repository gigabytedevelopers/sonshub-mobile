package com.gigabytedevelopersinc.apps.sonshub.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.gigabytedevelopersinc.apps.sonshub.R;
import com.gigabytedevelopersinc.apps.sonshub.fragments.downloads.DownloadedFragment;
import com.gigabytedevelopersinc.apps.sonshub.fragments.downloads.DownloadingFragment;
import com.gigabytedevelopersinc.apps.sonshub.ui.AdWrapper;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.Objects;

public class DownloadActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        SmartTabLayout smartTabLayout = findViewById(R.id.smart_tab);
        ImageView backBtn = findViewById(R.id.back);

        backBtn.setOnClickListener(view -> onBackPressed());
        ViewPager viewPager = findViewById(R.id.music_view_pager);

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(),
                FragmentPagerItems.with(this)
                .add("Downloading", DownloadingFragment.class)
                .add("Downloaded", DownloadedFragment.class)
                .create()
        );
        viewPager.setAdapter(adapter);
        smartTabLayout.setViewPager(viewPager);
    }
}