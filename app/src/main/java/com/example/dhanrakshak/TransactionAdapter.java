package com.example.dhanrakshak;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
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

        // Category / Title
        String category = transaction.getCategory();
        if (category == null || category.isEmpty())
            category = "General";
        holder.category.setText(category);

        // Amount with sign
        holder.amount.setText("₹" + transaction.getAmount());

        // Date
        holder.date.setText(transaction.getFormattedDate());

        // Type label
        holder.type.setText(transaction.getType());

        // Color based on type
        if ("Income".equals(transaction.getType())) {
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.income_green));
            holder.transactionIcon.setImageResource(R.drawable.ic_add_income_action);
        } else {
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.expense_red));
            holder.transactionIcon.setImageResource(R.drawable.ic_add_expense);
        }

        // Description / Notes
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
        ImageView transactionIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.transactionCategory);
            amount = itemView.findViewById(R.id.transactionAmount);
            date = itemView.findViewById(R.id.transactionDate);
            type = itemView.findViewById(R.id.transactionType);
            description = itemView.findViewById(R.id.transactionDescription);
            transactionIcon = itemView.findViewById(R.id.transactionIcon);
        }
    }
}
