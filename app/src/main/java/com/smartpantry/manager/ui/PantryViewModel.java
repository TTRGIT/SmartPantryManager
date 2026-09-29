package com.smartpantry.manager.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.smartpantry.manager.data.PantryRepository;
import com.smartpantry.manager.model.PantryItem;
import com.smartpantry.manager.model.RecipeWithIngredients;

import java.util.List;

/** Shares database-backed state between the main activity's fragments. */
public class PantryViewModel extends AndroidViewModel {
    // The repository survives fragment replacement because the ViewModel belongs to MainActivity.
    private final PantryRepository repository;

    /** Creates the shared database repository with a lifecycle-safe application context. */
    public PantryViewModel(@NonNull Application application) {
        super(application);
        repository = new PantryRepository(application);
    }

    /** Exposes live pantry rows without allowing the UI to issue SQL directly. */
    public LiveData<List<PantryItem>> pantry() { return repository.observePantry(); }

    /** Exposes complete recipes for strict matching. */
    public LiveData<List<RecipeWithIngredients>> recipes() { return repository.observeRecipes(); }

    /** Forwards a confirmed delete request to the background repository. */
    public void delete(PantryItem item) { repository.delete(item); }
}
