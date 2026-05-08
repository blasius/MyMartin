package rw.martinhardware.mymartin.ui.trips;

import android.content.Intent;
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

import rw.martinhardware.mymartin.databinding.FragmentTripsBinding;
import rw.martinhardware.mymartin.ui.trips.TripAdapter;
import rw.martinhardware.mymartin.ui.trips.TripViewModel;

public class TripsFragment extends Fragment {

    private FragmentTripsBinding binding;
    private TripAdapter tripAdapter;
    private TripViewModel tripViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        
        tripViewModel = new ViewModelProvider(this).get(TripViewModel.class);
        binding = FragmentTripsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupRecyclerView();
        setupObservers();
        
        return root;
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.recyclerViewTrips;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        tripAdapter = new TripAdapter();
        recyclerView.setAdapter(tripAdapter);

        // Set up click listener
        tripAdapter.setOnItemClickListener(trip -> {
            Intent intent = new Intent(getContext(), TripDetailsActivity.class);
            intent.putExtra("trip_id", trip.getId());
            startActivity(intent);
        });
    }

    private void setupObservers() {
        tripViewModel.getTrips().observe(getViewLifecycleOwner(), trips -> {
            tripAdapter.setTrips(trips);
        });

        tripViewModel.isLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.recyclerViewTrips.setVisibility(View.GONE);
            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.recyclerViewTrips.setVisibility(View.VISIBLE);
            }
        });

        tripViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
