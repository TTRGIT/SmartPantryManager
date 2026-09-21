package com.smartpantry.manager.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/** Required ingredient and quantity for a recipe. */
@Entity(tableName = "recipe_ingredients",
        foreignKeys = @ForeignKey(entity = Recipe.class, parentColumns = "id",
                childColumns = "recipeId", onDelete = ForeignKey.CASCADE), // every requirement must belong to a valid recipe
        indices = {@Index("recipeId"), @Index("normalizedName")}) // parent-child relation lookup and ingredient matching
public class RecipeIngredient {
    /** Primary key for this individual requirement row. */
    @PrimaryKey(autoGenerate = true)
    public long id;
    public long recipeId; // Foreign key linking this row to its recipe.
    @NonNull public String name; // visible ingredient name.
    @NonNull public String normalizedName; // Classic name used for matching.
    public double quantity; // Minimum amount required.
    @NonNull public String unit; // Unit in which the amount is expressed.

    /** Creates a required ingredient attached to a recipe header. */
    public RecipeIngredient(long recipeId, @NonNull String name, @NonNull String normalizedName,
                            double quantity, @NonNull String unit) {
        this.recipeId = recipeId;
        this.name = name;
        this.normalizedName = normalizedName;
        this.quantity = quantity;
        this.unit = unit;
    }
}
