package rw.martinhardware.mymartin.ui.deliveries;

public class Delivery {
    private String id;
    private String customerName;
    private String address;
    private String status; // PENDING, IN_PROGRESS, COMPLETED
    private String time;
    private String phoneNumber;
    private String packageInfo;
    private double latitude;
    private double longitude;
    private String priority; // HIGH, MEDIUM, LOW

    public Delivery(String id, String customerName, String address, String status, 
                   String time, String phoneNumber, String packageInfo, 
                   double latitude, double longitude, String priority) {
        this.id = id;
        this.customerName = customerName;
        this.address = address;
        this.status = status;
        this.time = time;
        this.phoneNumber = phoneNumber;
        this.packageInfo = packageInfo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.priority = priority;
    }

    // Getters
    public String getId() { return id; }
    public String getCustomerName() { return customerName; }
    public String getAddress() { return address; }
    public String getStatus() { return status; }
    public String getTime() { return time; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getPackageInfo() { return packageInfo; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getPriority() { return priority; }

    // Setters
    public void setStatus(String status) { this.status = status; }
    public void setPriority(String priority) { this.priority = priority; }
}
