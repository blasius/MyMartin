package rw.martinhardware.mymartin.ui.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import rw.martinhardware.mymartin.R;
import rw.martinhardware.mymartin.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private SharedPreferences sharedPreferences;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sharedPreferences = requireContext().getSharedPreferences("DriverPrefs", getContext().MODE_PRIVATE);

        // Initialize views
        TextView tvDriverName = binding.tvDriverName;
        TextView tvDriverId = binding.tvDriverId;
        TextView tvPhoneNumber = binding.tvPhoneNumber;
        TextView tvVehicleNumber = binding.tvVehicleNumber;
        TextView tvTotalDeliveries = binding.tvTotalDeliveries;
        TextView tvCompletedDeliveries = binding.tvCompletedDeliveries;
        TextView tvPendingDeliveries = binding.tvPendingDeliveries;

        // Load driver profile data
        loadDriverProfile();

        // Setup click listeners for settings
        binding.llEditProfile.setOnClickListener(v -> {
            // TODO: Open edit profile dialog/activity
        });

        binding.llSettings.setOnClickListener(v -> {
            // TODO: Open settings screen
        });

        binding.llLogout.setOnClickListener(v -> {
            logoutDriver();
        });

        return root;
    }

    private void loadDriverProfile() {
        // Load saved driver data
        String driverName = sharedPreferences.getString("driver_name", "John Driver");
        String driverId = sharedPreferences.getString("driver_id", "DRV001");
        String phoneNumber = sharedPreferences.getString("phone_number", "+250 788 999 000");
        String vehicleNumber = sharedPreferences.getString("vehicle_number", "RAA 123A");

        // Set profile data
        binding.tvDriverName.setText(driverName);
        binding.tvDriverId.setText("ID: " + driverId);
        binding.tvPhoneNumber.setText(phoneNumber);
        binding.tvVehicleNumber.setText("Vehicle: " + vehicleNumber);

        // Load delivery statistics (mock data for now)
        binding.tvTotalDeliveries.setText("156");
        binding.tvCompletedDeliveries.setText("142");
        binding.tvPendingDeliveries.setText("14");
    }

    private void logoutDriver() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("is_logged_in", false);
        editor.apply();

        // TODO: Navigate to login screen
        // For now, just show a message
        android.widget.Toast.makeText(getContext(), "Logged out successfully", android.widget.Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
