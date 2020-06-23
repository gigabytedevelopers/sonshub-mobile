package com.gigabytedevelopersinc.apps.sonshub.ui;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.amazon.device.ads.Ad;
import com.amazon.device.ads.AdError;
import com.amazon.device.ads.AdLayout;
import com.amazon.device.ads.AdProperties;
import com.amazon.device.ads.DefaultAdListener;
import com.amazon.device.ads.InterstitialAd;
import com.crashlytics.android.Crashlytics;
import com.gigabytedevelopersinc.apps.sonshub.App;
import com.gigabytedevelopersinc.apps.sonshub.R;
import com.startapp.sdk.ads.banner.Banner;
import com.startapp.sdk.ads.banner.BannerListener;
import com.startapp.sdk.adsbase.StartAppAd;

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

    private Context appContext = App.Companion.getContext();

    // Amazon
    private AdLayout mAdView;
    private InterstitialAd mInterstitialAd;
    private boolean showInterstitial = true;

    // StartApp
    private StartAppAd startAppAd;
    private Banner startAppBannerAd;
    private boolean showStartAppInterstitial = true;

    public AmazonAdWrapper(Context context) {
        super(context);
        init(context);
    }

    public AmazonAdWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        //initStartApp(context);
    }

    public AmazonAdWrapper(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
        //initStartApp(context);
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

    private void initStartApp(Context context) {
        mAdView.destroy();
        //Ads
        if (!isTelevision()) {
            LayoutInflater.from(context).inflate(R.layout.ads_wrapper_startapp, this, true);
            initStartAppBannerAd();
        } else {
            initStartAppInterstitialAd();
        }
    }

    private void initBannerAd() {
        mAdView = findViewById(R.id.ad_view);
        mAdView.setListener(new BannerAdsListener());
        loadBannerAds();
    }

    private void initStartAppBannerAd() {
        startAppBannerAd = findViewById(R.id.startAppBanner);
        startAppBannerAd.setBannerListener(new BannerListener() {
            @Override
            public void onReceiveAd(View view) {
                Timber.d("onReceiveAd");
            }

            @Override
            public void onFailedToReceiveAd(View view) {
                Timber.d("onFailedToReceiveAd: %s", startAppBannerAd.getErrorMessage());
                loadStartAppBannerAds();
            }

            @Override
            public void onImpression(View view) {
                Timber.d("onImpression");
            }

            @Override
            public void onClick(View view) {
                Timber.d("onClick");
            }
        });
        loadStartAppBannerAds();
    }

    private void initInterstitialAd() {
        mInterstitialAd.setListener(new InterstitialAdsListener());
        loadInterstitialAds();
    }

    private void initStartAppInterstitialAd() {
        startAppAd = new StartAppAd(appContext);
        loadStartAppInterstitialAds();
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
            if (error.getCode().toString().equals("NO_FILL")) {
                initStartApp(appContext);
            } else {
                loadBannerAds();
            }
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

    private class InterstitialAdsListener extends DefaultAdListener {
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
            if (error.getCode().toString().equals("NO_FILL")) {
                initStartAppInterstitialAd();
            } else {
                loadInterstitialAds();
            }
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
        showStartAppInterstitial = false;
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

    private void showBannerAd() {
        mAdView.showAd();
    }

    private void loadInterstitialAds() {
        mInterstitialAd.loadAd();
    }

    private void showInterstitial() {
        if (showInterstitial && null != mInterstitialAd) {
            if (mInterstitialAd.isReady()) {
                mInterstitialAd.showAd();
                if (!mInterstitialAd.showAd()) {
                    Timber.w("The ad was not shown. Check the logcat for more information.");
                    showStartAppInterstitial();
                    loadInterstitialAds();
                }
            } else {
                loadInterstitialAds();
            }
        }
    }

    // StartApp Ads loading and showing
    private void loadStartAppBannerAds() {
        if (isInEditMode()) {
            return;
        }
        //Fixes GPS AIOB Exception
        try {
            if (null != startAppBannerAd) {
                startAppBannerAd.loadAd();
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    private void loadStartAppInterstitialAds() {
        startAppAd.loadAd();
    }

    private void showStartAppInterstitial() {
        if (showStartAppInterstitial && null != startAppAd) {
            if (startAppAd.isReady()) {
                startAppAd.showAd();
                if (!startAppAd.showAd()) {
                    Timber.w("The ad was not shown. Check the logcat for more information.");
                    loadStartAppInterstitialAds();
                }
            }
        }
    }
}
