package rw.martinhardware.mymartin.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import rw.martinhardware.mymartin.MainActivity;
import rw.martinhardware.mymartin.R;
import rw.martinhardware.mymartin.viewmodel.AuthViewModel;

public class AuthActivity extends AppCompatActivity {

    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Observe auth state
        authViewModel.getAuthState().observe(this, authState -> {
            if (authState == AuthViewModel.AuthState.AUTHENTICATED) {
                // Navigate to MainActivity
                Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        // Load initial fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.auth_container, new AuthSelectionFragment())
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        // Prevent going back to MainActivity if not authenticated
        if (authViewModel.getAuthState().getValue() != AuthViewModel.AuthState.AUTHENTICATED) {
            super.onBackPressed();
        }
    }
}
