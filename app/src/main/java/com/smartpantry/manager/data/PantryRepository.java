package com.smartpantry.manager.data;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.smartpantry.manager.model.PantryItem;
import com.smartpantry.manager.model.RecipeWithIngredients;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/** Single data boundary that let screens perform database work safely without containing SQL or threading code */
public class PantryRepository {
    // DAOs hide Room implementation details from activities and fragments.
    private final PantryDatabase database;
    private final PantryDao pantryDao;
    private final RecipeDao recipeDao;
    // SQLite writes and blocking reads must never run on Android's main/UI thread.
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    /** Connects the repository to the database and seeds recipes on first launch. */
    public PantryRepository(Context context) {
        database = PantryDatabase.getInstance(context);
        pantryDao = database.pantryDao();
        recipeDao = database.recipeDao();
        io.execute(() -> SeedData.insertIfEmpty(database));
    }

    /** Observable pantry list used by the pantry and recipe suggestion screens. */
    public LiveData<List<PantryItem>> observePantry() {
        return pantryDao.observeAll();
    }

    /** Observable full recipe collection used whenever matching is recalculated. */
    public LiveData<List<RecipeWithIngredients>> observeRecipes() {
        return recipeDao.observeAllWithIngredients();
    }

    /** Observable single recipe used by the detail activity. */
    public LiveData<RecipeWithIngredients> observeRecipe(long id) {
        return recipeDao.observeById(id);
    }

    /** Asynchronously creates an ingredient. */
    public void insert(PantryItem item) {
        io.execute(() -> pantryDao.insert(item));
    }

    /** Asynchronously saves edits to an existing ingredient. */
    public void update(PantryItem item) {
        io.execute(() -> pantryDao.update(item));
    }

    /** Asynchronously deletes an ingredient. */
    public void delete(PantryItem item) {
        io.execute(() -> pantryDao.delete(item));
    }

    /** Performs a one-off background read and returns it through the callback. */
    public void getPantryItem(long id, Consumer<PantryItem> callback) {
        io.execute(() -> callback.accept(pantryDao.getById(id)));
    }
}
