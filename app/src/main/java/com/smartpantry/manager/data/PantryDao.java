package com.smartpantry.manager.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.smartpantry.manager.model.PantryItem;

import java.util.List;

/** Creating the Room data-access object that handles pantry items operations: creating, reading, updating, and deleting (CRUD) in the database */
@Dao
public class PantryDao {
    /** Checks every pantry row and automatically updates the UI after a change. */
    @Query("SELECT * FROM pantry_items ORDER BY name COLLATE NOCASE")
    LiveData<List<PantryItem>> observeAll();

    /** Retrieves one item when its database identifier is known. */
    @Query("SELECT * FROM pantry_items WHERE id = :id LIMIT 1")
    PantryItem getById(long id);

    /** Creates a pantry row and returns its generated primary key. */
    @Insert
    long insert(PantryItem item);

    /** Replaces the stored values of the row with the same primary key. */
    @Update
    void update(PantryItem item);

    /** Permanently removes the supplied pantry row. */
    @Delete
    void delete(PantryItem item);
}
