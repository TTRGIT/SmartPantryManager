package com.smartpantry.manager.ui;

import android.content.Context;
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

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.smartpantry.manager.R;
import com.smartpantry.manager.model.PantryItem;

/** Displays live pantry data and routes add, edit and delete user actions. */
public class PantryFragment extends Fragment implements PantryAdapter.Listener {
    private PantryAdapter adapter;
    private PantryViewModel viewModel;
    private TextView emptyView;

    /** Inflates the pantry list, empty state and add button layout. */
    @Nullable
    /* Connects RecyclerView, ViewModel observations and the add-ingredient Intent. */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pantry, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(PantryViewModel.class);
        adapter = new PantryAdapter(this);
        emptyView = view.findViewById(R.id.empty_pantry);
        RecyclerView recycler = view.findViewById(R.id.pantry_recycler);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler.setAdapter(adapter);

        // Room invalidates this LiveData after every insert, update or delete.
        viewModel.pantry().observe(getViewLifecycleOwner(), items -> {
            adapter.submitList(items);
            boolean empty = items == null || items.isEmpty();
            emptyView.setVisibility(empty ? View.VISIBLE : View.GONE);
            recycler.setVisibility(empty ? View.GONE : View.VISIBLE);
        });

        ExtendedFloatingActionButton fab = view.findViewById(R.id.add_ingredient_fab);
        fab.setOnClickListener(v -> startActivity(new Intent(requireContext(), AddEditIngredientActivity.class)));
    }

    /** Refreshes the expiry badges whenever the user returns from Settings. */
    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            boolean enabled = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
                    .getBoolean("expiry_alerts", true);
            adapter.setShowExpiryAlerts(enabled);
        }
    }

    /** Opens the same form in edit mode and passes the selected item through Intent extras. */
    @Override
    public void onEdit(PantryItem item) {
        Intent intent = new Intent(requireContext(), AddEditIngredientActivity.class);
        intent.putExtra(AddEditIngredientActivity.EXTRA_ID, item.id);
        intent.putExtra(AddEditIngredientActivity.EXTRA_NAME, item.name);
        intent.putExtra(AddEditIngredientActivity.EXTRA_QUANTITY, item.quantity);
        intent.putExtra(AddEditIngredientActivity.EXTRA_UNIT, item.unit);
        intent.putExtra(AddEditIngredientActivity.EXTRA_EXPIRY, item.expiryDate);
        startActivity(intent);
    }

    /** Requests confirmation before forwarding a destructive delete to the ViewModel. */
    @Override
    public void onDelete(PantryItem item) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete " + item.name + "?")
                .setMessage("This removes the ingredient from your pantry and may change recipe suggestions.")
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) -> viewModel.delete(item))
                .show();
    }
}
