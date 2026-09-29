package com.smartpantry.manager.util;

import java.text.DecimalFormat;
public final class Formatters {
    // Displays useful decimals without trailing values such as "2.00".
    private static final DecimalFormat QUANTITY = new DecimalFormat("0.##");

    private Formatters() { }

    /** Produces a short user-facing quantity string. */
    public static String quantity(double value) {
        return QUANTITY.format(value);
    }
}
