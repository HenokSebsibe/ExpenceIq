package com.example.smartexpensetracker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartexpensetracker.R;
import com.example.smartexpensetracker.models.SavingGoal;
import java.util.List;
import java.util.Locale;

public class SavingGoalAdapter extends RecyclerView.Adapter<SavingGoalAdapter.ViewHolder> {

    private final List<SavingGoal> goals;
    private final OnGoalClickListener listener;

    public interface OnGoalClickListener {
        void onGoalClick(SavingGoal goal);
    }

    public SavingGoalAdapter(List<SavingGoal> goals, OnGoalClickListener listener) {
        this.goals = goals;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saving_goal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SavingGoal goal = goals.get(position);
        holder.tvName.setText(goal.getName());
        holder.tvAmount.setText(String.format(Locale.getDefault(), "$%.2f / $%.2f", goal.getSavedAmount(), goal.getTargetAmount()));
        holder.tvDeadline.setText("Due: " + goal.getDeadline());
        holder.tvPercent.setText(goal.getProgress() + "%");
        holder.pbProgress.setProgress(goal.getProgress());
        
        if (goal.getStatus() == 1) {
            holder.tvStatus.setText("Completed");
            holder.tvStatus.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.income_green));
        } else {
            holder.tvStatus.setText("Active");
            holder.tvStatus.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.gold));
        }

        holder.itemView.setOnClickListener(v -> listener.onGoalClick(goal));
    }

    @Override
    public int getItemCount() {
        return goals.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAmount, tvDeadline, tvPercent, tvStatus;
        ProgressBar pbProgress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvGoalName);
            tvAmount = itemView.findViewById(R.id.tvGoalAmount);
            tvDeadline = itemView.findViewById(R.id.tvDeadline);
            tvPercent = itemView.findViewById(R.id.tvProgressPercent);
            tvStatus = itemView.findViewById(R.id.tvGoalStatus);
            pbProgress = itemView.findViewById(R.id.pbGoalProgress);
        }
    }
}
