package com.choreocam.app.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.ViewGroup;
import com.choreocam.app.ChoreoCamApplication;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import androidx.annotation.NonNull;

public class AdManager {

    private static final String PREFS_NAME = "ad_prefs";
    private static final String LAST_INTERSTITIAL_TIME = "last_interstitial_time";

    private final Activity activity;
    private final ConfigManager config;
    private AdView bannerAdView;
    private InterstitialAd interstitialAd;
    private boolean isLoadingInterstitial = false;

    public AdManager(Activity activity) {
        this.activity = activity;
        this.config = ChoreoCamApplication.getConfigManager();
        loadInterstitialAd();
    }

    public void loadBannerAd(ViewGroup adContainer) {
        if (!config.isAdsEnabled() || adContainer == null) {
            return;
        }

        bannerAdView = new AdView(activity);
        bannerAdView.setAdSize(AdSize.BANNER);
        bannerAdView.setAdUnitId(config.getBannerAdUnitId());

        adContainer.removeAllViews();
        adContainer.addView(bannerAdView);

        AdRequest adRequest = new AdRequest.Builder().build();
        bannerAdView.loadAd(adRequest);
    }

    private void loadInterstitialAd() {
        if (!config.isAdsEnabled() || isLoadingInterstitial) {
            return;
        }

        isLoadingInterstitial = true;

        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(activity, config.getInterstitialAdUnitId(), adRequest,
            new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd ad) {
                    interstitialAd = ad;
                    isLoadingInterstitial = false;
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    interstitialAd = null;
                    isLoadingInterstitial = false;
                }
            });
    }

    public void showInterstitialAd() {
        if (!config.isAdsEnabled()) {
            return;
        }

        if (!shouldShowInterstitial()) {
            return;
        }

        if (interstitialAd != null) {
            interstitialAd.show(activity);

            // Update last shown time
            SharedPreferences prefs = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            prefs.edit().putLong(LAST_INTERSTITIAL_TIME, System.currentTimeMillis()).apply();

            // Reload for next time
            interstitialAd = null;
            loadInterstitialAd();
        }
    }

    private boolean shouldShowInterstitial() {
        SharedPreferences prefs = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        long lastTime = prefs.getLong(LAST_INTERSTITIAL_TIME, 0);
        long currentTime = System.currentTimeMillis();
        long minutesPassed = (currentTime - lastTime) / 1000 / 60;

        return minutesPassed >= config.getInterstitialFrequencyMinutes();
    }

    public void pauseAd() {
        if (bannerAdView != null) {
            bannerAdView.pause();
        }
    }

    public void resumeAd() {
        if (bannerAdView != null) {
            bannerAdView.resume();
        }
    }

    public void destroyAd() {
        if (bannerAdView != null) {
            bannerAdView.destroy();
        }
    }
}
