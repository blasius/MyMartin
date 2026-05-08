package rw.martinhardware.mymartin.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import rw.martinhardware.mymartin.databinding.FragmentDashboardBinding;
import rw.martinhardware.mymartin.ui.trips.TripDetailsActivity;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private DashboardViewModel dashboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupUI();
        setupObservers();
        return root;
    }

    private void setupUI() {
        // Set up click listeners
        binding.buttonNotifications.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Notifications clicked", Toast.LENGTH_SHORT).show();
        });

        binding.buttonStartNavigation.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Starting navigation...", Toast.LENGTH_SHORT).show();
            // TODO: Integrate with maps/navigation
        });

        binding.buttonMarkDelivered.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Marking as delivered...", Toast.LENGTH_SHORT).show();
            // TODO: Update trip status via API
        });

        binding.cardCurrentTrip.setOnClickListener(v -> {
            // Navigate to trip details
            Intent intent = new Intent(getContext(), TripDetailsActivity.class);
            intent.putExtra("trip_id", "current_trip_id");
            startActivity(intent);
        });
    }

    private void setupObservers() {
        dashboardViewModel.getDriverName().observe(getViewLifecycleOwner(), name -> {
            binding.textDriverName.setText(name);
        });

        dashboardViewModel.getTripsCount().observe(getViewLifecycleOwner(), count -> {
            binding.textTripsCount.setText(String.valueOf(count));
        });

        dashboardViewModel.getCompletedCount().observe(getViewLifecycleOwner(), count -> {
            binding.textCompletedCount.setText(String.valueOf(count));
        });

        dashboardViewModel.getComplianceScore().observe(getViewLifecycleOwner(), score -> {
            binding.textComplianceScore.setText(score + "%");
        });

        dashboardViewModel.getCurrentTrip().observe(getViewLifecycleOwner(), trip -> {
            if (trip != null) {
                binding.textClientName.setText("Client: " + trip.getCustomerName());
                binding.textRoute.setText("Route: " + trip.getAddress());
                binding.textTripStatus.setText(trip.getStatus());
            } else {
                binding.cardCurrentTrip.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}