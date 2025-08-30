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

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import rw.martinhardware.mymartin.R;
import rw.martinhardware.mymartin.adapters.VehicleAdapter;
import rw.martinhardware.mymartin.databinding.FragmentHomeBinding;
import rw.martinhardware.mymartin.db.AppDatabase;
import rw.martinhardware.mymartin.entities.Vehicle;
import rw.martinhardware.mymartin.utils.SyncUtils;

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
        TextView tvLastSync = view.findViewById(R.id.tvLastSync);

        VehicleAdapter adapter = new VehicleAdapter();
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);

        AppDatabase db = AppDatabase.getInstance(requireContext());

        new Thread(() -> {
            // Insert a test vehicle if DB empty
            if (db.vehicleDao().getAllVehicles().isEmpty()) {
                Vehicle v = new Vehicle();
                v.setUuid(UUID.randomUUID().toString());
                v.setPlate("RA1234");
                v.setModel("Toyota");
                v.setInspectionDate("2025-09-01");
                v.setNotes("Test car");
                v.setUpdatedAt(System.currentTimeMillis());
                db.vehicleDao().insert(v);

                // pretend we just synced
                SyncUtils.saveLastSync(requireContext(), System.currentTimeMillis());
            }

            List<Vehicle> list = db.vehicleDao().getAllVehicles();
            long lastSync = SyncUtils.getLastSync(requireContext());

            requireActivity().runOnUiThread(() -> {
                adapter.setVehicles(list);

                if (lastSync == 0) {
                    tvLastSync.setText("Last synced: never");
                } else {
                    Date date = new Date(lastSync);
                    DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
                    tvLastSync.setText("Last synced: " + df.format(date));
                }
            });
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}