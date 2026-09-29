package com.smartpantry.manager.util;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/** Normalizes common spelling, punctuation and singular/plural variations. */
public final class IngredientNormalizer {
    // Aliases map different everyday names to one canonical ingredient name.
    private static final Map<String, String> ALIASES = new HashMap<>();

    static {
        // These pairs handle common local and international naming differences.
        ALIASES.put("capsicum", "bell pepper");
        ALIASES.put("green pepper", "bell pepper");
        ALIASES.put("chilli", "chili");
        ALIASES.put("green onion", "spring onion");
        ALIASES.put("scallion", "spring onion");
        ALIASES.put("garbanzo bean", "chickpea");
        ALIASES.put("garbanzo", "chickpea");
        ALIASES.put("vegetable oil", "oil");
        ALIASES.put("cooking oil", "oil");
    }

    private IngredientNormalizer() { }

    /**
     * Produces a lowercase canonical name used by pantry rows and recipe requirements.
     * Accents, punctuation, extra whitespace, aliases and simple plurals are handled here.
     */
    public static String normalize(String raw) {
        if (raw == null) return "";
        String value = Normalizer.normalize(raw, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replace('&', ' ')
                .replaceAll("[^a-z0-9 ]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        // Prefer an exact whole-phrase alias before changing the final word.
        String alias = ALIASES.get(value);
        if (alias != null) return alias;

        // Ingredient phrases are pluralized on their final word (for example kidney beans).
        String[] words = value.split(" ");
        if (words.length > 0) {
            words[words.length - 1] = singularize(words[words.length - 1]);
            value = String.join(" ", words);
        }
        return ALIASES.getOrDefault(value, value);
    }

    /** Applies small food-focused singular rules instead of aggressive word stemming. */
    private static String singularize(String word) {
        if (word.equals("tomatoes") || word.equals("potatoes")) {
            return word.substring(0, word.length() - 2);
        }
        if (word.equals("leaves")) return "leaf";
        if (word.equals("knives")) return "knife";
        if (word.endsWith("ies") && word.length() > 4) {
            return word.substring(0, word.length() - 3) + "y";
        }
        // These food words end in 's' but are already singular/uncountable.
        if (word.equals("cheese") || word.equals("couscous") || word.equals("molasses")
                || word.equals("hummus")) return word;
        if (word.endsWith("s") && !word.endsWith("ss") && word.length() > 3) {
            return word.substring(0, word.length() - 1);
        }
        return word;
    }
}
