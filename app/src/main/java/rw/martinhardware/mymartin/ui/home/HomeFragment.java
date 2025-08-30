package rw.martinhardware.mymartin.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.UUID;

import rw.martinhardware.mymartin.R;
import rw.martinhardware.mymartin.adapters.VehicleAdapter;
import rw.martinhardware.mymartin.databinding.FragmentHomeBinding;
import rw.martinhardware.mymartin.db.AppDatabase;
import rw.martinhardware.mymartin.entities.Vehicle;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rv = view.findViewById(R.id.rvVehicles);
        VehicleAdapter adapter = new VehicleAdapter();
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);

        AppDatabase db = AppDatabase.getInstance(requireContext());

        new Thread(() -> {
            // Insert a test vehicle (optional, for first run)
            if (db.vehicleDao().getAllVehicles().isEmpty()) {
                Vehicle v = new Vehicle();
                v.setUuid(UUID.randomUUID().toString());
                v.setPlate("RA1234");
                v.setModel("Toyota");
                v.setInspectionDate("2025-09-01");
                v.setNotes("Test car");
                v.setUpdatedAt(System.currentTimeMillis());
                db.vehicleDao().insert(v);
            }

            List<Vehicle> list = db.vehicleDao().getAllVehicles();

            requireActivity().runOnUiThread(() -> {
                adapter.setVehicles(list);
            });
        }).start();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}