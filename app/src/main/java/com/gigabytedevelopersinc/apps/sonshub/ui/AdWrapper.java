package com.gigabytedevelopersinc.apps.sonshub.ui;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import com.crashlytics.android.Crashlytics;
import com.gigabytedevelopersinc.apps.sonshub.BuildConfig;
import com.gigabytedevelopersinc.apps.sonshub.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import static com.gigabytedevelopersinc.apps.sonshub.activities.MainActivity.isTelevision;

/**
 * @author Created by Emmanuel Nwokoma (Founder and CEO at Gigabyte Developers) on 12/15/2018
 **/
public class AdWrapper extends FrameLayout {

    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private boolean showInterstiatial = true;

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
        if(!isTelevision() && !BuildConfig.DEBUG){
            LayoutInflater.from(context).inflate(R.layout.ads_wrapper, this, true);
            initAd();
        } else if (!isTelevision() && BuildConfig.DEBUG) {
            LayoutInflater.from(context).inflate(R.layout.ads_wrapper_test, this, true);
            initAd();
        } else {
            mInterstitialAd = new InterstitialAd(context);
            initInterstitialAd();
        }
    }

    public void initInterstitialAd(){
        if (BuildConfig.DEBUG) {
            mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_test_ads));
        } else {
            mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_ads));
        }
        //mInterstitialAd.setAdUnitId("ca-app-pub-6559138681672642/9998639521"); Interstitial
        requestNewInterstitial();
    }

    public void initAd(){
        if (BuildConfig.DEBUG) {
            // Show Test Banner Ads
            mAdView = findViewById(R.id.adViewTest);
            AdListener adListener = new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    mAdView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    super.onAdFailedToLoad(errorCode);
                    mAdView.setVisibility(View.GONE);
                    mAdView.loadAd(new AdRequest.Builder().build());
                }
            };
            mAdView.setAdListener(adListener);
        } else {
            // Show Banner Ads
            mAdView = findViewById(R.id.adView);
            AdListener adListener = new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    mAdView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    super.onAdFailedToLoad(errorCode);
                    mAdView.setVisibility(View.GONE);
                    mAdView.loadAd(new AdRequest.Builder().build());
                }
            };
            mAdView.setAdListener(adListener);
        }
    }

    private void requestNewInterstitial() {
        if (null != mInterstitialAd) {
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
        }
    }

    private void showInterstitial() {
        if (showInterstiatial && null != mInterstitialAd) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                requestNewInterstitial();
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        showInterstitial();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        showAd();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        showInterstiatial = false;
        return super.onSaveInstanceState();
    }

    private void showAd(){
        if(isInEditMode()){
            return;
        }
        //Fixes GPS AIOB Exception
        try {
            if (null != mAdView) {
                mAdView.loadAd(new AdRequest.Builder().build());
            }
        } catch (Exception e){
            Crashlytics.logException(e);
        }
    }
}
