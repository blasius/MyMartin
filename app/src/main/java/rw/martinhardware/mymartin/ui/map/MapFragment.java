package rw.martinhardware.mymartin.ui.map;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import rw.martinhardware.mymartin.R;
import rw.martinhardware.mymartin.databinding.FragmentMapBinding;
import rw.martinhardware.mymartin.ui.deliveries.Delivery;
import rw.martinhardware.mymartin.ui.deliveries.DeliveryDetailsActivity;
import rw.martinhardware.mymartin.ui.deliveries.MockData;
import rw.martinhardware.mymartin.ui.map.MapDeliveryAdapter;
import java.util.List;

public class MapFragment extends Fragment {

    private FragmentMapBinding binding;
    private MapDeliveryAdapter mapDeliveryAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Setup map placeholder
        setupMapPlaceholder();
        
        // Setup delivery list for map
        setupDeliveryList();

        return root;
    }

    private void setupMapPlaceholder() {
        // Map placeholder - in real app, this would be Google Maps or similar
        binding.mapPlaceholder.setOnClickListener(v -> {
            // TODO: Initialize full map view
        });
        
        // Update map info
        TextView tvMapInfo = binding.tvMapInfo;
        tvMapInfo.setText("Showing " + getPendingDeliveriesCount() + " pending deliveries");
    }

    private void setupDeliveryList() {
        RecyclerView recyclerView = binding.recyclerViewMapDeliveries;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        mapDeliveryAdapter = new MapDeliveryAdapter();
        recyclerView.setAdapter(mapDeliveryAdapter);

        // Load pending deliveries only
        List<Delivery> pendingDeliveries = getPendingDeliveries();
        mapDeliveryAdapter.setDeliveries(pendingDeliveries);

        // Set up click listener
        mapDeliveryAdapter.setOnItemClickListener(delivery -> {
            Intent intent = new Intent(getContext(), DeliveryDetailsActivity.class);
            intent.putExtra("delivery_id", delivery.getId());
            startActivity(intent);
        });
    }

    private List<Delivery> getPendingDeliveries() {
        List<Delivery> allDeliveries = MockData.getMockDeliveries();
        List<Delivery> pendingDeliveries = new java.util.ArrayList<>();
        
        for (Delivery delivery : allDeliveries) {
            if ("PENDING".equals(delivery.getStatus())) {
                pendingDeliveries.add(delivery);
            }
        }
        
        return pendingDeliveries;
    }

    private int getPendingDeliveriesCount() {
        return getPendingDeliveries().size();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
