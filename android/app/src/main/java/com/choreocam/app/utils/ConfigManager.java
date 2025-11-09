package com.choreocam.app.utils;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ConfigManager {

    private JsonObject config;
    private Context context;

    public ConfigManager(Context context) {
        this.context = context;
        loadConfig();
    }

    private void loadConfig() {
        try {
            // Try to load from external storage first (for easy configuration)
            File externalConfigFile = new File(context.getExternalFilesDir(null), "app.config.json");

            String jsonString;
            if (externalConfigFile.exists()) {
                jsonString = readFromFile(externalConfigFile);
            } else {
                // Fallback to assets
                jsonString = readFromAssets("app.config.json");
            }

            Gson gson = new Gson();
            config = gson.fromJson(jsonString, JsonObject.class);
        } catch (Exception e) {
            e.printStackTrace();
            // Initialize with default empty config
            config = new JsonObject();
        }
    }

    private String readFromFile(File file) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }
        return stringBuilder.toString();
    }

    private String readFromAssets(String fileName) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream = context.getAssets().open(fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }
        return stringBuilder.toString();
    }

    // Ads Configuration
    public boolean isAdsEnabled() {
        return getBoolean("ads", "enabled", true);
    }

    public String getAdMobAppId() {
        return getString("ads", "admob_app_id", "");
    }

    public String getBannerAdUnitId() {
        return getString("ads", "banner_ad_unit_id", "");
    }

    public String getInterstitialAdUnitId() {
        return getString("ads", "interstitial_ad_unit_id", "");
    }

    public int getInterstitialFrequencyMinutes() {
        return getInt("ads", "interstitial_frequency_minutes", 5);
    }

    public boolean showBannerOnAllScreens() {
        return getBoolean("ads", "show_banner_on_all_screens", true);
    }

    public boolean isAdTestMode() {
        return getBoolean("ads", "test_mode", true);
    }

    // Backend Configuration
    public String getBackendBaseUrl() {
        return getString("backend", "base_url", "https://api.choreocam.com");
    }

    public String getApiVersion() {
        return getString("backend", "api_version", "v1");
    }

    public int getTimeoutSeconds() {
        return getInt("backend", "timeout_seconds", 30);
    }

    public boolean shouldFallbackToLocal() {
        return getBoolean("backend", "fallback_to_local", true);
    }

    // Sync Configuration
    public boolean isAutoSyncEnabled() {
        return getBoolean("sync", "auto_sync_enabled", true);
    }

    public int getSyncIntervalMinutes() {
        return getInt("sync", "sync_interval_minutes", 15);
    }

    public int getRetryAttempts() {
        return getInt("sync", "retry_attempts", 3);
    }

    public int getRetryDelaySeconds() {
        return getInt("sync", "retry_delay_seconds", 5);
    }

    // IAP Configuration
    public boolean isIAPEnabled() {
        return getBoolean("iap", "enabled", true);
    }

    public String getProMonthlySku() {
        return getString("iap", "pro_monthly_sku", "choreocam_pro_monthly");
    }

    public String getProYearlySku() {
        return getString("iap", "pro_yearly_sku", "choreocam_pro_yearly");
    }

    public String getPremiumMusicPackSku() {
        return getString("iap", "premium_music_pack_sku", "choreocam_premium_music");
    }

    // Helper methods
    private String getString(String section, String key, String defaultValue) {
        try {
            if (config.has(section)) {
                JsonObject sectionObj = config.getAsJsonObject(section);
                if (sectionObj.has(key)) {
                    return sectionObj.get(key).getAsString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    private int getInt(String section, String key, int defaultValue) {
        try {
            if (config.has(section)) {
                JsonObject sectionObj = config.getAsJsonObject(section);
                if (sectionObj.has(key)) {
                    return sectionObj.get(key).getAsInt();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    private boolean getBoolean(String section, String key, boolean defaultValue) {
        try {
            if (config.has(section)) {
                JsonObject sectionObj = config.getAsJsonObject(section);
                if (sectionObj.has(key)) {
                    return sectionObj.get(key).getAsBoolean();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }
}
