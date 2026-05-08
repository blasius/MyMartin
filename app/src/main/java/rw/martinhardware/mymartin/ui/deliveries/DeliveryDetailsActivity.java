package rw.martinhardware.mymartin.ui.deliveries;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import rw.martinhardware.mymartin.R;

public class DeliveryDetailsActivity extends AppCompatActivity {
    
    private TextView tvCustomerName, tvAddress, tvPhoneNumber, tvStatus, tvTime, 
                     tvPackageInfo, tvPriority, tvDeliveryId;
    private Button btnCallCustomer, btnNavigate, btnUpdateStatus;
    private Delivery currentDelivery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_details);

        // Initialize views
        initViews();
        
        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Delivery Details");
        }

        // Get delivery data
        String deliveryId = getIntent().getStringExtra("delivery_id");
        loadDeliveryDetails(deliveryId);
        
        // Setup button listeners
        setupButtonListeners();
    }

    private void initViews() {
        tvDeliveryId = findViewById(R.id.tv_delivery_id);
        tvCustomerName = findViewById(R.id.tv_customer_name);
        tvAddress = findViewById(R.id.tv_address);
        tvPhoneNumber = findViewById(R.id.tv_phone_number);
        tvStatus = findViewById(R.id.tv_status);
        tvTime = findViewById(R.id.tv_time);
        tvPackageInfo = findViewById(R.id.tv_package_info);
        tvPriority = findViewById(R.id.tv_priority);
        
        btnCallCustomer = findViewById(R.id.btn_call_customer);
        btnNavigate = findViewById(R.id.btn_navigate);
        btnUpdateStatus = findViewById(R.id.btn_update_status);
    }

    private void loadDeliveryDetails(String deliveryId) {
        // Find delivery from mock data
        for (Delivery delivery : MockData.getMockDeliveries()) {
            if (delivery.getId().equals(deliveryId)) {
                currentDelivery = delivery;
                populateViews();
                break;
            }
        }
    }

    private void populateViews() {
        if (currentDelivery != null) {
            tvDeliveryId.setText("Delivery ID: " + currentDelivery.getId());
            tvCustomerName.setText(currentDelivery.getCustomerName());
            tvAddress.setText(currentDelivery.getAddress());
            tvPhoneNumber.setText(currentDelivery.getPhoneNumber());
            tvStatus.setText(currentDelivery.getStatus());
            tvTime.setText("Scheduled Time: " + currentDelivery.getTime());
            tvPackageInfo.setText(currentDelivery.getPackageInfo());
            tvPriority.setText(currentDelivery.getPriority());
            
            // Set status color
            switch (currentDelivery.getStatus()) {
                case "PENDING":
                    tvStatus.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                    break;
                case "IN_PROGRESS":
                    tvStatus.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                    break;
                case "COMPLETED":
                    tvStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    break;
            }
            
            // Set priority color
            switch (currentDelivery.getPriority()) {
                case "HIGH":
                    tvPriority.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    break;
                case "MEDIUM":
                    tvPriority.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                    break;
                case "LOW":
                    tvPriority.setTextColor(getResources().getColor(android.R.color.darker_gray));
                    break;
            }
            
            // Update button text based on status
            updateStatusButton();
        }
    }

    private void setupButtonListeners() {
        btnCallCustomer.setOnClickListener(v -> {
            if (currentDelivery != null) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + currentDelivery.getPhoneNumber()));
                startActivity(intent);
            }
        });

        btnNavigate.setOnClickListener(v -> {
            if (currentDelivery != null) {
                // Open Google Maps for navigation
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + 
                    currentDelivery.getLatitude() + "," + currentDelivery.getLongitude());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        btnUpdateStatus.setOnClickListener(v -> {
            if (currentDelivery != null) {
                String newStatus = getNextStatus(currentDelivery.getStatus());
                currentDelivery.setStatus(newStatus);
                populateViews();
                Toast.makeText(this, "Status updated to " + newStatus, Toast.LENGTH_SHORT).show();
                
                // Here you would also sync with backend
            }
        });
    }

    private String getNextStatus(String currentStatus) {
        switch (currentStatus) {
            case "PENDING":
                return "IN_PROGRESS";
            case "IN_PROGRESS":
                return "COMPLETED";
            case "COMPLETED":
                return "PENDING"; // Allow cycling back for demo
            default:
                return "PENDING";
        }
    }

    private void updateStatusButton() {
        if (currentDelivery != null) {
            switch (currentDelivery.getStatus()) {
                case "PENDING":
                    btnUpdateStatus.setText("Start Delivery");
                    btnUpdateStatus.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
                    break;
                case "IN_PROGRESS":
                    btnUpdateStatus.setText("Complete Delivery");
                    btnUpdateStatus.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
                    break;
                case "COMPLETED":
                    btnUpdateStatus.setText("Reset Status");
                    btnUpdateStatus.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                    break;
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
