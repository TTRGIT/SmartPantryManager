package com.smartpantry.manager.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.smartpantry.manager.R;
import com.smartpantry.manager.data.PantryRepository;
import com.smartpantry.manager.model.RecipeIngredient;
import com.smartpantry.manager.model.RecipeWithIngredients;
import com.smartpantry.manager.util.Formatters;
import com.smartpantry.manager.util.SystemBarHelper;

/** Displays a database recipe selected from either recipe suggestion section. */
public class RecipeDetailActivity extends AppCompatActivity {
    // Intent keys identify the selected recipe and its match status at navigation time.
    public static final String EXTRA_RECIPE_ID = "recipe_id";
    public static final String EXTRA_MISSING = "missing_ingredients";

    private TextView name, status, method;
    private LinearLayout ingredientContainer;

    /** Reads the recipe ID, configures back navigation, and observes the Room relation. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        MaterialToolbar toolbar = findViewById(R.id.detail_toolbar);
        // Reserve the phone status-bar area above the recipe title and back button.
        SystemBarHelper.applyTopInset(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(v -> finish());

        name = findViewById(R.id.detail_name);
        status = findViewById(R.id.detail_status);
        method = findViewById(R.id.detail_method);
        ingredientContainer = findViewById(R.id.ingredient_container);

        long recipeId = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1);
        // An invalid Intent cannot display a recipe safely, so close the screen.
        if (recipeId < 0) {
            finish();
            return;
        }
        new PantryRepository(getApplicationContext()).observeRecipe(recipeId)
                .observe(this, this::showRecipe);
    }

    /** Renders the recipe header, match badge, requirements and preparation steps. */
    private void showRecipe(RecipeWithIngredients value) {
        if (value == null) return;
        name.setText(value.recipe.name);
        method.setText(value.recipe.method);
        String missing = getIntent().getStringExtra(EXTRA_MISSING);
        if (missing == null || missing.isEmpty()) {
            status.setText("Ready to cook");
            status.setBackgroundResource(R.drawable.bg_badge_green);
            status.setTextColor(getColor(R.color.green_900));
        } else {
            status.setText("Almost there - missing: " + missing);
            status.setBackgroundResource(R.drawable.bg_badge_orange);
            status.setTextColor(getColor(R.color.danger));
        }

        // Rebuild the rows if LiveData supplies an updated recipe relation.
        ingredientContainer.removeAllViews();
        for (RecipeIngredient ingredient : value.ingredients) {
            TextView row = new TextView(this);
            row.setText("•  " + Formatters.quantity(ingredient.quantity) + " "
                    + ingredient.unit + " " + ingredient.name);
            row.setTextColor(getColor(R.color.charcoal));
            row.setTextSize(16);
            row.setPadding(0, 8, 0, 8);
            ingredientContainer.addView(row, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        }
    }
}
