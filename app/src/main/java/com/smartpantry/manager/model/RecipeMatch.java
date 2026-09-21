package com.smartpantry.manager.model;

import java.util.Collections;
import java.util.List;

/** Unmodifiable result from comparing one recipe with the pantry. Also,
 * match status should change whenever pantry item change, therefore provides
 * the exact data for green ready or orange missing badges. */
public class RecipeMatch {
    public final RecipeWithIngredients recipe; // Recipe that was evaluated.
    public final List<String> missingIngredients; // Missing or insufficient requirements.

    /** Wraps a match result and prevents accidental modification of its missing list. */
    public RecipeMatch(RecipeWithIngredients recipe, List<String> missingIngredients) {
        this.recipe = recipe;
        this.missingIngredients = Collections.unmodifiableList(missingIngredients);
    }

    /** A recipe is strictly ready only when nothing is missing. */
    public boolean isReady() {
        return missingIngredients.isEmpty();
    }
}
