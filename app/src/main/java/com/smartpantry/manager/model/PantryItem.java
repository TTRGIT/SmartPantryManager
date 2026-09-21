package com.smartpantry.manager.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/** *@entity tells Room to create a SQLite table
 * While *@Index("normalizedName") makes repeated matching lookups more efficient.*/
@Entity(tableName = "pantry_items", indices = {@Index("normalizedName")})

/* Implementation of PantryItem constructor that accepts user data */
public class PantryItem {
    /** Unique database key generated automatically by Room. */
    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull public String name; // ingredient name.
    @NonNull public String normalizedName; // Search-friendly name used by the matcher.
    public double quantity; // Amount currently available.
    @NonNull public String unit; // Unit selected in the add/edit form.

    /** date (yyyy-MM-dd), or null when the user did not supply one. */
    @Nullable public String expiryDate;

    /** Creates an in-memory item; Room then supplies the ID when it is inserted. */
    public PantryItem(@NonNull String name, @NonNull String normalizedName, double quantity,
                      @NonNull String unit, @Nullable String expiryDate) {
        this.name = name;
        this.normalizedName = normalizedName;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }
}