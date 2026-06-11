package rw.martinhardware.mymartin.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import io.objectbox.Box;
import rw.martinhardware.mymartin.MyApp;
import rw.martinhardware.mymartin.auth.AuthActivity;
import rw.martinhardware.mymartin.databinding.FragmentProfileBinding;
import rw.martinhardware.mymartin.entities.User;
import rw.martinhardware.mymartin.entities.User_;
import rw.martinhardware.mymartin.viewmodel.AuthViewModel;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private AuthViewModel authViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        binding.llLogout.setOnClickListener(v -> logout());

        authViewModel.getAuthState().observe(getViewLifecycleOwner(), state -> {
            if (state == AuthViewModel.AuthState.NOT_AUTHENTICATED) {
                Intent intent = new Intent(getContext(), AuthActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
            }
        });

        loadUserProfile();
    }

    private void loadUserProfile() {
        try {
            Box<User> box = ((MyApp) requireActivity().getApplication()).getBoxStore().boxFor(User.class);
            User u = box.query().equal(User_.isActive, true).build().findFirst();
            if (u != null) {
                binding.tvDriverName.setText(u.getName() != null ? u.getName() : "Driver");
                String id = u.getUuid() != null ? "ID: " + u.getUuid() : (u.getEmail() != null ? u.getEmail() : "");
                binding.tvDriverId.setText(id);
                String phone = u.getPhoneNumber() != null ? u.getPhoneNumber() : u.getWhatsappNumber();
                binding.tvPhoneNumber.setText(phone != null ? phone : "N/A");

                binding.llVehicleRow.setVisibility(View.GONE);
                binding.llDeliveryStats.setVisibility(View.GONE);
                binding.llEditProfile.setVisibility(View.GONE);
                binding.llSettings.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
        }
    }

    private void logout() {
        authViewModel.logout();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
