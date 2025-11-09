package com.choreocam.app.api;

import com.choreocam.app.api.models.AuthRequest;
import com.choreocam.app.api.models.AuthResponse;
import com.choreocam.app.api.models.PresetResponse;
import com.choreocam.app.api.models.MusicResponse;
import com.choreocam.app.api.models.StyleLearnRequest;
import com.choreocam.app.api.models.SyncRequest;
import com.choreocam.app.api.models.SyncResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    // Authentication
    @POST("auth/register")
    Call<AuthResponse> register(@Body AuthRequest request);

    @POST("auth/login")
    Call<AuthResponse> login(@Body AuthRequest request);

    // Presets
    @GET("presets")
    Call<PresetResponse> getPresets();

    @GET("presets/{id}")
    Call<PresetResponse> getPresetById(@Path("id") String presetId);

    // Music
    @GET("music")
    Call<MusicResponse> getMusicTracks();

    @GET("music/{id}")
    Call<MusicResponse> getMusicTrackById(@Path("id") String trackId);

    // Style Learning
    @POST("style/learn")
    Call<Void> learnStyle(@Body StyleLearnRequest request);

    @GET("style/suggestions")
    Call<PresetResponse> getStyleSuggestions();

    // Sync
    @POST("sync")
    Call<SyncResponse> syncData(@Body SyncRequest request);

    // User profile
    @GET("user/profile")
    Call<AuthResponse> getUserProfile();

    @PUT("user/profile")
    Call<AuthResponse> updateUserProfile(@Body AuthRequest request);
}
