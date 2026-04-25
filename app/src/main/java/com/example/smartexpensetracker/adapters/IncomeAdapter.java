package com.example.smartexpensetracker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartexpensetracker.R;
import com.example.smartexpensetracker.models.Income;
import java.util.List;
import java.util.Locale;

public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder> {

    private List<Income> incomeList;
    private OnIncomeClickListener listener;

    public interface OnIncomeClickListener {
        void onEditClick(Income income);
        void onDeleteClick(Income income);
    }

    public IncomeAdapter(List<Income> incomeList, OnIncomeClickListener listener) {
        this.incomeList = incomeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_income, parent, false);
        return new IncomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IncomeViewHolder holder, int position) {
        Income income = incomeList.get(position);
        holder.tvSource.setText(income.getSource());
        holder.tvDate.setText(income.getDate());
        holder.tvAmount.setText(String.format(Locale.getDefault(), "+$%.2f", income.getAmount()));
        
        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(income));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(income));
    }

    @Override
    public int getItemCount() {
        return incomeList.size();
    }

    public static class IncomeViewHolder extends RecyclerView.ViewHolder {
        TextView tvSource, tvDate, tvAmount;
        ImageButton btnEdit, btnDelete;

        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSource = itemView.findViewById(R.id.tvIncomeSource);
            tvDate = itemView.findViewById(R.id.tvIncomeDate);
            tvAmount = itemView.findViewById(R.id.tvIncomeAmount);
            btnEdit = itemView.findViewById(R.id.btnEditIncome);
            btnDelete = itemView.findViewById(R.id.btnDeleteIncome);
        }
    }
}
