package com.choreocam.app.api.models;

public class SyncResponse {
    private boolean success;
    private String message;
    private int usersSynced;
    private int projectsSynced;
    private long serverTimestamp;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getUsersSynced() {
        return usersSynced;
    }

    public void setUsersSynced(int usersSynced) {
        this.usersSynced = usersSynced;
    }

    public int getProjectsSynced() {
        return projectsSynced;
    }

    public void setProjectsSynced(int projectsSynced) {
        this.projectsSynced = projectsSynced;
    }

    public long getServerTimestamp() {
        return serverTimestamp;
    }

    public void setServerTimestamp(long serverTimestamp) {
        this.serverTimestamp = serverTimestamp;
    }
}
