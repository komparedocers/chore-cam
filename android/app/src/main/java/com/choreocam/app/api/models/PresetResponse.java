package com.choreocam.app.api.models;

import com.choreocam.app.models.Preset;
import java.util.List;

public class PresetResponse {
    private boolean success;
    private String message;
    private List<Preset> presets;
    private Preset preset;

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

    public List<Preset> getPresets() {
        return presets;
    }

    public void setPresets(List<Preset> presets) {
        this.presets = presets;
    }

    public Preset getPreset() {
        return preset;
    }

    public void setPreset(Preset preset) {
        this.preset = preset;
    }
}
