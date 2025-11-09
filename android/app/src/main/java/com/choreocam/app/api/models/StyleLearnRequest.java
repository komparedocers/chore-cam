package com.choreocam.app.api.models;

public class StyleLearnRequest {
    private String projectId;
    private String cutStyle;
    private int averageClipLength;
    private String transitionPreferences;
    private String musicGenre;

    public StyleLearnRequest() {}

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getCutStyle() {
        return cutStyle;
    }

    public void setCutStyle(String cutStyle) {
        this.cutStyle = cutStyle;
    }

    public int getAverageClipLength() {
        return averageClipLength;
    }

    public void setAverageClipLength(int averageClipLength) {
        this.averageClipLength = averageClipLength;
    }

    public String getTransitionPreferences() {
        return transitionPreferences;
    }

    public void setTransitionPreferences(String transitionPreferences) {
        this.transitionPreferences = transitionPreferences;
    }

    public String getMusicGenre() {
        return musicGenre;
    }

    public void setMusicGenre(String musicGenre) {
        this.musicGenre = musicGenre;
    }
}
