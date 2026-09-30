package com.smartpantry.manager.ui;

import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.appbar.MaterialToolbar;
import com.smartpantry.manager.R;
import com.smartpantry.manager.util.SystemBarHelper;

/** Single host activity for the three primary app destinations. */
public class MainActivity extends AppCompatActivity {
    // The shared toolbar title changes with the selected bottom-navigation destination.
    private MaterialToolbar toolbar;

    /** Creates the host layout and connects each navigation item to its fragment. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        // Move the app bar below the phone's status icons and any display cutout.
        SystemBarHelper.applyTopInset(toolbar);
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        // Replace only the content area; the toolbar and bottom bar remain visible.
        navigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_pantry) {
                show(new PantryFragment(), getString(R.string.pantry));
            } else if (id == R.id.nav_recipes) {
                show(new RecipeSuggestionsFragment(), getString(R.string.recipes));
            } else if (id == R.id.nav_settings) {
                show(new SettingsFragment(), getString(R.string.settings));
            } else {
                return false;
            }
            return true;
        });

        // Select the pantry only on first creation so Android can restore later state.
        if (savedInstanceState == null) navigation.setSelectedItemId(R.id.nav_pantry);
    }

    /** Displays a destination fragment and gives the shared toolbar the matching title. */
    private void show(Fragment fragment, String title) {
        toolbar.setTitle(title);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}