package com.choreocam.app;

import android.app.Application;
import android.content.Context;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import com.choreocam.app.database.AppDatabase;
import com.choreocam.app.utils.ConfigManager;
import com.choreocam.app.workers.SyncWorker;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class ChoreoCamApplication extends Application {

    private static Context context;
    private static AppDatabase database;
    private static ConfigManager configManager;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        // Initialize configuration manager
        configManager = new ConfigManager(this);

        // Initialize Room database
        database = AppDatabase.getInstance(this);

        // Initialize AdMob
        initializeAdMob();

        // Schedule periodic sync work
        scheduleSyncWork();
    }

    private void initializeAdMob() {
        if (configManager.isAdsEnabled()) {
            MobileAds.initialize(this, initializationStatus -> {
                // AdMob initialization complete
            });

            // Set test device IDs if in test mode
            if (configManager.isAdTestMode()) {
                RequestConfiguration configuration = new RequestConfiguration.Builder()
                    .setTestDeviceIds(Arrays.asList("ABCDEF012345"))
                    .build();
                MobileAds.setRequestConfiguration(configuration);
            }
        }
    }

    private void scheduleSyncWork() {
        if (configManager.isAutoSyncEnabled()) {
            int syncInterval = configManager.getSyncIntervalMinutes();

            PeriodicWorkRequest syncWorkRequest = new PeriodicWorkRequest.Builder(
                SyncWorker.class,
                syncInterval,
                TimeUnit.MINUTES
            ).build();

            WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "sync_work",
                ExistingPeriodicWorkPolicy.KEEP,
                syncWorkRequest
            );
        }
    }

    public static Context getAppContext() {
        return context;
    }

    public static AppDatabase getDatabase() {
        return database;
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }
}
