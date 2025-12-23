package com.example.dhanrakshak;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private Context context;
    private List<TransactionModel> transactionList;

    public TransactionAdapter(Context context, List<TransactionModel> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.transaction_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TransactionModel transaction = transactionList.get(position);

        holder.category.setText("Category: " + transaction.getCategory());
        holder.amount.setText("₹" + transaction.getAmount());
        holder.date.setText("Date: " + transaction.getFormattedDate());
        holder.type.setText("Type: " + transaction.getType());

        // Set amount text color based on type
        if (transaction.getType().equals("Income")) {
            holder.amount.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.amount.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        }

        // Show/hide description
        String notes = transaction.getNotes();
        if (notes != null && !notes.trim().isEmpty()) {
            holder.description.setText(notes);
            holder.description.setVisibility(View.VISIBLE);
        } else {
            holder.description.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView category, amount, date, type, description;

        public ViewHolder(View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.transactionCategory);
            amount = itemView.findViewById(R.id.transactionAmount);
            date = itemView.findViewById(R.id.transactionDate);
            type = itemView.findViewById(R.id.transactionType);
            description = itemView.findViewById(R.id.transactionDescription); // ✅ Fixed
        }
    }
}
