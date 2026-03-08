package com.example.dhanrakshak;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.ViewHolder> {

    private Context context;
    private List<GoalModel> goalList;

    public GoalAdapter(Context context, List<GoalModel> goalList) {
        this.context = context;
        this.goalList = goalList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_goal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GoalModel goal = goalList.get(position);
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

        holder.tvGoalName.setText(goal.getName());
        holder.tvGoalTarget.setText(String.format("Target: ₹%.0f", goal.getTargetAmount()));
        holder.tvGoalSaved.setText(String.format("₹%.0f", goal.getSavedAmount()));
        holder.tvGoalRemaining.setText(String.format("₹%.0f", goal.getRemainingAmount()));
        holder.goalProgressBar.setProgress(goal.getProgressPercent());
    }

    @Override
    public int getItemCount() {
        return goalList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvGoalName, tvGoalTarget, tvGoalSaved, tvGoalRemaining;
        ProgressBar goalProgressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            tvGoalName = itemView.findViewById(R.id.tvGoalName);
            tvGoalTarget = itemView.findViewById(R.id.tvGoalTarget);
            tvGoalSaved = itemView.findViewById(R.id.tvGoalSaved);
            tvGoalRemaining = itemView.findViewById(R.id.tvGoalRemaining);
            goalProgressBar = itemView.findViewById(R.id.goalProgressBar);
        }
    }
}
