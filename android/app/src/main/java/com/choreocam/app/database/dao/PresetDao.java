package com.choreocam.app.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.choreocam.app.models.Preset;
import java.util.List;

@Dao
public interface PresetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Preset preset);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Preset> presets);

    @Update
    void update(Preset preset);

    @Delete
    void delete(Preset preset);

    @Query("SELECT * FROM presets ORDER BY cachedAt DESC")
    LiveData<List<Preset>> getAllPresets();

    @Query("SELECT * FROM presets WHERE id = :id")
    LiveData<Preset> getPresetById(long id);

    @Query("SELECT * FROM presets WHERE presetId = :presetId")
    Preset getPresetByPresetId(String presetId);

    @Query("SELECT * FROM presets WHERE category = :category ORDER BY cachedAt DESC")
    LiveData<List<Preset>> getPresetsByCategory(String category);

    @Query("SELECT * FROM presets WHERE isPro = 0 ORDER BY cachedAt DESC")
    LiveData<List<Preset>> getFreePresets();

    @Query("SELECT * FROM presets WHERE isPro = 1 ORDER BY cachedAt DESC")
    LiveData<List<Preset>> getProPresets();

    @Query("DELETE FROM presets")
    void deleteAll();
}
