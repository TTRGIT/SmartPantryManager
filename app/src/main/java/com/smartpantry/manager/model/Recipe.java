package com.smartpantry.manager.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/** creating the parent recipe table, however the required ingredients will be stored in separate recipe_ingredients table  */
@Entity(tableName = "recipes")
public class Recipe {
    /** Unique recipe key referenced by every RecipeIngredient child row. */
    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull public String name; // Title shown in recipe cards and details.
    @NonNull public String method; // Newline-separated preparation instructions.

    /** Creates a recipe header before its required ingredients are added. */
    public Recipe(@NonNull String name, @NonNull String method) {
        this.name = name;
        this.method = method;
    }
}
