package rw.martinhardware.mymartin;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import io.objectbox.BoxStore;
import rw.martinhardware.mymartin.auth.AuthActivity;
import rw.martinhardware.mymartin.dao.UserDao;
import rw.martinhardware.mymartin.databinding.ActivityMainBinding;
import rw.martinhardware.mymartin.entities.User;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check authentication status
        if (!isUserAuthenticated()) {
            navigateToAuth();
            return;
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_dashboard, R.id.navigation_trips, R.id.navigation_map,
                R.id.navigation_compliance, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    private boolean isUserAuthenticated() {
        try {
            BoxStore boxStore = ((MyApp) getApplication()).getBoxStore();
            UserDao userDao = new UserDao(boxStore);
            User activeUser = userDao.getActiveUser();
            return activeUser != null && activeUser.isTokenValid();
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