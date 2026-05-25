package rw.martinhardware.mymartin.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import rw.martinhardware.mymartin.R;
import rw.martinhardware.mymartin.databinding.FragmentWhatsappLoginBinding;
import rw.martinhardware.mymartin.viewmodel.AuthViewModel;

public class WhatsAppLoginFragment extends Fragment {

    private FragmentWhatsappLoginBinding binding;
    private AuthViewModel authViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWhatsappLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        binding.btnBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        binding.btnRequestOtp.setOnClickListener(v -> {
            String phoneNumber = binding.etPhoneNumber.getText().toString().trim();

            if (validateInput(phoneNumber)) {
                authViewModel.requestWhatsAppOtp(phoneNumber);
            }
        });

        // Observe loading state
        authViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.btnRequestOtp.setEnabled(!isLoading);
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        // Observe errors
        authViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                Log.e("Darling: ", errorMessage);
                Log.e("Darling: ", errorMessage);
                Toast.makeText(getContext(), "This:" + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe OTP sent state
        authViewModel.getAuthState().observe(getViewLifecycleOwner(), authState -> {
            if (authState == AuthViewModel.AuthState.OTP_SENT) {
                String phoneNumber = binding.etPhoneNumber.getText().toString().trim();
                navigateToOtpVerification(phoneNumber, "whatsapp");
            }
        });
    }

    private boolean validateInput(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            binding.etPhoneNumber.setError("Phone number is required");
            return false;
        }

        if (!phoneNumber.matches("^[+]?[0-9]{10,15}$")) {
            binding.etPhoneNumber.setError("Invalid phone number format");
            return false;
        }

        return true;
    }

    private void navigateToOtpVerification(String phoneNumber, String method) {
        Bundle bundle = new Bundle();
        bundle.putString("phone_number", phoneNumber);
        bundle.putString("method", method);

        OtpVerificationFragment fragment = new OtpVerificationFragment();
        fragment.setArguments(bundle);

        getParentFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(R.id.auth_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
