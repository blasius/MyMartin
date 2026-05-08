package rw.martinhardware.mymartin.ui.map;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import rw.martinhardware.mymartin.R;
import rw.martinhardware.mymartin.ui.deliveries.Delivery;

import java.util.ArrayList;
import java.util.List;

public class MapDeliveryAdapter extends RecyclerView.Adapter<MapDeliveryAdapter.MapDeliveryViewHolder> {
    
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
    public MapDeliveryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_map_delivery, parent, false);
        return new MapDeliveryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MapDeliveryViewHolder holder, int position) {
        Delivery delivery = deliveries.get(position);
        holder.bind(delivery);
    }

    @Override
    public int getItemCount() {
        return deliveries.size();
    }

    class MapDeliveryViewHolder extends RecyclerView.ViewHolder {
        private TextView customerName;
        private TextView address;
        private TextView distance;
        private TextView time;

        public MapDeliveryViewHolder(@NonNull View itemView) {
            super(itemView);
            customerName = itemView.findViewById(R.id.tv_map_customer_name);
            address = itemView.findViewById(R.id.tv_map_address);
            distance = itemView.findViewById(R.id.tv_map_distance);
            time = itemView.findViewById(R.id.tv_map_time);
        }

        public void bind(Delivery delivery) {
            customerName.setText(delivery.getCustomerName());
            address.setText(delivery.getAddress());
            time.setText(delivery.getTime());
            
            // Mock distance calculation
            distance.setText(getMockDistance(delivery));
            
            // Set click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(delivery);
                }
            });
        }
        
        private String getMockDistance(Delivery delivery) {
            String[] distances = {"2.5 km", "5.8 km", "12.3 km", "8.1 km", "3.7 km"};
            return distances[Math.abs(deliveries.indexOf(delivery) % distances.length)];
        }
    }
}
