package com.choreocam.app.api.models;

import com.choreocam.app.models.Project;
import com.choreocam.app.models.User;
import java.util.List;

public class SyncRequest {
    private User user;
    private List<Project> projects;
    private long lastSyncTimestamp;

    public SyncRequest() {}

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public long getLastSyncTimestamp() {
        return lastSyncTimestamp;
    }

    public void setLastSyncTimestamp(long lastSyncTimestamp) {
        this.lastSyncTimestamp = lastSyncTimestamp;
    }
}
