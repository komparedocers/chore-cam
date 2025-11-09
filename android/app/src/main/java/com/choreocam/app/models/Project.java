package com.choreocam.app.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "projects")
public class Project {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String projectId;
    private long userId;
    private String title;
    private String description;
    private String clipsMetaJson; // JSON array of clip metadata
    private long chosenPresetId;
    private long musicId;
    private String status; // draft, rendering, completed, failed
    private String outputFilePath;
    private long duration;
    private String resolution;
    private long createdAt;
    private long updatedAt;
    private boolean needsSync;

    public Project() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.status = "draft";
        this.needsSync = true;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClipsMetaJson() {
        return clipsMetaJson;
    }

    public void setClipsMetaJson(String clipsMetaJson) {
        this.clipsMetaJson = clipsMetaJson;
    }

    public long getChosenPresetId() {
        return chosenPresetId;
    }

    public void setChosenPresetId(long chosenPresetId) {
        this.chosenPresetId = chosenPresetId;
    }

    public long getMusicId() {
        return musicId;
    }

    public void setMusicId(long musicId) {
        this.musicId = musicId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOutputFilePath() {
        return outputFilePath;
    }

    public void setOutputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isNeedsSync() {
        return needsSync;
    }

    public void setNeedsSync(boolean needsSync) {
        this.needsSync = needsSync;
    }
}
