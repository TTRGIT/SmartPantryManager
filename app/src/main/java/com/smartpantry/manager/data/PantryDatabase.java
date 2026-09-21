package com.smartpantry.manager.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.smartpantry.manager.model.PantryItem;
import com.smartpantry.manager.model.Recipe;
import com.smartpantry.manager.model.RecipeIngredient;

/** On-device SQLite database. Room validates queries and keeps data across launches. */
@Database(entities = {PantryItem.class, Recipe.class, RecipeIngredient.class},
        version = 1, exportSchema = false)
public abstract class PantryDatabase extends RoomDatabase {
    // Volatile makes the singleton safely visible to every application thread.
    private static volatile PantryDatabase INSTANCE;

    /** Gives Room access to pantry CRUD queries. */
    public abstract PantryDao pantryDao();
    /** Gives Room access to recipe and recipe-ingredient queries. */
    public abstract RecipeDao recipeDao();

    /**
     * The following function ensures that one database copy for the whole app is created.
     * As it stops an activity screen from getting saved by mistake.
     */
    public static PantryDatabase getInstance(Context context) {
        // Double-checked locking that avoids building the SQLite database more than once.
        if (INSTANCE == null) {
            synchronized (PantryDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            PantryDatabase.class, "smart_pantry.db").build();
                }
            }
        }
        return INSTANCE;
    }
}
