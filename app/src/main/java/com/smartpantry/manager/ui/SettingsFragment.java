package com.smartpantry.manager.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.smartpantry.manager.R;

/** Lightweight persistent preferences screen. */
public class SettingsFragment extends Fragment {
    /** Inflates the two persistent preference controls and privacy explanation. */
    @Nullable
    /** Loads saved values and writes every change to SharedPreferences immediately. */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        SwitchMaterial alerts = view.findViewById(R.id.expiry_switch);
        RadioGroup units = view.findViewById(R.id.unit_group);

        // Defaults provide useful expiry warnings and familiar metric units on first launch.
        alerts.setChecked(prefs.getBoolean("expiry_alerts", true));
        String preferred = prefs.getString("unit_preference", "metric");
        units.check("kitchen".equals(preferred) ? R.id.kitchen_radio : R.id.metric_radio);

        alerts.setOnCheckedChangeListener((button, checked) ->
                prefs.edit().putBoolean("expiry_alerts", checked).apply());
        units.setOnCheckedChangeListener((group, id) -> prefs.edit()
                .putString("unit_preference", id == R.id.kitchen_radio ? "kitchen" : "metric")
                .apply());
    }
}
