package rw.martinhardware.mymartin;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import rw.martinhardware.mymartin.auth.AuthActivity;
import rw.martinhardware.mymartin.databinding.ActivityMainBinding;
import rw.martinhardware.mymartin.entities.User;
import rw.martinhardware.mymartin.entities.User_;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isUserAuthenticated()) {
            navigateToAuth();
            return;
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    private boolean isUserAuthenticated() {
        try {
            BoxStore boxStore = ((MyApp) getApplication()).getBoxStore();
            Box<User> userBox = boxStore.boxFor(User.class);
            User active = userBox.query().equal(User_.isActive, true).build().findFirst();
            return active != null && active.isTokenValid();
        } catch (Exception e) {
            return false;
        }
    }

    private void navigateToAuth() {
        Intent intent = new Intent(this, AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
