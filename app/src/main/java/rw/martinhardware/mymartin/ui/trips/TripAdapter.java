package rw.martinhardware.mymartin.ui.trips;

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

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {
    
    private List<Delivery> trips = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Delivery trip);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trip, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Delivery currentTrip = trips.get(position);
        holder.bind(currentTrip);
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public void setTrips(List<Delivery> trips) {
        this.trips = trips;
        notifyDataSetChanged();
    }

    class TripViewHolder extends RecyclerView.ViewHolder {
        private TextView textTripId;
        private TextView textClientName;
        private TextView textRoute;
        private TextView textStatus;
        private TextView textTime;
        private TextView textPriority;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            textTripId = itemView.findViewById(R.id.text_trip_id);
            textClientName = itemView.findViewById(R.id.text_client_name);
            textRoute = itemView.findViewById(R.id.text_route);
            textStatus = itemView.findViewById(R.id.text_status);
            textTime = itemView.findViewById(R.id.text_time);
            textPriority = itemView.findViewById(R.id.text_priority);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(trips.get(position));
                    }
                }
            });
        }

        public void bind(Delivery trip) {
            textTripId.setText("Trip #" + trip.getId());
            textClientName.setText(trip.getCustomerName());
            textRoute.setText(trip.getAddress());
            textStatus.setText(trip.getStatus());
            textTime.setText(trip.getTime());
            textPriority.setText(trip.getPriority());

            // Set status color
            switch (trip.getStatus()) {
                case "PENDING":
                    textStatus.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_orange_dark));
                    break;
                case "IN_PROGRESS":
                case "ON_ROUTE":
                    textStatus.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_blue_dark));
                    break;
                case "COMPLETED":
                    textStatus.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
                    break;
                default:
                    textStatus.setTextColor(itemView.getContext().getResources().getColor(android.R.color.darker_gray));
            }

            // Set priority color
            switch (trip.getPriority()) {
                case "HIGH":
                    textPriority.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
                    break;
                case "MEDIUM":
                    textPriority.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_orange_dark));
                    break;
                case "LOW":
                    textPriority.setTextColor(itemView.getContext().getResources().getColor(android.R.color.darker_gray));
                    break;
            }
        }
    }
}
