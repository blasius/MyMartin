package rw.martinhardware.mymartin.ui.trips;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import rw.martinhardware.mymartin.ui.deliveries.Delivery;
import rw.martinhardware.mymartin.ui.deliveries.MockData;

public class TripDetailsViewModel extends ViewModel {
    
    private MutableLiveData<Delivery> currentTrip;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<String> errorMessage;
    private MutableLiveData<Boolean> statusUpdateSuccess;

    public TripDetailsViewModel() {
        currentTrip = new MutableLiveData<>();
        isLoading = new MutableLiveData<>();
        errorMessage = new MutableLiveData<>();
        statusUpdateSuccess = new MutableLiveData<>();
    }

    public void loadTripDetails(String tripId) {
        isLoading.setValue(true);
        
        try {
            // In a real app, this would fetch from API
            // For now, use mock data
            Delivery mockTrip = MockData.getMockDeliveries().get(0);
            currentTrip.setValue(mockTrip);
            errorMessage.setValue(null);
        } catch (Exception e) {
            errorMessage.setValue("Failed to load trip details: " + e.getMessage());
        } finally {
            isLoading.setValue(false);
        }
    }

    public void updateTripStatus(String newStatus, String notes) {
        isLoading.setValue(true);
        
        try {
            // In a real app, this would call the API
            Delivery trip = currentTrip.getValue();
            if (trip != null) {
                trip.setStatus(newStatus);
                currentTrip.setValue(trip);
                statusUpdateSuccess.setValue(true);
            }
        } catch (Exception e) {
            errorMessage.setValue("Failed to update status: " + e.getMessage());
            statusUpdateSuccess.setValue(false);
        } finally {
            isLoading.setValue(false);
        }
    }

    public LiveData<Delivery> getCurrentTrip() {
        return currentTrip;
    }

    public LiveData<Boolean> isLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getStatusUpdateSuccess() {
        return statusUpdateSuccess;
    }
}
