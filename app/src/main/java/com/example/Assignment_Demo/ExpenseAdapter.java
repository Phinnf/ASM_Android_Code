package com.example.Assignment_Demo;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Locale; // Import Locale

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private Context mContext;
    public Cursor mCursor; // Made public for easier closing in Activity

    public ExpenseAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }

    public class ExpenseViewHolder extends RecyclerView.ViewHolder {
        public TextView descriptionText, amountText, categoryText, dateText;
        public ImageView iconImage; // 1. Declare the ImageView

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            descriptionText = itemView.findViewById(R.id.tvItemDescription);
            amountText = itemView.findViewById(R.id.tvItemAmount);
            categoryText = itemView.findViewById(R.id.tvItemCategory);
            dateText = itemView.findViewById(R.id.tvItemDate);

            // 2. Bind the ImageView ID from your XML
            iconImage = itemView.findViewById(R.id.imgIcon);
        }
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.expense_item_layout, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        if (mCursor == null || !mCursor.moveToPosition(position)) {
            return;
        }

        // Get data from Cursor
        String description = mCursor.getString(mCursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXPENSE_DESC));
        double amount = mCursor.getDouble(mCursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXPENSE_AMOUNT));
        String category = mCursor.getString(mCursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXPENSE_CATEGORY));
        String date = mCursor.getString(mCursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXPENSE_DATE));
        long id = mCursor.getLong(mCursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXPENSE_ID));

        // Set Text Data
        holder.descriptionText.setText(description);
        holder.amountText.setText(String.format(Locale.US, "%.0f$", amount));
        holder.categoryText.setText(category);
        holder.dateText.setText(date);

        // 3. SET THE ICON DYNAMICALLY
        // Call the helper method we created in Step 2
        holder.iconImage.setImageResource(DatabaseHelper.getCategoryIcon(category));

        // Optional: Change icon color based on category for better visuals
        setCategoryColor(holder.iconImage, category);
        holder.itemView.setTag(id);

        // Bind data to ViewHolder
        holder.descriptionText.setText(description);
        holder.amountText.setText(String.format(Locale.US, "-%.0f$", amount));
        holder.categoryText.setText("Category: " + category);
        holder.dateText.setText(date);

        // Save ID to tag for processing (e.g., delete)
        holder.itemView.setTag(id);

    }

    @Override
    public int getItemCount() {
        return (mCursor == null) ? 0 : mCursor.getCount();
    }

    // This function is used to update the RecyclerView when new data is available
    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = newCursor;
        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }
    private void setCategoryColor(ImageView img, String category) {
        int color;
        switch (category) {
            case "Food": color = 0xFFFF5722; break;      // Orange
            case "Transportation": color = 0xFF2196F3; break; // Blue
            case "Rent": color = 0xFF9C27B0; break;      // Purple
            case "Education": color = 0xFF4CAF50; break; // Green
            case "Entertainment": color = 0xFFFFC107; break; // Amber
            default: color = 0xFF607D8B; break;          // Grey
        }
        img.setColorFilter(color);
    }
}
