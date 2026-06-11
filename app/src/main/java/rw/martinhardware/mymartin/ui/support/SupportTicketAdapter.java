package rw.martinhardware.mymartin.ui.support;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import rw.martinhardware.mymartin.R;
import rw.martinhardware.mymartin.models.Ticket;

public class SupportTicketAdapter extends RecyclerView.Adapter<SupportTicketAdapter.ViewHolder> {

    private static final int[] CATEGORY_COLORS = {
        0xFFE53935, 0xFFFB8C00, 0xFF43A047, 0xFF1E88E5, 0xFF8E24AA,
        0xFF00ACC1, 0xFFF4511E, 0xFF3949AB
    };

    private List<Ticket> tickets = new ArrayList<>();
    private OnTicketClickListener listener;

    public interface OnTicketClickListener {
        void onTicketClick(Ticket ticket);
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
        notifyDataSetChanged();
    }

    public void setOnTicketClickListener(OnTicketClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_support_ticket, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        Ticket t = tickets.get(pos);
        h.tvReference.setText(t.getReference() != null ? t.getReference() : "#" + t.getId());
        h.tvTitle.setText(t.getTitle());
        h.tvStatus.setText(t.getStatus());
        String priority = t.getPriority() != null ? t.getPriority().toUpperCase(Locale.ROOT) : "";
        h.tvPriority.setText(priority);
        h.tvDate.setText(t.getCreatedAt());

        if (t.getCategory() != null && t.getCategory().getName() != null) {
            h.tvCategory.setText(t.getCategory().getName());
            int color = CATEGORY_COLORS[Math.abs(t.getCategory().getId()) % CATEGORY_COLORS.length];
            GradientDrawable bg = new GradientDrawable();
            bg.setColor(color);
            bg.setCornerRadius(24f);
            h.tvCategory.setBackground(bg);
            h.tvCategory.setTextColor(0xFFFFFFFF);
            h.tvCategory.setPadding(16, 4, 16, 4);
            h.tvCategory.setVisibility(View.VISIBLE);
        } else {
            h.tvCategory.setVisibility(View.GONE);
        }

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onTicketClick(t);
        });
    }

    @Override
    public int getItemCount() { return tickets.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvReference, tvTitle, tvCategory, tvStatus, tvPriority, tvDate;
        ViewHolder(@NonNull View v) {
            super(v);
            tvReference = v.findViewById(R.id.tv_ticket_reference);
            tvTitle = v.findViewById(R.id.tv_ticket_title);
            tvCategory = v.findViewById(R.id.tv_ticket_category);
            tvStatus = v.findViewById(R.id.tv_ticket_status);
            tvPriority = v.findViewById(R.id.tv_ticket_priority);
            tvDate = v.findViewById(R.id.tv_ticket_date);
        }
    }
}
