package rw.martinhardware.mymartin.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import rw.martinhardware.mymartin.R;
import rw.martinhardware.mymartin.databinding.FragmentOtpVerificationBinding;
import rw.martinhardware.mymartin.viewmodel.AuthViewModel;

public class OtpVerificationFragment extends Fragment {

    private FragmentOtpVerificationBinding binding;
    private AuthViewModel authViewModel;
    private String phoneNumber;
    private String method;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOtpVerificationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        // Get arguments
        if (getArguments() != null) {
            phoneNumber = getArguments().getString("phone_number");
            method = getArguments().getString("method");
        }

        binding.tvPhoneNumber.setText(phoneNumber);

        binding.btnBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        binding.btnVerify.setOnClickListener(v -> {
            String otp = binding.etOtp.getText().toString().trim();

            if (validateInput(otp)) {
                if ("whatsapp".equals(method)) {
                    authViewModel.verifyWhatsAppOtp(phoneNumber, otp);
                }
            }
        });

        binding.btnResend.setOnClickListener(v -> {
            if ("whatsapp".equals(method)) {
                authViewModel.requestWhatsAppOtp(phoneNumber);
                Toast.makeText(getContext(), "OTP resent successfully", Toast.LENGTH_SHORT).show();
            }
        });

        // Observe loading state
        authViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.btnVerify.setEnabled(!isLoading);
            binding.btnResend.setEnabled(!isLoading);
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        // Observe errors
        authViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInput(String otp) {
        if (TextUtils.isEmpty(otp)) {
            binding.etOtp.setError("OTP is required");
            return false;
        }

        if (otp.length() < 4 || otp.length() > 6) {
            binding.etOtp.setError("OTP must be 4-6 digits");
            return false;
        }

        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
