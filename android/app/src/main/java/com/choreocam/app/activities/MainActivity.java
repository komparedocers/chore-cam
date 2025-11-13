package com.choreocam.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.choreocam.app.ChoreoCamApplication;
import com.choreocam.app.R;
import com.choreocam.app.models.User;
import com.choreocam.app.utils.AdManager;
import com.choreocam.app.workers.NetworkUtils;
import com.google.android.material.button.MaterialButton;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private LinearLayout offlineIndicator;
    private MaterialButton createProjectBtn;
    private MaterialButton myProjectsBtn;
    private MaterialButton presetsBtn;
    private MaterialButton musicLibraryBtn;
    private MaterialButton upgradeBtn;
    private FrameLayout adContainer;
    private View proCard;

    private AdManager adManager;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("MainActivity onCreate()");

        try {
            setContentView(R.layout.activity_main);

            initializeViews();
            setupToolbar();
            setupAdManager();
            setupClickListeners();
            checkUserStatus();
            checkNetworkStatus();

            Timber.i("MainActivity initialized successfully");
        } catch (Exception e) {
            Timber.e(e, "Error in MainActivity onCreate()");
        }
    }

    private void initializeViews() {
        Timber.d("Initializing views");
        try {
            offlineIndicator = findViewById(R.id.offlineIndicator);
            createProjectBtn = findViewById(R.id.createProjectBtn);
            myProjectsBtn = findViewById(R.id.myProjectsBtn);
            presetsBtn = findViewById(R.id.presetsBtn);
            musicLibraryBtn = findViewById(R.id.musicLibraryBtn);
            upgradeBtn = findViewById(R.id.upgradeBtn);
            adContainer = findViewById(R.id.adContainer);
            proCard = findViewById(R.id.proCard);
        } catch (Exception e) {
            Timber.e(e, "Error initializing views");
        }
    }

    private void setupToolbar() {
        Timber.d("Setting up toolbar");
        try {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
        } catch (Exception e) {
            Timber.e(e, "Error setting up toolbar");
        }
    }

    private void setupAdManager() {
        Timber.d("Setting up ad manager");
        try {
            adManager = new AdManager(this);

            // Load banner ad if user is not Pro
            if (currentUser == null || !currentUser.isPro()) {
                Timber.d("Loading banner ad for free user");
                adManager.loadBannerAd(adContainer);
            } else {
                Timber.d("Skipping ads for Pro user");
            }
        } catch (Exception e) {
            Timber.e(e, "Error setting up ad manager");
        }
    }

    private void setupClickListeners() {
        Timber.d("Setting up click listeners");
        try {
            createProjectBtn.setOnClickListener(v -> {
                Timber.d("Create project button clicked");
                checkPermissionsAndStartEditor();
            });

            myProjectsBtn.setOnClickListener(v -> {
                Timber.d("My projects button clicked");
                try {
                    Intent intent = new Intent(MainActivity.this, ProjectListActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Timber.e(e, "Error starting ProjectListActivity");
                }
            });

            presetsBtn.setOnClickListener(v -> {
                Timber.d("Presets button clicked");
                try {
                    Intent intent = new Intent(MainActivity.this, PresetsActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Timber.e(e, "Error starting PresetsActivity");
                }
            });

            musicLibraryBtn.setOnClickListener(v -> {
                Timber.d("Music library button clicked");
                try {
                    Intent intent = new Intent(MainActivity.this, MusicLibraryActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Timber.e(e, "Error starting MusicLibraryActivity");
                }
            });

            upgradeBtn.setOnClickListener(v -> {
                Timber.d("Upgrade button clicked");
                try {
                    Intent intent = new Intent(MainActivity.this, ProUpgradeActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Timber.e(e, "Error starting ProUpgradeActivity");
                }
            });
        } catch (Exception e) {
            Timber.e(e, "Error setting up click listeners");
        }
    }

    private void checkPermissionsAndStartEditor() {
        Timber.d("Checking permissions");
        try {
            // Check media permissions
            PermissionHelper permissionHelper = new PermissionHelper(this);
            if (permissionHelper.hasMediaPermissions()) {
                Timber.d("Permissions granted, starting editor");
                startProjectEditor();
            } else {
                Timber.d("Requesting media permissions");
                permissionHelper.requestMediaPermissions();
            }
        } catch (Exception e) {
            Timber.e(e, "Error checking permissions");
        }
    }

    private void startProjectEditor() {
        Timber.d("Starting project editor");
        try {
            Intent intent = new Intent(MainActivity.this, ProjectEditorActivity.class);
            startActivity(intent);

            // Show interstitial ad if appropriate
            if (currentUser == null || !currentUser.isPro()) {
                Timber.d("Showing interstitial ad");
                adManager.showInterstitialAd();
            }
        } catch (Exception e) {
            Timber.e(e, "Error starting project editor");
        }
    }

    private void checkUserStatus() {
        Timber.d("Checking user status");
        new Thread(() -> {
            try {
                currentUser = ChoreoCamApplication.getDatabase().userDao().getCurrentUser();
                if (currentUser != null) {
                    Timber.d("User found: %s, Pro: %b", currentUser.getEmail(), currentUser.isPro());
                } else {
                    Timber.d("No current user found");
                }

                runOnUiThread(() -> {
                    if (currentUser != null && currentUser.isPro()) {
                        Timber.d("Hiding ads for Pro user");
                        // Hide pro card and ads for Pro users
                        proCard.setVisibility(View.GONE);
                        adContainer.setVisibility(View.GONE);
                    }
                });
            } catch (Exception e) {
                Timber.e(e, "Error checking user status");
            }
        }).start();
    }

    private void checkNetworkStatus() {
        try {
            boolean isOnline = NetworkUtils.isNetworkAvailable(this);
            Timber.d("Network status: %s", isOnline ? "online" : "offline");
            offlineIndicator.setVisibility(isOnline ? View.GONE : View.VISIBLE);
        } catch (Exception e) {
            Timber.e(e, "Error checking network status");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Timber.d("Menu item selected: %d", id);

        try {
            if (id == R.id.action_settings) {
                Timber.d("Opening settings");
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.action_account) {
                Timber.d("Opening account screen");
                Intent intent = new Intent(this, AuthActivity.class);
                startActivity(intent);
                return true;
            }
        } catch (Exception e) {
            Timber.e(e, "Error handling menu item selection");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Timber.d("MainActivity onResume()");
        try {
            adManager.resumeAd();
            checkNetworkStatus();
            checkUserStatus();
        } catch (Exception e) {
            Timber.e(e, "Error in onResume()");
        }
    }

    @Override
    protected void onPause() {
        Timber.d("MainActivity onPause()");
        try {
            adManager.pauseAd();
        } catch (Exception e) {
            Timber.e(e, "Error in onPause()");
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Timber.d("MainActivity onDestroy()");
        try {
            adManager.destroyAd();
        } catch (Exception e) {
            Timber.e(e, "Error in onDestroy()");
        }
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Timber.d("Permission result received: requestCode=%d", requestCode);
        try {
            PermissionHelper permissionHelper = new PermissionHelper(this);
            if (permissionHelper.hasMediaPermissions()) {
                Timber.i("Permissions granted");
                startProjectEditor();
            } else {
                Timber.w("Permissions denied");
            }
        } catch (Exception e) {
            Timber.e(e, "Error handling permission result");
        }
    }
}
