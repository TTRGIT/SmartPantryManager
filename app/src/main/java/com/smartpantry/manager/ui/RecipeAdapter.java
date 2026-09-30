package com.smartpantry.manager.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smartpantry.manager.R;
import com.smartpantry.manager.model.RecipeIngredient;
import com.smartpantry.manager.model.RecipeMatch;

import java.util.ArrayList;
import java.util.List;

/** Custom adapter used for both the strict and Almost There recipe lists. */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.Holder> {
    /** Lets the fragment own navigation when a recipe card is selected. */
    public interface Listener { void onRecipeClick(RecipeMatch match); }

    private final Listener listener;
    private final List<RecipeMatch> items = new ArrayList<>();

    /** Stores the click listener supplied by RecipeSuggestionsFragment. */
    public RecipeAdapter(Listener listener) {
        this.listener = listener;
    }

    /** Replaces the displayed match results after pantry data changes. */
    public void submitList(List<RecipeMatch> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged();
    }

    /** Inflates a reusable recipe card. */
    @NonNull
    /** Shows recipe name, ingredient summary, and ready/missing status. */
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        RecipeMatch match = items.get(position);
        holder.name.setText(match.recipe.recipe.name);
        // Build a compact dot-separated ingredient preview for the card.
        StringBuilder names = new StringBuilder();
        for (RecipeIngredient ingredient : match.recipe.ingredients) {
            if (names.length() > 0) names.append(" • ");
            names.append(ingredient.name);
        }
        holder.summary.setText(names);
        // Colour and wording make the strict and Almost There categories unmistakable.
        if (match.isReady()) {
            holder.status.setText("Everything is in your pantry");
            holder.status.setBackgroundResource(R.drawable.bg_badge_green);
            holder.status.setTextColor(holder.itemView.getContext().getColor(R.color.green_900));
        } else {
            holder.status.setText("Missing: " + String.join(", ", match.missingIngredients));
            holder.status.setBackgroundResource(R.drawable.bg_badge_orange);
            holder.status.setTextColor(holder.itemView.getContext().getColor(R.color.danger));
        }
        holder.itemView.setOnClickListener(v -> listener.onRecipeClick(match));
    }

    /** Reports the number of cards RecyclerView should display. */
    @Override
    public int getItemCount() { return items.size(); }

    /** Caches card views for smooth RecyclerView scrolling. */
    static class Holder extends RecyclerView.ViewHolder {
        final TextView name, summary, status;

        Holder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.recipe_name);
            summary = itemView.findViewById(R.id.recipe_summary);
            status = itemView.findViewById(R.id.recipe_status);
        }
    }
}
