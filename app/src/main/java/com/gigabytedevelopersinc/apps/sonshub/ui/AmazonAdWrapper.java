package com.gigabytedevelopersinc.apps.sonshub.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.amazon.device.ads.Ad;
import com.amazon.device.ads.AdError;
import com.amazon.device.ads.AdLayout;
import com.amazon.device.ads.AdProperties;
import com.amazon.device.ads.AdRegistration;
import com.amazon.device.ads.DefaultAdListener;
import com.amazon.device.ads.InterstitialAd;
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
public class AmazonAdWrapper extends FrameLayout {

    @SuppressLint("StaticFieldLeak")
    private static AdLayout mAdView;
    private static InterstitialAd mInterstitialAd;
    private boolean showInterstitial = true;

    public AmazonAdWrapper(Context context) {
        super(context);
        init(context);
    }

    public AmazonAdWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AmazonAdWrapper(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        //Ads
        if (!isTelevision()) {
            LayoutInflater.from(context).inflate(R.layout.ads_wrapper, this, true);
            initBannerAd();
        } else {
            mInterstitialAd = new InterstitialAd(context);
            initInterstitialAd();
        }
    }

    private void initBannerAd() {
        if (!BuildConfig.DEBUG) {
            AdRegistration.enableLogging(false);
            AdRegistration.enableTesting(false);
        } else {
            AdRegistration.enableLogging(true);
            AdRegistration.enableTesting(true);
        }
        mAdView = findViewById(R.id.ad_view);
        mAdView.setListener(new BannerAdsListener());
        loadBannerAds();
    }

    private static void initInterstitialAd() {
        if (!BuildConfig.DEBUG) {
            AdRegistration.enableLogging(false);
            AdRegistration.enableTesting(false);
        } else {
            AdRegistration.enableLogging(true);
            AdRegistration.enableTesting(true);
        }
        mInterstitialAd.setListener(new InterstitialAdsListener());
    }

    private class BannerAdsListener extends DefaultAdListener {
        /**
         * This event is called once an ad loads successfully.
         */
        @Override
        public void onAdLoaded(final Ad ad, final AdProperties adProperties) {
            Timber.i("%s ad loaded successfully.", adProperties.getAdType().toString());
            showBannerAd();
        }

        /**
         * This event is called if an ad fails to load.
         */
        @Override
        public void onAdFailedToLoad(final Ad ad, final AdError error) {
            Timber.w("Ad failed to load. Code: " + error.getCode() + ", Message: " + error.getMessage());
            loadBannerAds();
        }

        /**
         * This event is called after a rich media ad expands.
         */
        @Override
        public void onAdExpanded(final Ad ad) {
            Timber.i("Ad expanded.");
            // You may want to pause your activity here.
        }

        /**
         * This event is called after a rich media ad has collapsed from an expanded state.
         */
        @Override
        public void onAdCollapsed(final Ad ad) {
            Timber.i("Ad collapsed.");
            // Resume your activity here, if it was paused in onAdExpanded.
        }
    }

    private static class InterstitialAdsListener extends DefaultAdListener {
        /**
         * This event is called once an ad loads successfully.
         */
        @Override
        public void onAdLoaded(final Ad ad, final AdProperties adProperties) {
            Timber.i("%s Interstitial ad loaded successfully.", adProperties.getAdType().toString());
        }

        /**
         * This event is called if an ad fails to load.
         */
        @Override
        public void onAdFailedToLoad(final Ad ad, final AdError error) {
            Timber.w("Interstitial Ad failed to load. Code: " + error.getCode() + ", Message: " + error.getMessage());
            loadInterstitialAds();
        }

        /**
         * This event is called when an interstitial ad has been dismissed by the user.
         */
        @Override
        public void onAdDismissed(final Ad ad) {
            Timber.i("Interstitial Ad has been dismissed by the user.");

            // Once the shown ad is dismissed, its lifecycle is complete and a new ad can be loaded.
            loadInterstitialAds();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        loadBannerAds();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        showInterstitial();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        showInterstitial = false;
        return super.onSaveInstanceState();
    }

    private void loadBannerAds() {
        if (isInEditMode()) {
            return;
        }
        //Fixes GPS AIOB Exception
        try {
            if (null != mAdView) {
                mAdView.loadAd();
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    private static void showBannerAd() {
        mAdView.showAd();
    }

    private static void loadInterstitialAds() {
        mInterstitialAd.loadAd();
    }

    private void showInterstitial() {
        if (showInterstitial && null != mInterstitialAd) {
            if (mInterstitialAd.isReady()) {
                mInterstitialAd.showAd();
                if (!mInterstitialAd.showAd()) {
                    Timber.w("The ad was not shown. Check the logcat for more information.");
                    loadInterstitialAds();
                }
            } else {
                loadInterstitialAds();
            }
        }
    }
}
