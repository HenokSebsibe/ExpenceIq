package com.example.smartexpensetracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartexpensetracker.R;
import com.example.smartexpensetracker.models.SavingGoal;
import com.google.android.material.button.MaterialButton;
import java.util.List;
import java.util.Locale;

public class SavingGoalAdapter extends RecyclerView.Adapter<SavingGoalAdapter.ViewHolder> {

    private final List<SavingGoal> goals;
    private final OnGoalClickListener listener;

    public interface OnGoalClickListener {
        void onGoalClick(SavingGoal goal);
        void onUpdateAmountClick(SavingGoal goal);
        void onAddContributionClick(SavingGoal goal);
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
        Context context = holder.itemView.getContext();
        
        holder.tvName.setText(goal.getName());
        holder.tvAmount.setText(String.format(Locale.getDefault(), "$%.2f / $%.2f", goal.getSavedAmount(), goal.getTargetAmount()));
        holder.tvDeadline.setText("Due: " + goal.getDeadline());
        
        int progress = goal.getProgress();
        holder.tvPercent.setText(progress + "%");
        holder.pbProgress.setProgress(progress);
        
        // Progress based colors
        if (progress >= 100) {
            holder.tvStatus.setText("Completed!");
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.income_green));
            holder.btnAddContribution.setVisibility(View.GONE);
        } else if (progress >= 80) {
            holder.tvStatus.setText("Almost there!");
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.royal_gold));
            holder.btnAddContribution.setVisibility(View.VISIBLE);
        } else {
            holder.tvStatus.setText("Active");
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.night_platinum));
            holder.btnAddContribution.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(v -> listener.onGoalClick(goal));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onUpdateAmountClick(goal);
            return true;
        });

        holder.btnAddContribution.setOnClickListener(v -> listener.onAddContributionClick(goal));
    }

    @Override
    public int getItemCount() {
        return goals.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAmount, tvDeadline, tvPercent, tvStatus;
        ProgressBar pbProgress;
        MaterialButton btnAddContribution;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvGoalName);
            tvAmount = itemView.findViewById(R.id.tvGoalAmount);
            tvDeadline = itemView.findViewById(R.id.tvDeadline);
            tvPercent = itemView.findViewById(R.id.tvProgressPercent);
            tvStatus = itemView.findViewById(R.id.tvGoalStatus);
            pbProgress = itemView.findViewById(R.id.pbGoalProgress);
            btnAddContribution = itemView.findViewById(R.id.btnAddContribution);
        }
    }
}
