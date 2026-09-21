package com.smartpantry.manager.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.smartpantry.manager.model.Recipe;
import com.smartpantry.manager.model.RecipeIngredient;
import com.smartpantry.manager.model.RecipeWithIngredients;

import java.util.List;

@Dao
/** Defines Room queries for recipe headers and their child ingredient rows. */
public class RecipeDao {
    /** Loads every recipe together with its required ingredients as observable data. */
    @Transaction
    @Query("SELECT * FROM recipes ORDER BY name COLLATE NOCASE")
    LiveData<List<RecipeWithIngredients>> observeAllWithIngredients();

    /** Loads one complete recipe for the detail screen. */
    @Transaction
    @Query("SELECT * FROM recipes WHERE id = :id LIMIT 1")
    LiveData<RecipeWithIngredients> observeById(long id);

    /** Returns the number of recipes so seeding only happens on an empty database. */
    @Query("SELECT COUNT(*) FROM recipes")
    int count();

    /** Inserts the recipe header before its ingredients and returns the new ID. */
    @Insert
    long insertRecipe(Recipe recipe);

    /** Inserts all ingredient rows belonging to a recipe in one call. */
    @Insert
    void insertIngredients(List<RecipeIngredient> ingredients);
}
