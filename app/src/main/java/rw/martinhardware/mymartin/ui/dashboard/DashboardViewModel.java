package rw.martinhardware.mymartin.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import rw.martinhardware.mymartin.ui.deliveries.Delivery;

public class DashboardViewModel extends ViewModel {
    
    private MutableLiveData<String> driverName;
    private MutableLiveData<Integer> tripsCount;
    private MutableLiveData<Integer> completedCount;
    private MutableLiveData<Integer> complianceScore;
    private MutableLiveData<Delivery> currentTrip;

    public DashboardViewModel() {
        driverName = new MutableLiveData<>();
        tripsCount = new MutableLiveData<>();
        completedCount = new MutableLiveData<>();
        complianceScore = new MutableLiveData<>();
        currentTrip = new MutableLiveData<>();
        
        loadDashboardData();
    }

    private void loadDashboardData() {
        // Mock data - in real app, this would come from API/database
        driverName.setValue("John Kaigarula");
        tripsCount.setValue(5);
        completedCount.setValue(3);
        complianceScore.setValue(98);
        
        // Create a mock current trip
        Delivery mockTrip = new Delivery(
            "TRIP001",
            "ABC Corp",
            "Kigali → Butare",
            "ON_ROUTE",
            "02:30 PM",
            "+250 788 123 456",
            "Electronics - 2.5 tons",
            -1.9536,
            30.0606,
            "HIGH"
        );
        currentTrip.setValue(mockTrip);
    }

    public LiveData<String> getDriverName() {
        return driverName;
    }

    public LiveData<Integer> getTripsCount() {
        return tripsCount;
    }

    public LiveData<Integer> getCompletedCount() {
        return completedCount;
    }

    public LiveData<Integer> getComplianceScore() {
        return complianceScore;
    }

    public LiveData<Delivery> getCurrentTrip() {
        return currentTrip;
    }
}