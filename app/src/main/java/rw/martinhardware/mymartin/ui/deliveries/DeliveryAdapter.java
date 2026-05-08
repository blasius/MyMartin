package rw.martinhardware.mymartin.ui.deliveries;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import rw.martinhardware.mymartin.R;

import java.util.ArrayList;
import java.util.List;

public class DeliveryAdapter extends RecyclerView.Adapter<DeliveryAdapter.DeliveryViewHolder> {
    
    private List<Delivery> deliveries = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Delivery delivery);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setDeliveries(List<Delivery> deliveries) {
        this.deliveries = deliveries;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DeliveryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_delivery, parent, false);
        return new DeliveryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryViewHolder holder, int position) {
        Delivery delivery = deliveries.get(position);
        holder.bind(delivery);
    }

    @Override
    public int getItemCount() {
        return deliveries.size();
    }

    class DeliveryViewHolder extends RecyclerView.ViewHolder {
        private TextView customerName;
        private TextView address;
        private TextView status;
        private TextView time;
        private TextView priority;
        private TextView packageInfo;

        public DeliveryViewHolder(@NonNull View itemView) {
            super(itemView);
            customerName = itemView.findViewById(R.id.tv_customer_name);
            address = itemView.findViewById(R.id.tv_address);
            status = itemView.findViewById(R.id.tv_status);
            time = itemView.findViewById(R.id.tv_time);
            priority = itemView.findViewById(R.id.tv_priority);
            packageInfo = itemView.findViewById(R.id.tv_package_info);
        }

        public void bind(Delivery delivery) {
            customerName.setText(delivery.getCustomerName());
            address.setText(delivery.getAddress());
            time.setText(delivery.getTime());
            packageInfo.setText(delivery.getPackageInfo());
            
            // Set status with color
            status.setText(delivery.getStatus());
            switch (delivery.getStatus()) {
                case "PENDING":
                    status.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_orange_dark));
                    break;
                case "IN_PROGRESS":
                    status.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_blue_dark));
                    break;
                case "COMPLETED":
                    status.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
                    break;
            }
            
            // Set priority with color
            priority.setText(delivery.getPriority());
            switch (delivery.getPriority()) {
                case "HIGH":
                    priority.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
                    break;
                case "MEDIUM":
                    priority.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_orange_dark));
                    break;
                case "LOW":
                    priority.setTextColor(itemView.getContext().getResources().getColor(android.R.color.darker_gray));
                    break;
            }
            
            // Set click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(delivery);
                }
            });
        }
    }
}
