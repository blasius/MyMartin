package rw.martinhardware.mymartin.ui.compliance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import rw.martinhardware.mymartin.databinding.FragmentComplianceBinding;
import rw.martinhardware.mymartin.ui.compliance.ComplianceAdapter;
import rw.martinhardware.mymartin.ui.compliance.ComplianceViewModel;

public class ComplianceFragment extends Fragment {

    private FragmentComplianceBinding binding;
    private ComplianceAdapter complianceAdapter;
    private ComplianceViewModel complianceViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        
        complianceViewModel = new ViewModelProvider(this).get(ComplianceViewModel.class);
        binding = FragmentComplianceBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupRecyclerView();
        setupObservers();
        setupClickListeners();
        
        return root;
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.recyclerViewCompliance;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        complianceAdapter = new ComplianceAdapter();
        recyclerView.setAdapter(complianceAdapter);
    }

    private void setupObservers() {
        complianceViewModel.getVehicleStatus().observe(getViewLifecycleOwner(), vehicleStatus -> {
            binding.textVehiclePlate.setText(vehicleStatus.getPlate());
            binding.textVehicleModel.setText(vehicleStatus.getModel());
            binding.textInspectionDate.setText(vehicleStatus.getInspectionDate());
            binding.textComplianceScore.setText(vehicleStatus.getComplianceScore() + "%");
        });

        complianceViewModel.getFines().observe(getViewLifecycleOwner(), fines -> {
            complianceAdapter.setFines(fines);
        });

        complianceViewModel.isLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.scrollViewContent.setVisibility(View.GONE);
            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.scrollViewContent.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupClickListeners() {
        binding.buttonRefresh.setOnClickListener(v -> {
            complianceViewModel.refreshComplianceData();
            Toast.makeText(getContext(), "Refreshing compliance data...", Toast.LENGTH_SHORT).show();
        });

        binding.buttonViewInspections.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Opening inspections calendar...", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to inspections calendar
        });

        binding.buttonViewFines.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Opening fines details...", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to fines details
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
