package com.choreocam.app.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "presets")
public class Preset {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String presetId;
    private String name;
    private String description;
    private String thumbnailUrl;
    private String category; // basic, premium, trending
    private boolean isPro;
    private String transitionsJson; // JSON array of transition configs
    private String effectsJson; // JSON array of effect configs
    private String captionStyleJson; // JSON object for caption styling
    private long createdAt;
    private long cachedAt;

    public Preset() {
        this.cachedAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPresetId() {
        return presetId;
    }

    public void setPresetId(String presetId) {
        this.presetId = presetId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isPro() {
        return isPro;
    }

    public void setPro(boolean pro) {
        isPro = pro;
    }

    public String getTransitionsJson() {
        return transitionsJson;
    }

    public void setTransitionsJson(String transitionsJson) {
        this.transitionsJson = transitionsJson;
    }

    public String getEffectsJson() {
        return effectsJson;
    }

    public void setEffectsJson(String effectsJson) {
        this.effectsJson = effectsJson;
    }

    public String getCaptionStyleJson() {
        return captionStyleJson;
    }

    public void setCaptionStyleJson(String captionStyleJson) {
        this.captionStyleJson = captionStyleJson;
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
