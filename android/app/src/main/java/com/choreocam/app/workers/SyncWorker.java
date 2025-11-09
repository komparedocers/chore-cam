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
import java.util.List;

public class SyncWorker extends Worker {

    private final AppDatabase database;

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        this.database = ChoreoCamApplication.getDatabase();
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            // Check for internet connectivity
            if (!NetworkUtils.isNetworkAvailable(getApplicationContext())) {
                return Result.retry();
            }

            // Get users and projects that need syncing
            List<User> usersToSync = database.userDao().getUsersNeedingSync();
            List<Project> projectsToSync = database.projectDao().getProjectsNeedingSync();

            if (usersToSync.isEmpty() && projectsToSync.isEmpty()) {
                return Result.success();
            }

            // Prepare sync request
            SyncRequest request = new SyncRequest();
            if (!usersToSync.isEmpty()) {
                request.setUser(usersToSync.get(0));
            }
            request.setProjects(projectsToSync);
            request.setLastSyncTimestamp(System.currentTimeMillis());

            // Execute sync
            Response<SyncResponse> response = ApiClient.getApiService()
                .syncData(request)
                .execute();

            if (response.isSuccessful() && response.body() != null) {
                SyncResponse syncResponse = response.body();

                if (syncResponse.isSuccess()) {
                    // Mark items as synced
                    for (User user : usersToSync) {
                        user.setNeedsSync(false);
                        user.setLastSyncedAt(System.currentTimeMillis());
                        database.userDao().update(user);
                    }

                    for (Project project : projectsToSync) {
                        project.setNeedsSync(false);
                        database.projectDao().update(project);
                    }

                    return Result.success();
                }
            }

            // If sync failed, retry with exponential backoff
            return Result.retry();

        } catch (Exception e) {
            e.printStackTrace();
            // Retry on exception
            return Result.retry();
        }
    }
}
