package com.smartpantry.manager.util;

import java.util.Locale;

/** Converts compatible mass, volume and count units to a shared base unit. */
public final class UnitConverter {
    private UnitConverter() { }

    /** Converts both amounts and reports whether the available amount meets the requirement. */
    public static boolean canSatisfy(
            double available,
            String availableUnit,
            double required,
            String requiredUnit
    ) {
        UnitValue have = toBase(available, availableUnit);
        UnitValue need = toBase(required, requiredUnit);
        return have.category.equals(need.category) && have.value + 0.000001 >= need.value;
    }

    /**
     * Converts an available amount into a recipe's required unit. A zero result means the
     * units belong to different categories and cannot safely be compared.
     */
    public static double valueInRequiredUnit(
            double available,
            String availableUnit,
            String requiredUnit
    ) {
        UnitValue have = toBase(available, availableUnit);
        UnitValue need = toBase(1, requiredUnit);
        if (!have.category.equals(need.category)) return 0;
        return have.value / need.value;
    }

    /** Converts mass to grams, volume to millilitres, and keeps counts/slices separate. */
    private static UnitValue toBase(double value, String rawUnit) {
        String unit = normalizeUnit(rawUnit);
        switch (unit) {
            case "kg": return new UnitValue("mass", value * 1000);
            case "mg": return new UnitValue("mass", value / 1000);
            case "g": return new UnitValue("mass", value);
            case "l": return new UnitValue("volume", value * 1000);
            case "tsp": return new UnitValue("volume", value * 5);
            case "tbsp": return new UnitValue("volume", value * 15);
            case "cup": return new UnitValue("volume", value * 250);
            case "ml": return new UnitValue("volume", value);
            case "piece": return new UnitValue("count", value);
            case "slice": return new UnitValue("slice", value);
            default: return new UnitValue("custom:" + unit, value);
        }
    }

    /** Converts spelling and plural variations such as litres/L and pieces/pcs. */
    public static String normalizeUnit(String raw) {
        String unit = raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT);
        switch (unit) {
            case "grams": case "gram": return "g";
            case "kilograms": case "kilogram": case "kgs": return "kg";
            case "milligrams": case "milligram": return "mg";
            case "millilitres": case "millilitre": case "milliliters": case "milliliter": return "ml";
            case "litres": case "litre": case "liters": case "liter": return "l";
            case "teaspoons": case "teaspoon": return "tsp";
            case "tablespoons": case "tablespoon": return "tbsp";
            case "cups": return "cup";
            case "pieces": case "pcs": case "pc": case "items": case "item":
            case "whole": case "unit": case "units": case "can": case "cans": return "piece";
            case "slices": return "slice";
            default: return unit;
        }
    }

    /** Holds a converted number together with the category that may be compared safely. */
    private static class UnitValue {
        final String category;
        final double value;

        UnitValue(String category, double value) {
            this.category = category;
            this.value = value;
        }
    }
}
