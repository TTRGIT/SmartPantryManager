package com.smartpantry.manager.util;

import android.view.View;
import android.view.ViewGroup;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/** Keeps app bars clear of the phone's status bar, camera cutout and display notch. */
public final class SystemBarHelper {
    private SystemBarHelper() { }

    /**
     * Adds the status-bar inset above a toolbar's original content area. The original
     * measurements are captured once so repeated inset events cannot accumulate padding.
     */
    public static void applyTopInset(View toolbar) {
        final int originalHeight = toolbar.getLayoutParams().height;
        final int originalLeft = toolbar.getPaddingLeft();
        final int originalTop = toolbar.getPaddingTop();
        final int originalRight = toolbar.getPaddingRight();
        final int originalBottom = toolbar.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(toolbar, (view, windowInsets) -> {
            // statusBars also accounts for the safe area needed by display cutouts.
            Insets statusBars = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars());
            view.setPadding(originalLeft, originalTop + statusBars.top,
                    originalRight, originalBottom);

            // Preserve the normal toolbar height, then add space occupied by the phone bar.
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.height = originalHeight + statusBars.top;
            view.setLayoutParams(params);
            return windowInsets;
        });

        // Requests the first inset calculation after the toolbar is attached to its window.
        ViewCompat.requestApplyInsets(toolbar);
    }
}
