package rw.martinhardware.mymartin.ui.trips;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import rw.martinhardware.mymartin.databinding.ActivityTripDetailsBinding;
import rw.martinhardware.mymartin.ui.deliveries.Delivery;

public class TripDetailsActivity extends AppCompatActivity {

    private ActivityTripDetailsBinding binding;
    private TripDetailsViewModel viewModel;
    private String tripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTripDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get trip ID from intent
        tripId = getIntent().getStringExtra("trip_id");
        if (tripId == null) {
            tripId = "current_trip_id"; // Default for dashboard
        }

        viewModel = new ViewModelProvider(this).get(TripDetailsViewModel.class);
        viewModel.loadTripDetails(tripId);

        setupUI();
        setupObservers();
    }

    private void setupUI() {
        // Set up click listeners
        binding.buttonCallClient.setOnClickListener(v -> {
            viewModel.getCurrentTrip().observe(this, trip -> {
                if (trip != null) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + trip.getPhoneNumber()));
                    startActivity(intent);
                }
            });
        });

        binding.buttonViewRoute.setOnClickListener(v -> {
            Toast.makeText(this, "Opening route in maps...", Toast.LENGTH_SHORT).show();
            // TODO: Open route in Google Maps
        });

        binding.buttonUpdateStatus.setOnClickListener(v -> {
            showStatusUpdateDialog();
        });

        binding.buttonStartNavigation.setOnClickListener(v -> {
            Toast.makeText(this, "Starting navigation...", Toast.LENGTH_SHORT).show();
            // TODO: Start navigation
        });

        binding.buttonMarkDelivered.setOnClickListener(v -> {
            viewModel.updateTripStatus("COMPLETED", "Delivered successfully");
        });
    }

    private void setupObservers() {
        viewModel.getCurrentTrip().observe(this, trip -> {
            if (trip != null) {
                updateUI(trip);
            }
        });

        viewModel.isLoading().observe(this, isLoading -> {
            if (isLoading) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.scrollViewContent.setVisibility(View.GONE);
            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.scrollViewContent.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getStatusUpdateSuccess().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Status updated successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(Delivery trip) {
        binding.textTripId.setText("Trip #" + trip.getId());
        binding.textClientName.setText(trip.getCustomerName());
        binding.textRoute.setText(trip.getAddress());
        binding.textWeight.setText("2.5 tons"); // Mock data
        binding.textPhone.setText(trip.getPhoneNumber());
        binding.textStatus.setText(trip.getStatus());
        binding.textTime.setText(trip.getTime());
        binding.textPriority.setText(trip.getPriority());

        // Update status color
        switch (trip.getStatus()) {
            case "PENDING":
                binding.textStatus.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                break;
            case "IN_PROGRESS":
            case "ON_ROUTE":
                binding.textStatus.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                break;
            case "COMPLETED":
                binding.textStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                break;
            default:
                binding.textStatus.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }

        // Update priority color
        switch (trip.getPriority()) {
            case "HIGH":
                binding.textPriority.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                break;
            case "MEDIUM":
                binding.textPriority.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                break;
            case "LOW":
                binding.textPriority.setTextColor(getResources().getColor(android.R.color.darker_gray));
                break;
        }
    }

    private void showStatusUpdateDialog() {
        // TODO: Implement status update dialog
        Toast.makeText(this, "Status update dialog coming soon", Toast.LENGTH_SHORT).show();
    }
}
