package com.smartpantry.manager.ui;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.smartpantry.manager.R;
import com.smartpantry.manager.model.PantryItem;
import com.smartpantry.manager.util.Formatters;
import com.smartpantry.manager.util.RecipeMatcher;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** Converts database pantry rows into reusable RecyclerView cards. */
public class PantryAdapter extends ListAdapter<PantryItem, PantryAdapter.Holder> {
    /** Sends row actions back to the fragment instead of accessing navigation/database here. */
    public interface Listener {
        void onEdit(PantryItem item);
        void onDelete(PantryItem item);
    }

    private final Listener listener;
    private boolean showExpiryAlerts = true;

    /** Creates a ListAdapter that uses DiffUtil for efficient list changes. */
    public PantryAdapter(Listener listener) {
        super(DIFF);
        this.listener = listener;
    }

    /** Enables or hides all expiring-soon badges using the stored preference. */
    public void setShowExpiryAlerts(boolean show) {
        showExpiryAlerts = show;
        notifyDataSetChanged();
    }

    /** Inflates one pantry card when RecyclerView needs a new reusable holder. */
    @NonNull
    /** Binds a pantry entity and click handlers to an existing row holder. */
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pantry, parent, false);
        return new Holder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        PantryItem item = getItem(position);
        holder.name.setText(item.name);
        holder.quantity.setText(Formatters.quantity(item.quantity) + " " + item.unit);
        bindExpiry(holder.expiry, item.expiryDate);
        holder.itemView.setOnClickListener(v -> listener.onEdit(item));
        holder.edit.setOnClickListener(v -> listener.onEdit(item));
        holder.delete.setOnClickListener(v -> listener.onDelete(item));
    }

    /** Shows a badge only for expired items or items expiring within three days. */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void bindExpiry(TextView view, String date) {
        view.setVisibility(View.GONE);
        if (!showExpiryAlerts || date == null || date.isEmpty()) return;
        try {
            LocalDate expiry = LocalDate.parse(date);
            long days = ChronoUnit.DAYS.between(LocalDate.now(), expiry);
            if (days < 0) {
                view.setText("Expired " + date);
                view.setVisibility(View.VISIBLE);
            } else if (days <= 3) {
                view.setText(days == 0 ? "Expires today" : "Expires in " + days + " day" + (days == 1 ? "" : "s"));
                view.setVisibility(View.VISIBLE);
            }
        } catch (RuntimeException ignored) {
            // Dates saved by the form are ISO formatted; ignore legacy malformed values safely.
        }
    }

    /** Caches child view references so scrolling does not repeatedly call findViewById. */
    static class Holder extends RecyclerView.ViewHolder {
        final TextView name, quantity, expiry;
        final Button edit, delete;

        Holder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_name);
            quantity = itemView.findViewById(R.id.item_quantity);
            expiry = itemView.findViewById(R.id.item_expiry);
            edit = itemView.findViewById(R.id.edit_button);
            delete = itemView.findViewById(R.id.delete_button);
        }
    }

    // DiffUtil updates only rows whose ID or displayed content actually changed.
    private static final DiffUtil.ItemCallback<PantryItem> DIFF = new DiffUtil.ItemCallback<PantryItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull PantryItem oldItem, @NonNull PantryItem newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull PantryItem oldItem, @NonNull PantryItem newItem) {
            return oldItem.name.equals(newItem.name) && oldItem.quantity == newItem.quantity
                    && oldItem.unit.equals(newItem.unit)
                    && java.util.Objects.equals(oldItem.expiryDate, newItem.expiryDate);
        }
    };
}
