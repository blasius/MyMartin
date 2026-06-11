package rw.martinhardware.mymartin.ui.support;

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
        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onTicketClick(t);
        });
    }

    @Override
    public int getItemCount() { return tickets.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvReference, tvTitle, tvStatus, tvPriority, tvDate;
        ViewHolder(@NonNull View v) {
            super(v);
            tvReference = v.findViewById(R.id.tv_ticket_reference);
            tvTitle = v.findViewById(R.id.tv_ticket_title);
            tvStatus = v.findViewById(R.id.tv_ticket_status);
            tvPriority = v.findViewById(R.id.tv_ticket_priority);
            tvDate = v.findViewById(R.id.tv_ticket_date);
        }
    }
}
