package com.choreocam.app.workers;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.choreocam.app.ChoreoCamApplication;
import com.choreocam.app.api.ApiClient;
import com.choreocam.app.api.models.SyncRequest;
import com.choreocam.app.api.models.SyncResponse;
import com.choreocam.app.database.AppDatabase;
import com.choreocam.app.models.Project;
import com.choreocam.app.models.User;
import retrofit2.Response;
import timber.log.Timber;
import java.util.List;

public class SyncWorker extends Worker {

    private final AppDatabase database;

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        this.database = ChoreoCamApplication.getDatabase();
        Timber.d("SyncWorker initialized, Run attempt: %d", getRunAttemptCount());
    }

    @NonNull
    @Override
    public Result doWork() {
        Timber.i("SyncWorker starting sync operation");

        try {
            // Check for internet connectivity
            if (!NetworkUtils.isNetworkAvailable(getApplicationContext())) {
                Timber.w("No network connection available, will retry later");
                return Result.retry();
            }

            Timber.d("Network connection available, proceeding with sync");

            // Get users and projects that need syncing
            List<User> usersToSync = database.userDao().getUsersNeedingSync();
            List<Project> projectsToSync = database.projectDao().getProjectsNeedingSync();

            Timber.d("Found %d users and %d projects needing sync",
                usersToSync.size(), projectsToSync.size());

            if (usersToSync.isEmpty() && projectsToSync.isEmpty()) {
                Timber.i("No data to sync");
                return Result.success();
            }

            // Prepare sync request
            SyncRequest request = new SyncRequest();
            if (!usersToSync.isEmpty()) {
                User user = usersToSync.get(0);
                request.setUser(user);
                Timber.d("Syncing user: %s", user.getEmail());
            }
            request.setProjects(projectsToSync);
            request.setLastSyncTimestamp(System.currentTimeMillis());

            // Execute sync
            Timber.d("Executing sync API call");
            Response<SyncResponse> response = ApiClient.getApiService()
                .syncData(request)
                .execute();

            if (response.isSuccessful() && response.body() != null) {
                SyncResponse syncResponse = response.body();
                Timber.d("Sync response received: success=%b", syncResponse.isSuccess());

                if (syncResponse.isSuccess()) {
                    Timber.i("Sync successful, updating local database");

                    // Mark items as synced
                    for (User user : usersToSync) {
                        user.setNeedsSync(false);
                        user.setLastSyncedAt(System.currentTimeMillis());
                        database.userDao().update(user);
                        Timber.d("Updated sync status for user: %s", user.getEmail());
                    }

                    for (Project project : projectsToSync) {
                        project.setNeedsSync(false);
                        database.projectDao().update(project);
                        Timber.d("Updated sync status for project: %s", project.getTitle());
                    }

                    Timber.i("Sync completed successfully. Synced %d users, %d projects",
                        syncResponse.getUsersSynced(), syncResponse.getProjectsSynced());
                    return Result.success();
                } else {
                    Timber.w("Sync response indicated failure: %s", syncResponse.getMessage());
                }
            } else {
                Timber.w("Sync API call failed. Response code: %d, Message: %s",
                    response.code(), response.message());
            }

            // If sync failed, retry with exponential backoff
            Timber.d("Sync failed, scheduling retry");
            return Result.retry();

        } catch (Exception e) {
            Timber.e(e, "Exception during sync operation");
            // Retry on exception
            return Result.retry();
        }
    }
}
