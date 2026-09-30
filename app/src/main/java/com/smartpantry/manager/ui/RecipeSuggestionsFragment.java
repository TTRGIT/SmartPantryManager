package com.smartpantry.manager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smartpantry.manager.R;
import com.smartpantry.manager.model.PantryItem;
import com.smartpantry.manager.model.RecipeMatch;
import com.smartpantry.manager.model.RecipeWithIngredients;
import com.smartpantry.manager.util.RecipeMatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Compares live pantry data with recipes and displays two strictly separated result lists. */
public class RecipeSuggestionsFragment extends Fragment implements RecipeAdapter.Listener {
    // Separate adapters ensure Almost There results can never leak into the ready list.
    private RecipeAdapter readyAdapter, almostAdapter;
    private TextView noSuggestions, noAlmost;
    private List<PantryItem> pantry = Collections.emptyList();
    private List<RecipeWithIngredients> recipes = Collections.emptyList();

    /** Inflates both recipe sections and their independent empty messages. */
    @Nullable
    /** Sets up both lists and observes the two data sources needed for matching. */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        readyAdapter = new RecipeAdapter(this);
        almostAdapter = new RecipeAdapter(this);
        noSuggestions = view.findViewById(R.id.no_suggestions);
        noAlmost = view.findViewById(R.id.no_almost);

        RecyclerView ready = view.findViewById(R.id.suggested_recycler);
        RecyclerView almost = view.findViewById(R.id.almost_recycler);
        ready.setLayoutManager(new LinearLayoutManager(requireContext()));
        almost.setLayoutManager(new LinearLayoutManager(requireContext()));
        ready.setAdapter(readyAdapter);
        almost.setAdapter(almostAdapter);

        PantryViewModel model = new ViewModelProvider(requireActivity()).get(PantryViewModel.class);
        // Recalculate when CRUD operations change the user's pantry.
        model.pantry().observe(getViewLifecycleOwner(), value -> {
            pantry = value == null ? Collections.emptyList() : value;
            refreshMatches();
        });
        // Recalculate again when the first-run recipe seed becomes available.
        model.recipes().observe(getViewLifecycleOwner(), value -> {
            recipes = value == null ? Collections.emptyList() : value;
            refreshMatches();
        });
    }

    /** Applies the strict rules and sends each result to exactly one visible category. */
    private void refreshMatches() {
        List<RecipeMatch> ready = new ArrayList<>();
        List<RecipeMatch> almost = new ArrayList<>();
        for (RecipeMatch match : RecipeMatcher.match(recipes, pantry)) {
            if (match.missingIngredients.isEmpty()) ready.add(match);
            else if (match.missingIngredients.size() == 1) almost.add(match);
            // Recipes missing two or more ingredients are intentionally not displayed.
        }
        readyAdapter.submitList(ready);
        almostAdapter.submitList(almost);
        noSuggestions.setVisibility(ready.isEmpty() ? View.VISIBLE : View.GONE);
        noAlmost.setVisibility(almost.isEmpty() ? View.VISIBLE : View.GONE);
    }

    /** Opens recipe details and passes both the recipe ID and current match status. */
    @Override
    public void onRecipeClick(RecipeMatch match) {
        Intent intent = new Intent(requireContext(), RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, match.recipe.recipe.id);
        intent.putExtra(RecipeDetailActivity.EXTRA_MISSING,
                match.missingIngredients.isEmpty() ? "" : String.join(", ", match.missingIngredients));
        startActivity(intent);
    }
}
