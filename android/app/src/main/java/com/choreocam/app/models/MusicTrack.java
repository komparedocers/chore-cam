package com.choreocam.app.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "music_tracks")
public class MusicTrack {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String trackId;
    private String title;
    private String artist;
    private String genre;
    private int bpm;
    private long durationMs;
    private String fileUrl;
    private String localFilePath;
    private boolean isPro;
    private String cuePointsJson; // JSON array of beat markers
    private long createdAt;
    private long cachedAt;

    public MusicTrack() {
        this.cachedAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getBpm() {
        return bpm;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(long durationMs) {
        this.durationMs = durationMs;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
    }

    public boolean isPro() {
        return isPro;
    }

    public void setPro(boolean pro) {
        isPro = pro;
    }

    public String getCuePointsJson() {
        return cuePointsJson;
    }

    public void setCuePointsJson(String cuePointsJson) {
        this.cuePointsJson = cuePointsJson;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getCachedAt() {
        return cachedAt;
    }

    public void setCachedAt(long cachedAt) {
        this.cachedAt = cachedAt;
    }
}
