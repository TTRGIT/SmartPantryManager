package com.smartpantry.manager.ui;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.smartpantry.manager.R;
import com.smartpantry.manager.data.PantryRepository;
import com.smartpantry.manager.model.PantryItem;
import com.smartpantry.manager.util.IngredientNormalizer;
import com.smartpantry.manager.util.SystemBarHelper;

import java.time.LocalDate;
import java.util.Locale;

/** Add/edit form. Existing values arrive via Intent extras from the pantry adapter. */
public class AddEditIngredientActivity extends AppCompatActivity {
    // Intent keys let PantryFragment pass an existing row into this reusable form.
    public static final String EXTRA_ID = "ingredient_id";
    public static final String EXTRA_NAME = "ingredient_name";
    public static final String EXTRA_QUANTITY = "ingredient_quantity";
    public static final String EXTRA_UNIT = "ingredient_unit";
    public static final String EXTRA_EXPIRY = "ingredient_expiry";

    // Input controls and their layouts are kept separately so validation errors can be shown.
    private TextInputEditText nameInput, quantityInput, expiryInput;
    private AutoCompleteTextView unitInput;
    private TextInputLayout nameLayout, quantityLayout, unitLayout;
    private PantryRepository repository;
    // A negative ID means add mode; a valid database ID means edit mode.
    private long itemId = -1;

    /** Initializes controls and decides whether the activity is adding or editing. */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);
        repository = new PantryRepository(getApplicationContext());
        bindViews();
        configureToolbar();
        configureUnits();
        configureDatePicker();
        populateForEdit();
        findViewById(R.id.save_button).setOnClickListener(v -> save());
    }

    /** Finds each XML view once and stores it for later form operations. */
    private void bindViews() {
        nameInput = findViewById(R.id.name_input);
        quantityInput = findViewById(R.id.quantity_input);
        unitInput = findViewById(R.id.unit_input);
        expiryInput = findViewById(R.id.expiry_input);
        nameLayout = findViewById(R.id.name_layout);
        quantityLayout = findViewById(R.id.quantity_layout);
        unitLayout = findViewById(R.id.unit_layout);
    }

    /** Configures back navigation and an add/edit title based on the Intent ID. */
    private void configureToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.form_toolbar);
        // Reserve the phone status-bar area above the form title and back button.
        SystemBarHelper.applyTopInset(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(v -> finish());
        itemId = getIntent().getLongExtra(EXTRA_ID, -1);
        toolbar.setTitle(itemId < 0 ? R.string.add_ingredient : R.string.edit_ingredient);
    }

    /** Supplies the supported units to the exposed dropdown field. */
    private void configureUnits() {
        String[] units = {"piece", "slice", "g", "kg", "ml", "L", "tsp", "tbsp", "cup", "can"};
        unitInput.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, units));
    }

    /** Opens a date picker and stores the selected date in SQLite-friendly ISO format. */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void configureDatePicker() {
        expiryInput.setOnClickListener(v -> {
            LocalDate initial;
            try { initial = LocalDate.parse(text(expiryInput)); }
            catch (RuntimeException ignored) { initial = LocalDate.now(); }
            new DatePickerDialog(this, (picker, year, month, day) ->
                    expiryInput.setText(String.format(Locale.ROOT, "%04d-%02d-%02d", year, month + 1, day)),
                    initial.getYear(), initial.getMonthValue() - 1, initial.getDayOfMonth()).show();
        });
    }

    /** Copies values supplied by the pantry adapter into the form when editing. */
    private void populateForEdit() {
        if (itemId < 0) return;
        nameInput.setText(getIntent().getStringExtra(EXTRA_NAME));
        quantityInput.setText(String.valueOf(getIntent().getDoubleExtra(EXTRA_QUANTITY, 0)));
        unitInput.setText(getIntent().getStringExtra(EXTRA_UNIT), false);
        expiryInput.setText(getIntent().getStringExtra(EXTRA_EXPIRY));
    }

    /** Validates input, constructs a normalized entity, then inserts or updates it. */
    private void save() {
        clearErrors();
        String name = text(nameInput).trim();
        String unit = text(unitInput).trim();
        double quantity = -1;
        try { quantity = Double.parseDouble(text(quantityInput)); }
        catch (NumberFormatException ignored) { }

        // Collect all validation errors so the user can correct every field at once.
        boolean valid = true;
        if (name.length() < 2) {
            nameLayout.setError("Enter an ingredient name (at least 2 characters)");
            valid = false;
        }
        if (quantity <= 0) {
            quantityLayout.setError("Quantity must be greater than zero");
            valid = false;
        }
        if (unit.isEmpty()) {
            unitLayout.setError("Select a unit");
            valid = false;
        }
        if (!valid) return;

        String expiry = text(expiryInput).trim();
        // Save both display and canonical names; canonical names power robust matching.
        PantryItem item = new PantryItem(name, IngredientNormalizer.normalize(name), quantity,
                unit, expiry.isEmpty() ? null : expiry);
        if (itemId < 0) repository.insert(item);
        else {
            item.id = itemId;
            repository.update(item);
        }
        finish();
    }

    /** Removes previous validation messages before checking the form again. */
    private void clearErrors() {
        nameLayout.setError(null);
        quantityLayout.setError(null);
        unitLayout.setError(null);
    }

    /** Reads text safely even when an input has no Editable value. */
    private static String text(android.widget.TextView view) {
        return view.getText() == null ? "" : view.getText().toString();
    }
}
