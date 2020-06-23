package com.gigabytedevelopersinc.apps.sonshub.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.chartboost.sdk.CBLocation;
import com.chartboost.sdk.Chartboost;
import com.chartboost.sdk.ChartboostBanner;
import com.chartboost.sdk.ChartboostBannerListener;
import com.chartboost.sdk.ChartboostDelegate;
import com.chartboost.sdk.Events.ChartboostCacheError;
import com.chartboost.sdk.Events.ChartboostCacheEvent;
import com.chartboost.sdk.Events.ChartboostClickError;
import com.chartboost.sdk.Events.ChartboostClickEvent;
import com.chartboost.sdk.Events.ChartboostShowError;
import com.chartboost.sdk.Events.ChartboostShowEvent;
import com.chartboost.sdk.Model.CBError;
import com.crashlytics.android.Crashlytics;
import com.gigabytedevelopersinc.apps.sonshub.BuildConfig;
import com.gigabytedevelopersinc.apps.sonshub.R;

import timber.log.Timber;

import static com.gigabytedevelopersinc.apps.sonshub.activities.MainActivity.isTelevision;

/**
 * Project - SonsHub
 * Created with Android Studio
 * Company: Gigabyte Developers
 * User: Emmanuel Nwokoma
 * Title: Founder and CEO
 * Day: Saturday, 13
 * Month: June
 * Year: 2020
 * Date: 13 Jun, 2020
 * Time: 7:57 AM
 * Desc: AmazonAmazonAdWrapper
 **/
public class ChartboostAdWrapper extends FrameLayout {

    @SuppressLint("StaticFieldLeak")
    private static ChartboostBanner chartboostBanner;
    private boolean showInterstitial = true;

    public ChartboostAdWrapper(Context context) {
        super(context);
        init(context);
    }

    public ChartboostAdWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ChartboostAdWrapper(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        //Ads
        if (!isTelevision()) {
            LayoutInflater.from(context).inflate(R.layout.ads_wrapper, this, true);
            initBannerAd();
        } else {
            //initInterstitialAd();
        }
    }

    private void initBannerAd() {
        chartboostBanner = findViewById(R.id.ad_view);
        chartboostBanner.setListener(new BannerAdsListener());
        cacheBanner();
    }

    private class BannerAdsListener implements ChartboostBannerListener {
        @Override
        public void onAdCached(ChartboostCacheEvent chartboostCacheEvent, ChartboostCacheError chartboostCacheError) {
            if (chartboostCacheError != null) {
                Timber.d("Banner cached error: %s", chartboostCacheError.code);
                cacheBanner();
            } else {
                Timber.d("Banner cached");
                showBannerAd();
            }
        }

        @Override
        public void onAdShown(ChartboostShowEvent chartboostShowEvent, ChartboostShowError chartboostShowError) {
            if (chartboostShowError != null) {
                Timber.d("Banner shown error: %s", chartboostShowError.code);
            } else {
                Timber.d("Banner shown for location: %s", chartboostShowEvent.location);
                cacheBanner();
            }
        }

        @Override
        public void onAdClicked(ChartboostClickEvent chartboostClickEvent, ChartboostClickError chartboostClickError) {

        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //showInterstitial();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        cacheBanner();
    }

    /*@Override
    protected Parcelable onSaveInstanceState() {
        showInterstitial = false;
        return super.onSaveInstanceState();
    }*/

    private void showBannerAd() {
        chartboostBanner.show();
    }

    private void cacheBanner(){
        if (isInEditMode()) {
            return;
        }
        //Fixes GPS AIOB Exception
        try {
            if (chartboostBanner != null) {
                chartboostBanner.cache();
            }
        } catch (Exception e){
            Crashlytics.logException(e);
        }
    }
}
