package com.example.smartexpensetracker.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartexpensetracker.R;
import com.example.smartexpensetracker.models.Transaction;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactionList;

    public TransactionAdapter(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        
        holder.tvType.setText(transaction.getType().toUpperCase());
        holder.tvDate.setText(transaction.getDate());
        
        if (transaction.getType().equalsIgnoreCase("income")) {
            holder.tvAmount.setText(String.format(Locale.getDefault(), "+$%.2f", transaction.getAmount()));
            holder.tvAmount.setTextColor(Color.parseColor("#4CAF50")); // Green
            holder.ivIcon.setImageResource(android.R.drawable.ic_input_add);
        } else {
            holder.tvAmount.setText(String.format(Locale.getDefault(), "-$%.2f", transaction.getAmount()));
            holder.tvAmount.setTextColor(Color.parseColor("#F44336")); // Red
            holder.ivIcon.setImageResource(android.R.drawable.ic_delete);
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvDate, tvAmount;
        ImageView ivIcon;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tvTransactionType);
            tvDate = itemView.findViewById(R.id.tvTransactionDate);
            tvAmount = itemView.findViewById(R.id.tvTransactionAmount);
            ivIcon = itemView.findViewById(R.id.ivTransactionIcon);
        }
    }
}
