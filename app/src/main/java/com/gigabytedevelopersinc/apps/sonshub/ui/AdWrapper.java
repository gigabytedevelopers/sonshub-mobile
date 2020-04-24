package com.gigabytedevelopersinc.apps.sonshub.ui;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics;
import com.gigabytedevelopersinc.apps.sonshub.BuildConfig;
import com.gigabytedevelopersinc.apps.sonshub.R;
import com.mobfox.android.MobfoxSDK;
import com.startapp.sdk.ads.banner.Banner;
import com.startapp.sdk.adsbase.StartAppAd;

import timber.log.Timber;

import static com.gigabytedevelopersinc.apps.sonshub.activities.MainActivity.isTelevision;

/**
 * @author Created by Emmanuel Nwokoma (Founder and CEO at Gigabyte Developers) on 12/15/2018
 **/
public class AdWrapper extends FrameLayout {

    private StartAppAd startAppAd = new StartAppAd(getContext());

    public AdWrapper(Context context) {
        super(context);
        init(context);
    }

    public AdWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AdWrapper(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        //Ads
        if (!isTelevision()){
            LayoutInflater.from(context).inflate(R.layout.ads_wrapper, this, true);
            initAd();
        } else {
            loadAd();
        }
    }

    public void initAd() {
        Banner mAdView = findViewById(R.id.startAppBanner);
        mAdView.setVisibility(View.VISIBLE);
        mAdView.loadAd();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        showAd();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        showAd();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    private void showAds(){

    }

    public void loadAd() {
        startAppAd.loadAd(StartAppAd.AdMode.AUTOMATIC);
    }

    public void showAd() {
        Banner mAdView = findViewById(R.id.startAppBanner);
        if (isInEditMode()) {
            return;
        }
        //Fixes GPS AIOB Exception
        try {
            if (null != mAdView) {
                loadAd();
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }
}
