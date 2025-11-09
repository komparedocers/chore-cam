package com.choreocam.app.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.choreocam.app.models.User;
import java.util.List;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);

    @Query("SELECT * FROM users WHERE id = :id")
    LiveData<User> getUserById(long id);

    @Query("SELECT * FROM users WHERE userId = :userId")
    User getUserByUserId(String userId);

    @Query("SELECT * FROM users WHERE email = :email")
    User getUserByEmail(String email);

    @Query("SELECT * FROM users LIMIT 1")
    User getCurrentUser();

    @Query("SELECT * FROM users LIMIT 1")
    LiveData<User> getCurrentUserLive();

    @Query("SELECT * FROM users WHERE needsSync = 1")
    List<User> getUsersNeedingSync();

    @Query("DELETE FROM users")
    void deleteAll();
}
