package rw.martinhardware.mymartin.ui.deliveries;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;

public class DeliveryViewModel extends ViewModel {
    private MutableLiveData<List<Delivery>> deliveries;

    public DeliveryViewModel() {
        deliveries = new MutableLiveData<>();
        loadDeliveries();
    }

    public LiveData<List<Delivery>> getDeliveries() {
        return deliveries;
    }

    private void loadDeliveries() {
        // In a real app, this would fetch from a database or API
        deliveries.setValue(MockData.getMockDeliveries());
    }

    public void updateDeliveryStatus(String deliveryId, String newStatus) {
        // Update delivery status logic
        List<Delivery> currentDeliveries = deliveries.getValue();
        if (currentDeliveries != null) {
            for (Delivery delivery : currentDeliveries) {
                if (delivery.getId().equals(deliveryId)) {
                    delivery.setStatus(newStatus);
                    break;
                }
            }
            deliveries.setValue(currentDeliveries);
        }
    }
}
