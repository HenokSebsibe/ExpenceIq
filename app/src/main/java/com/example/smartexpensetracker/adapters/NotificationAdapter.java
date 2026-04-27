package com.example.smartexpensetracker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartexpensetracker.R;
import com.example.smartexpensetracker.models.AppNotification;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final List<AppNotification> notifications;

    public NotificationAdapter(List<AppNotification> notifications) {
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppNotification notification = notifications.get(position);
        
        String title = notification.getType().toUpperCase();
        holder.tvTitle.setText(title);
        holder.tvMessage.setText(notification.getMessage());
        holder.tvDate.setText(notification.getDate());

        int color;
        int iconRes;
        switch (notification.getType().toLowerCase()) {
            case "success":
                color = ContextCompat.getColor(holder.itemView.getContext(), R.color.income_green);
                iconRes = android.R.drawable.ic_dialog_info; // You can use a better icon if available
                break;
            case "warning":
                color = ContextCompat.getColor(holder.itemView.getContext(), R.color.expense_red);
                iconRes = android.R.drawable.stat_sys_warning;
                break;
            default:
                color = ContextCompat.getColor(holder.itemView.getContext(), R.color.water_blue);
                iconRes = android.R.drawable.ic_popup_reminder;
                break;
        }
        
        holder.ivIcon.setColorFilter(color);
        holder.ivIcon.setImageResource(iconRes);
        
        // Indicate if read/unread
        if (notification.getIsRead() == 0) {
            holder.itemView.setAlpha(1.0f);
            holder.tvTitle.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_primary_light));
        } else {
            holder.itemView.setAlpha(0.6f);
        }
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMessage, tvDate;
        ImageView ivIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvNotificationTitle);
            tvMessage = itemView.findViewById(R.id.tvNotificationMessage);
            tvDate = itemView.findViewById(R.id.tvNotificationDate);
            ivIcon = itemView.findViewById(R.id.ivNotificationIcon);
        }
    }
}
