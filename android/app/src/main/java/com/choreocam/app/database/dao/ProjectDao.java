package com.choreocam.app.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.choreocam.app.models.Project;
import java.util.List;

@Dao
public interface ProjectDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Project project);

    @Update
    void update(Project project);

    @Delete
    void delete(Project project);

    @Query("SELECT * FROM projects ORDER BY updatedAt DESC")
    LiveData<List<Project>> getAllProjects();

    @Query("SELECT * FROM projects WHERE id = :id")
    LiveData<Project> getProjectById(long id);

    @Query("SELECT * FROM projects WHERE projectId = :projectId")
    Project getProjectByProjectId(String projectId);

    @Query("SELECT * FROM projects WHERE userId = :userId ORDER BY updatedAt DESC")
    LiveData<List<Project>> getProjectsByUserId(long userId);

    @Query("SELECT * FROM projects WHERE status = :status ORDER BY updatedAt DESC")
    LiveData<List<Project>> getProjectsByStatus(String status);

    @Query("SELECT * FROM projects WHERE needsSync = 1")
    List<Project> getProjectsNeedingSync();

    @Query("DELETE FROM projects WHERE id = :id")
    void deleteById(long id);

    @Query("DELETE FROM projects")
    void deleteAll();
}
