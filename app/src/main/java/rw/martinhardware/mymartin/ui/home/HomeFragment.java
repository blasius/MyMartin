package rw.martinhardware.mymartin.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import io.objectbox.Box;
import rw.martinhardware.mymartin.MyApp;
import rw.martinhardware.mymartin.R;
import rw.martinhardware.mymartin.databinding.FragmentHomeBinding;
import rw.martinhardware.mymartin.entities.User;
import rw.martinhardware.mymartin.entities.User_;

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

        binding.tvHello.setText("Welcome, " + getDriverName());

        binding.cardSupport.setOnClickListener(v ->
                Navigation.findNavController(requireView()).navigate(R.id.navigation_support));
        binding.cardProfile.setOnClickListener(v ->
                Navigation.findNavController(requireView()).navigate(R.id.navigation_profile));
    }

    private String getDriverName() {
        try {
            Box<User> box = ((MyApp) requireActivity().getApplication()).getBoxStore().boxFor(User.class);
            User u = box.query().equal(User_.isActive, true).build().findFirst();
            if (u != null && u.getName() != null) return u.getName();
        } catch (Exception ignored) {}
        return "Driver";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
