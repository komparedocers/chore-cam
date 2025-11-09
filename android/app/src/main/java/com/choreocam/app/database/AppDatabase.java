package com.choreocam.app.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.choreocam.app.database.dao.MusicTrackDao;
import com.choreocam.app.database.dao.PresetDao;
import com.choreocam.app.database.dao.ProjectDao;
import com.choreocam.app.database.dao.UserDao;
import com.choreocam.app.models.MusicTrack;
import com.choreocam.app.models.Preset;
import com.choreocam.app.models.Project;
import com.choreocam.app.models.User;

@Database(
    entities = {User.class, Project.class, Preset.class, MusicTrack.class},
    version = 1,
    exportSchema = true
)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "choreocam_db";
    private static volatile AppDatabase instance;

    public abstract UserDao userDao();
    public abstract ProjectDao projectDao();
    public abstract PresetDao presetDao();
    public abstract MusicTrackDao musicTrackDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                context.getApplicationContext(),
                AppDatabase.class,
                DATABASE_NAME
            )
            .fallbackToDestructiveMigration()
            .build();
        }
        return instance;
    }
}
