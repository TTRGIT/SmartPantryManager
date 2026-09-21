package com.smartpantry.manager.data;

import com.smartpantry.manager.model.Recipe;
import com.smartpantry.manager.model.RecipeIngredient;
import com.smartpantry.manager.util.IngredientNormalizer;

import java.util.ArrayList;
import java.util.List;

/** Inserts the built-in recipe collection once, on the first database launch. */
final class SeedData {
    private SeedData() { }

    static void insertIfEmpty(PantryDatabase db) {
        RecipeDao dao = db.recipeDao();
        // Existing rows mean first-run setup has already been completed.
        if (dao.count() > 0) return;
        // A transaction prevents users from seeing a half-seeded recipe collection.
        db.runInTransaction(() -> {
            if (dao.count() > 0) return;
            add(dao, "Tomato Toast", "1. Toast the bread.\n2. Spread with butter.\n3. Slice the tomato and arrange it on top.",
                    ing("Bread", 2, "slice"), ing("Tomato", 1, "piece"), ing("Butter", 10, "g"));
            add(dao, "Creamy Scrambled Eggs", "1. Beat the eggs with milk.\n2. Melt butter in a pan.\n3. Cook gently, stirring until softly set.",
                    ing("Egg", 2, "piece"), ing("Milk", 50, "ml"), ing("Butter", 10, "g"));
            add(dao, "Cheese Omelette", "1. Beat eggs with milk.\n2. Pour into a buttered pan.\n3. Add cheese, fold, and cook until set.",
                    ing("Egg", 2, "piece"), ing("Milk", 30, "ml"), ing("Cheese", 30, "g"), ing("Butter", 10, "g"));
            add(dao, "Grilled Cheese", "1. Butter the bread.\n2. Place cheese between the slices.\n3. Toast in a pan until golden on both sides.",
                    ing("Bread", 2, "slice"), ing("Cheese", 50, "g"), ing("Butter", 10, "g"));
            add(dao, "Banana Breakfast Oats", "1. Simmer oats and milk for 5 minutes.\n2. Slice the banana.\n3. Serve the oats topped with banana.",
                    ing("Oats", 50, "g"), ing("Milk", 200, "ml"), ing("Banana", 1, "piece"));
            add(dao, "Fresh Tomato Pasta", "1. Boil pasta until tender.\n2. Fry chopped garlic in oil.\n3. Add tomatoes, simmer, and toss with pasta.",
                    ing("Pasta", 200, "g"), ing("Tomato", 3, "piece"), ing("Garlic", 2, "piece"), ing("Olive oil", 15, "ml"));
            add(dao, "Garlic Butter Pasta", "1. Cook and drain the pasta.\n2. Melt butter with chopped garlic.\n3. Toss together and serve.",
                    ing("Pasta", 200, "g"), ing("Butter", 25, "g"), ing("Garlic", 2, "piece"));
            add(dao, "Quick Egg Fried Rice", "1. Fry diced carrot in oil.\n2. Scramble in the eggs.\n3. Add cooked rice and soy sauce; stir-fry until hot.",
                    ing("Rice", 300, "g"), ing("Egg", 2, "piece"), ing("Carrot", 1, "piece"), ing("Soy sauce", 30, "ml"), ing("Oil", 15, "ml"));
            add(dao, "Crispy Potato Hash", "1. Dice potatoes and onion.\n2. Heat oil in a pan.\n3. Fry until crisp and cooked through.",
                    ing("Potato", 3, "piece"), ing("Onion", 1, "piece"), ing("Oil", 30, "ml"));
            add(dao, "Creamy Mashed Potatoes", "1. Boil potatoes until soft.\n2. Drain and mash.\n3. Beat in warm milk and butter.",
                    ing("Potato", 4, "piece"), ing("Milk", 100, "ml"), ing("Butter", 30, "g"));
            add(dao, "Lentil Vegetable Soup", "1. Chop the vegetables.\n2. Add everything to a pot.\n3. Simmer for 30 minutes until the lentils are tender.",
                    ing("Lentils", 200, "g"), ing("Tomato", 2, "piece"), ing("Onion", 1, "piece"), ing("Carrot", 2, "piece"), ing("Water", 1, "l"));
            add(dao, "Chickpea Cucumber Salad", "1. Drain the chickpeas.\n2. Chop tomato and cucumber.\n3. Toss everything with olive oil.",
                    ing("Chickpeas", 240, "g"), ing("Tomato", 2, "piece"), ing("Cucumber", 1, "piece"), ing("Olive oil", 30, "ml"));
            add(dao, "Tuna Mayo Sandwich", "1. Mix tuna and mayonnaise.\n2. Spoon onto two bread slices.\n3. Top with the remaining bread and serve.",
                    ing("Bread", 4, "slice"), ing("Tuna", 1, "can"), ing("Mayonnaise", 30, "g"));
            add(dao, "Simple Bean Salad", "1. Drain the beans.\n2. Dice tomato and onion.\n3. Combine and serve chilled.",
                    ing("Kidney beans", 240, "g"), ing("Tomato", 2, "piece"), ing("Onion", 1, "piece"));
            add(dao, "Spinach Cheese Omelette", "1. Wilt spinach in a pan.\n2. Add beaten eggs.\n3. Sprinkle with cheese, fold, and cook through.",
                    ing("Egg", 2, "piece"), ing("Spinach", 100, "g"), ing("Cheese", 30, "g"));
            add(dao, "Colourful Vegetable Stir-fry", "1. Slice all vegetables.\n2. Stir-fry in hot oil until just tender.\n3. Add soy sauce and toss.",
                    ing("Broccoli", 1, "piece"), ing("Carrot", 2, "piece"), ing("Bell pepper", 1, "piece"), ing("Soy sauce", 30, "ml"), ing("Oil", 15, "ml"));
            add(dao, "Easy Pantry Pancakes", "1. Whisk flour, milk, and eggs.\n2. Melt a little butter in a pan.\n3. Cook ladles of batter until golden on both sides.",
                    ing("Flour", 200, "g"), ing("Milk", 300, "ml"), ing("Egg", 2, "piece"), ing("Butter", 20, "g"));
            add(dao, "Classic French Toast", "1. Beat eggs and milk.\n2. Dip each bread slice.\n3. Fry in butter until golden.",
                    ing("Bread", 4, "slice"), ing("Egg", 2, "piece"), ing("Milk", 100, "ml"), ing("Butter", 20, "g"));
            add(dao, "Apple Cinnamon Oats", "1. Simmer oats and milk.\n2. Dice and stir in the apple.\n3. Sprinkle with cinnamon and serve.",
                    ing("Oats", 50, "g"), ing("Milk", 200, "ml"), ing("Apple", 1, "piece"), ing("Cinnamon", 1, "tsp"));
            add(dao, "Garden Side Salad", "1. Tear the lettuce.\n2. Chop tomato and cucumber.\n3. Toss with olive oil just before serving.",
                    ing("Lettuce", 1, "piece"), ing("Tomato", 2, "piece"), ing("Cucumber", 1, "piece"), ing("Olive oil", 30, "ml"));
        });
    }

    private static Ingredient ing(String name, double quantity, String unit) {
        // Short factory keeps the 20 recipe declarations easy to read.
        return new Ingredient(name, quantity, unit);
    }

    /** Inserts one header and all of its normalized child ingredient records. */
    private static void add(RecipeDao dao, String name, String method, Ingredient... values) {
        long recipeId = dao.insertRecipe(new Recipe(name, method));
        List<RecipeIngredient> ingredients = new ArrayList<>();
        for (Ingredient value : values) {
            ingredients.add(new RecipeIngredient(recipeId, value.name,
                    IngredientNormalizer.normalize(value.name), value.quantity, value.unit));
        }
        dao.insertIngredients(ingredients);
    }

    /** Temporary value object used only while constructing seed recipes. */
    private static class Ingredient {
        final String name;
        final double quantity;
        final String unit;

        Ingredient(String name, double quantity, String unit) {
            this.name = name;
            this.quantity = quantity;
            this.unit = unit;
        }
    }
}
