package com.choreocam.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import com.choreocam.app.database.AppDatabase;
import com.choreocam.app.utils.ConfigManager;
import com.choreocam.app.utils.CrashReportingTree;
import com.choreocam.app.workers.SyncWorker;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import timber.log.Timber;
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

        // Initialize logging first
        initializeLogging();

        Timber.i("ChoreoCam Application starting...");

        try {
            // Initialize configuration manager
            configManager = new ConfigManager(this);
            Timber.d("ConfigManager initialized successfully");

            // Initialize Room database
            database = AppDatabase.getInstance(this);
            Timber.d("Database initialized successfully");

            // Initialize AdMob
            initializeAdMob();

            // Schedule periodic sync work
            scheduleSyncWork();

            Timber.i("ChoreoCam Application started successfully");
        } catch (Exception e) {
            Timber.e(e, "Error during application initialization");
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    private void initializeLogging() {
        if (BuildConfig.DEBUG) {
            // Debug mode: Log everything to console
            Timber.plant(new Timber.DebugTree());
            Timber.d("Debug logging enabled");
        } else {
            // Release mode: Log to Crashlytics
            Timber.plant(new CrashReportingTree());
            Timber.d("Production logging enabled with Crashlytics");
        }

        // Configure Crashlytics
        FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
        crashlytics.setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG);
        Timber.d("Crashlytics initialized");
    }

    private void initializeAdMob() {
        try {
            if (configManager.isAdsEnabled()) {
                Timber.d("Initializing AdMob...");
                MobileAds.initialize(this, initializationStatus -> {
                    Timber.i("AdMob initialization complete");
                });

                // Set test device IDs if in test mode
                if (configManager.isAdTestMode()) {
                    Timber.d("AdMob test mode enabled");
                    RequestConfiguration configuration = new RequestConfiguration.Builder()
                        .setTestDeviceIds(Arrays.asList("ABCDEF012345"))
                        .build();
                    MobileAds.setRequestConfiguration(configuration);
                }
            } else {
                Timber.d("AdMob is disabled in configuration");
            }
        } catch (Exception e) {
            Timber.e(e, "Error initializing AdMob");
        }
    }

    private void scheduleSyncWork() {
        try {
            if (configManager.isAutoSyncEnabled()) {
                int syncInterval = configManager.getSyncIntervalMinutes();
                Timber.d("Scheduling sync work with interval: %d minutes", syncInterval);

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

                Timber.i("Sync work scheduled successfully");
            } else {
                Timber.d("Auto sync is disabled in configuration");
            }
        } catch (Exception e) {
            Timber.e(e, "Error scheduling sync work");
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
