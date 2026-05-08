package rw.martinhardware.mymartin.ui.compliance;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import rw.martinhardware.mymartin.entities.Vehicle;

public class ComplianceViewModel extends ViewModel {
    
    private MutableLiveData<VehicleStatus> vehicleStatus;
    private MutableLiveData<List<Fine>> fines;
    private MutableLiveData<Boolean> isLoading;

    public ComplianceViewModel() {
        vehicleStatus = new MutableLiveData<>();
        fines = new MutableLiveData<>();
        isLoading = new MutableLiveData<>();
        
        loadComplianceData();
    }

    private void loadComplianceData() {
        isLoading.setValue(true);
        
        // Mock vehicle status
        VehicleStatus mockVehicleStatus = new VehicleStatus(
            "RAA 123A",
            "Volvo FH16",
            "2024-12-15",
            98
        );
        vehicleStatus.setValue(mockVehicleStatus);

        // Mock fines
        List<Fine> mockFines = new ArrayList<>();
        mockFines.add(new Fine("F001", "Speeding", "2024-11-20", 50000, "UNPAID"));
        mockFines.add(new Fine("F002", "Illegal Parking", "2024-11-15", 25000, "PAID"));
        mockFines.add(new Fine("F003", "Traffic Violation", "2024-11-10", 75000, "UNPAID"));
        fines.setValue(mockFines);
        
        isLoading.setValue(false);
    }

    public void refreshComplianceData() {
        loadComplianceData();
    }

    public LiveData<VehicleStatus> getVehicleStatus() {
        return vehicleStatus;
    }

    public LiveData<List<Fine>> getFines() {
        return fines;
    }

    public LiveData<Boolean> isLoading() {
        return isLoading;
    }

    // Inner classes for data models
    public static class VehicleStatus {
        private String plate;
        private String model;
        private String inspectionDate;
        private int complianceScore;

        public VehicleStatus(String plate, String model, String inspectionDate, int complianceScore) {
            this.plate = plate;
            this.model = model;
            this.inspectionDate = inspectionDate;
            this.complianceScore = complianceScore;
        }

        // Getters
        public String getPlate() { return plate; }
        public String getModel() { return model; }
        public String getInspectionDate() { return inspectionDate; }
        public int getComplianceScore() { return complianceScore; }
    }

    public static class Fine {
        private String id;
        private String type;
        private String date;
        private int amount;
        private String status;

        public Fine(String id, String type, String date, int amount, String status) {
            this.id = id;
            this.type = type;
            this.date = date;
            this.amount = amount;
            this.status = status;
        }

        // Getters
        public String getId() { return id; }
        public String getType() { return type; }
        public String getDate() { return date; }
        public int getAmount() { return amount; }
        public String getStatus() { return status; }
    }
}
