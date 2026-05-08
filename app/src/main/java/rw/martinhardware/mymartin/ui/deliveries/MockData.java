package rw.martinhardware.mymartin.ui.deliveries;

import java.util.ArrayList;
import java.util.List;

public class MockData {
    
    public static List<Delivery> getMockDeliveries() {
        List<Delivery> deliveries = new ArrayList<>();
        
        deliveries.add(new Delivery(
            "DEL001",
            "John Smith",
            "123 Main St, Kigali, Rwanda",
            "PENDING",
            "09:30 AM",
            "+250 788 123 456",
            "Electronics Package - 5kg",
            -1.9536,
            30.0606,
            "HIGH"
        ));
        
        deliveries.add(new Delivery(
            "DEL002",
            "Alice Johnson",
            "456 Market St, Kigali, Rwanda",
            "IN_PROGRESS",
            "10:15 AM",
            "+250 788 234 567",
            "Food Supplies - 20kg",
            -1.9441,
            30.0619,
            "MEDIUM"
        ));
        
        deliveries.add(new Delivery(
            "DEL003",
            "Robert Williams",
            "789 Industrial Rd, Kigali, Rwanda",
            "COMPLETED",
            "08:45 AM",
            "+250 788 345 678",
            "Industrial Parts - 50kg",
            -1.9367,
            30.0528,
            "LOW"
        ));
        
        deliveries.add(new Delivery(
            "DEL004",
            "Sarah Davis",
            "321 Commerce Ave, Kigali, Rwanda",
            "PENDING",
            "11:00 AM",
            "+250 788 456 789",
            "Medical Supplies - 10kg",
            -1.9500,
            30.0700,
            "HIGH"
        ));
        
        deliveries.add(new Delivery(
            "DEL005",
            "Michael Brown",
            "654 Transport Way, Kigali, Rwanda",
            "PENDING",
            "02:30 PM",
            "+250 788 567 890",
            "Furniture - 100kg",
            -1.9400,
            30.0800,
            "MEDIUM"
        ));
        
        deliveries.add(new Delivery(
            "DEL006",
            "Emma Wilson",
            "987 Logistics Blvd, Kigali, Rwanda",
            "IN_PROGRESS",
            "03:45 PM",
            "+250 788 678 901",
            "Clothing - 30kg",
            -1.9600,
            30.0500,
            "LOW"
        ));
        
        return deliveries;
    }
}
