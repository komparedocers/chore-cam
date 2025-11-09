package com.choreocam.app.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.choreocam.app.models.MusicTrack;
import java.util.List;

@Dao
public interface MusicTrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(MusicTrack track);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<MusicTrack> tracks);

    @Update
    void update(MusicTrack track);

    @Delete
    void delete(MusicTrack track);

    @Query("SELECT * FROM music_tracks ORDER BY title ASC")
    LiveData<List<MusicTrack>> getAllTracks();

    @Query("SELECT * FROM music_tracks WHERE id = :id")
    LiveData<MusicTrack> getTrackById(long id);

    @Query("SELECT * FROM music_tracks WHERE trackId = :trackId")
    MusicTrack getTrackByTrackId(String trackId);

    @Query("SELECT * FROM music_tracks WHERE genre = :genre ORDER BY title ASC")
    LiveData<List<MusicTrack>> getTracksByGenre(String genre);

    @Query("SELECT * FROM music_tracks WHERE isPro = 0 ORDER BY title ASC")
    LiveData<List<MusicTrack>> getFreeTracks();

    @Query("SELECT * FROM music_tracks WHERE isPro = 1 ORDER BY title ASC")
    LiveData<List<MusicTrack>> getProTracks();

    @Query("SELECT * FROM music_tracks WHERE localFilePath IS NOT NULL")
    LiveData<List<MusicTrack>> getDownloadedTracks();

    @Query("DELETE FROM music_tracks")
    void deleteAll();
}
