package rw.martinhardware.mymartin.ui.deliveries;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import rw.martinhardware.mymartin.R;
import rw.martinhardware.mymartin.databinding.FragmentDeliveriesBinding;
import rw.martinhardware.mymartin.ui.deliveries.DeliveryDetailsActivity;
import rw.martinhardware.mymartin.ui.deliveries.DeliveryAdapter;
import rw.martinhardware.mymartin.ui.deliveries.DeliveryViewModel;
import rw.martinhardware.mymartin.ui.deliveries.MockData;

import java.util.List;

public class DeliveriesFragment extends Fragment {

    private FragmentDeliveriesBinding binding;
    private DeliveryAdapter deliveryAdapter;
    private DeliveryViewModel deliveryViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDeliveriesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.recyclerViewDeliveries;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        deliveryAdapter = new DeliveryAdapter();
        recyclerView.setAdapter(deliveryAdapter);

        // Load mock data
        List<Delivery> deliveries = MockData.getMockDeliveries();
        deliveryAdapter.setDeliveries(deliveries);

        // Set up click listener
        deliveryAdapter.setOnItemClickListener(delivery -> {
            Intent intent = new Intent(getContext(), DeliveryDetailsActivity.class);
            intent.putExtra("delivery_id", delivery.getId());
            startActivity(intent);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
