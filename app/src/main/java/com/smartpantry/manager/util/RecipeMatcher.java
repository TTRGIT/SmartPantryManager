package com.smartpantry.manager.util;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.smartpantry.manager.model.PantryItem;
import com.smartpantry.manager.model.RecipeIngredient;
import com.smartpantry.manager.model.RecipeMatch;
import com.smartpantry.manager.model.RecipeWithIngredients;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/** Strictly matches recipes. Insufficient quantity counts as a missing ingredient. */
public final class RecipeMatcher {
    private RecipeMatcher() { }

    /**
     * Evaluates every recipe independently and returns its complete missing list. The UI then
     * shows zero-missing results as ready and exactly-one-missing results as Almost There.
     */
    public static List<RecipeMatch> match(List<RecipeWithIngredients> recipes,
                                          List<PantryItem> pantry) {
        List<RecipeMatch> results = new ArrayList<>();
        if (recipes == null) return results;

        for (RecipeWithIngredients recipe : recipes) {
            List<String> missing = new ArrayList<>();
            for (RecipeIngredient required : recipe.ingredients) {
                // Several pantry rows for the same ingredient are combined after conversion.
                double totalInRequiredUnit = 0;
                if (pantry != null) {
                    for (PantryItem available : pantry) {
                        // Expired food never counts as available for a safe suggestion.
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            if (!isExpired(available.expiryDate)
                                    && required.normalizedName.equals(available.normalizedName)) {
                                totalInRequiredUnit += UnitConverter.valueInRequiredUnit(available.quantity, available.unit, required.unit);
                            }
                        }
                    }
                }
                // A tiny tolerance avoids false misses caused by floating-point rounding.
                if (totalInRequiredUnit + 0.000001 < required.quantity) {
                    missing.add(required.name);
                }
            }
            results.add(new RecipeMatch(recipe, missing));
        }
        return results;
    }

    /** Returns true only when a valid ISO date is earlier than today. */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean isExpired(String isoDate) {
        if (isoDate == null || isoDate.trim().isEmpty()) return false;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return LocalDate.parse(isoDate).isBefore(LocalDate.now());
            }
        } catch (DateTimeParseException ignored) {
            return false;
        }
        return false;
    }
}
