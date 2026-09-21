package com.smartpantry.manager.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

/** Room relation used by recipe list/detail screens and the matcher.
 * This gives the matcher and details screen one object containing everything about a recipe. */
public class RecipeWithIngredients {
    // @Embedded tells Room to populate the recipe header in this object.
    @Embedded public Recipe recipe;

    // @Relation fetches every child row whose recipeId matches the header ID.
    @Relation(parentColumn = "id", entityColumn = "recipeId")
    public List<RecipeIngredient> ingredients; //Holds the complete requirements
}
