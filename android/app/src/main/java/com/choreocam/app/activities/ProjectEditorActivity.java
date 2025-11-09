package com.choreocam.app.activities;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.choreocam.app.ChoreoCamApplication;
import com.choreocam.app.R;
import com.choreocam.app.database.AppDatabase;
import com.choreocam.app.models.Project;
import com.choreocam.app.models.User;
import com.choreocam.app.utils.AdManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ProjectEditorActivity extends AppCompatActivity {

    private TextInputEditText titleInput;
    private MaterialButton addClipsBtn;
    private MaterialButton selectMusicBtn;
    private MaterialButton choosePresetBtn;
    private MaterialButton autoEditBtn;
    private MaterialButton exportBtn;
    private FrameLayout adContainer;

    private AdManager adManager;
    private AppDatabase database;
    private Project currentProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_editor);

        database = ChoreoCamApplication.getDatabase();

        initializeViews();
        setupToolbar();
        setupAdManager();
        setupClickListeners();
        createNewProject();
    }

    private void initializeViews() {
        titleInput = findViewById(R.id.titleInput);
        addClipsBtn = findViewById(R.id.addClipsBtn);
        selectMusicBtn = findViewById(R.id.selectMusicBtn);
        choosePresetBtn = findViewById(R.id.choosePresetBtn);
        autoEditBtn = findViewById(R.id.autoEditBtn);
        exportBtn = findViewById(R.id.exportBtn);
        adContainer = findViewById(R.id.adContainer);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("New Project");
        }
    }

    private void setupAdManager() {
        adManager = new AdManager(this);
        new Thread(() -> {
            User currentUser = database.userDao().getCurrentUser();
            runOnUiThread(() -> {
                if (currentUser == null || !currentUser.isPro()) {
                    adManager.loadBannerAd(adContainer);
                }
            });
        }).start();
    }

    private void setupClickListeners() {
        addClipsBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Add video clips functionality", Toast.LENGTH_SHORT).show();
        });

        selectMusicBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Select music functionality", Toast.LENGTH_SHORT).show();
        });

        choosePresetBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Choose preset functionality", Toast.LENGTH_SHORT).show();
        });

        autoEditBtn.setOnClickListener(v -> {
            performAutoEdit();
        });

        exportBtn.setOnClickListener(v -> {
            exportProject();
        });
    }

    private void createNewProject() {
        new Thread(() -> {
            currentProject = new Project();
            User currentUser = database.userDao().getCurrentUser();
            if (currentUser != null) {
                currentProject.setUserId(currentUser.getId());
            }
            currentProject.setTitle("Untitled Project");
            long projectId = database.projectDao().insert(currentProject);
            currentProject.setId(projectId);
        }).start();
    }

    private void performAutoEdit() {
        String title = titleInput.getText() != null ? titleInput.getText().toString() : "";
        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a project title", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Auto-editing video... This may take a moment", Toast.LENGTH_LONG).show();

        new Thread(() -> {
            if (currentProject != null) {
                currentProject.setTitle(title);
                currentProject.setStatus("rendering");
                database.projectDao().update(currentProject);

                // Simulate processing
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnUiThread(() -> {
                    Toast.makeText(this, "Preview ready!", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void exportProject() {
        if (currentProject == null) {
            Toast.makeText(this, "No project to export", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Exporting video...", Toast.LENGTH_SHORT).show();

        new Thread(() -> {
            currentProject.setStatus("completed");
            currentProject.setOutputFilePath("/storage/emulated/0/ChoreoCam/export_" +
                System.currentTimeMillis() + ".mp4");
            database.projectDao().update(currentProject);

            runOnUiThread(() -> {
                Toast.makeText(this, "Export complete!", Toast.LENGTH_LONG).show();
                finish();
            });
        }).start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adManager != null) {
            adManager.resumeAd();
        }
    }

    @Override
    protected void onPause() {
        if (adManager != null) {
            adManager.pauseAd();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (adManager != null) {
            adManager.destroyAd();
        }
        super.onDestroy();
    }
}
