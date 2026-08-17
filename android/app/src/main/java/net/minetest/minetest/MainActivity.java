package net.minetest.minetest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
importcom.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class MainActivity extends UnusedActivity {
    private AdView adView;
    private InterstitialAd mInterstitialAd;
    private RewardedAd mRewardedAd;
    private boolean isAdFree = false;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobileAds.initialize(this, initializationStatus -> {});

        setupBanner();
        loadInterstitial();
        loadRewardedAd();
        startInterstitialTimer();
    }

    private void setupBanner() {
        adView = new AdView(this);
        adView.setAdUnitId("ca-app-pub-3940256099942544/9214589741");
        adView.setAdSize(AdSize.BANNER);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;

        addContentView(adView, params);
        adView.loadAd(new AdRequest.Builder().build());
    }

    private void loadInterstitial() {
        InterstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712",
            new AdRequest.Builder().build(),
            new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    mInterstitialAd = interstitialAd;
                }
            });
    }

    private void startInterstitialTimer() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isAdFree && mInterstitialAd != null) {
                    mInterstitialAd.show(MainActivity.this);
                    loadInterstitial();
                }
                handler.postDelayed(this, 10 * 60 * 1000);
            }
        }, 10 * 60 * 1000);
    }

    private void loadRewardedAd() {
        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917",
            new AdRequest.Builder().build(),
            new RewardedAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                    mRewardedAd = rewardedAd;
                }
            });
    }

    public void showRewardedAd() {
        if (mRewardedAd != null) {
            mRewardedAd.show(this, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    activateAdFreePeriod();
                }
            });
        }
    }

    private void activateAdFreePeriod() {
        isAdFree = true;
        if (adView != null) adView.setVisibility(View.GONE);

        handler.postDelayed(() -> {
            isAdFree = false;
            if (adView != null) adView.setVisibility(View.VISIBLE);
        }, 20 * 60 * 1000);
    }
}
