package rw.martinhardware.mymartin.ui.trips;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import rw.martinhardware.mymartin.ui.deliveries.Delivery;
import rw.martinhardware.mymartin.ui.deliveries.MockData;

public class TripViewModel extends ViewModel {
    
    private MutableLiveData<List<Delivery>> trips;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<String> errorMessage;

    public TripViewModel() {
        trips = new MutableLiveData<>();
        isLoading = new MutableLiveData<>();
        errorMessage = new MutableLiveData<>();
        
        loadTrips();
    }

    private void loadTrips() {
        isLoading.setValue(true);
        
        try {
            // In a real app, this would fetch from API
            List<Delivery> mockTrips = MockData.getMockDeliveries();
            trips.setValue(mockTrips);
            errorMessage.setValue(null);
        } catch (Exception e) {
            errorMessage.setValue("Failed to load trips: " + e.getMessage());
        } finally {
            isLoading.setValue(false);
        }
    }

    public void refreshTrips() {
        loadTrips();
    }

    public LiveData<List<Delivery>> getTrips() {
        return trips;
    }

    public LiveData<Boolean> isLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
}
