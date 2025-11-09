package com.choreocam.app.api.models;

import com.choreocam.app.models.MusicTrack;
import java.util.List;

public class MusicResponse {
    private boolean success;
    private String message;
    private List<MusicTrack> tracks;
    private MusicTrack track;

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

    public List<MusicTrack> getTracks() {
        return tracks;
    }

    public void setTracks(List<MusicTrack> tracks) {
        this.tracks = tracks;
    }

    public MusicTrack getTrack() {
        return track;
    }

    public void setTrack(MusicTrack track) {
        this.track = track;
    }
}
