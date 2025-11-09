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
        setContentView(R.layout.activity_main);

        initializeViews();
        setupToolbar();
        setupAdManager();
        setupClickListeners();
        checkUserStatus();
        checkNetworkStatus();
    }

    private void initializeViews() {
        offlineIndicator = findViewById(R.id.offlineIndicator);
        createProjectBtn = findViewById(R.id.createProjectBtn);
        myProjectsBtn = findViewById(R.id.myProjectsBtn);
        presetsBtn = findViewById(R.id.presetsBtn);
        musicLibraryBtn = findViewById(R.id.musicLibraryBtn);
        upgradeBtn = findViewById(R.id.upgradeBtn);
        adContainer = findViewById(R.id.adContainer);
        proCard = findViewById(R.id.proCard);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupAdManager() {
        adManager = new AdManager(this);

        // Load banner ad if user is not Pro
        if (currentUser == null || !currentUser.isPro()) {
            adManager.loadBannerAd(adContainer);
        }
    }

    private void setupClickListeners() {
        createProjectBtn.setOnClickListener(v -> {
            checkPermissionsAndStartEditor();
        });

        myProjectsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProjectListActivity.class);
            startActivity(intent);
        });

        presetsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PresetsActivity.class);
            startActivity(intent);
        });

        musicLibraryBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MusicLibraryActivity.class);
            startActivity(intent);
        });

        upgradeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProUpgradeActivity.class);
            startActivity(intent);
        });
    }

    private void checkPermissionsAndStartEditor() {
        // Check media permissions
        PermissionHelper permissionHelper = new PermissionHelper(this);
        if (permissionHelper.hasMediaPermissions()) {
            startProjectEditor();
        } else {
            permissionHelper.requestMediaPermissions();
        }
    }

    private void startProjectEditor() {
        Intent intent = new Intent(MainActivity.this, ProjectEditorActivity.class);
        startActivity(intent);

        // Show interstitial ad if appropriate
        if (currentUser == null || !currentUser.isPro()) {
            adManager.showInterstitialAd();
        }
    }

    private void checkUserStatus() {
        new Thread(() -> {
            currentUser = ChoreoCamApplication.getDatabase().userDao().getCurrentUser();
            runOnUiThread(() -> {
                if (currentUser != null && currentUser.isPro()) {
                    // Hide pro card and ads for Pro users
                    proCard.setVisibility(View.GONE);
                    adContainer.setVisibility(View.GONE);
                }
            });
        }).start();
    }

    private void checkNetworkStatus() {
        boolean isOnline = NetworkUtils.isNetworkAvailable(this);
        offlineIndicator.setVisibility(isOnline ? View.GONE : View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_account) {
            Intent intent = new Intent(this, AuthActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adManager.resumeAd();
        checkNetworkStatus();
        checkUserStatus();
    }

    @Override
    protected void onPause() {
        adManager.pauseAd();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        adManager.destroyAd();
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper permissionHelper = new PermissionHelper(this);
        if (permissionHelper.hasMediaPermissions()) {
            startProjectEditor();
        }
    }
}
