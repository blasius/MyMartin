package rw.martinhardware.mymartin.entities;

public class SyncLog {

    private Long id;

    private long lastSyncTime; // epoch millis

    // Getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public long getLastSyncTime() { return lastSyncTime; }
    public void setLastSyncTime(long lastSyncTime) { this.lastSyncTime = lastSyncTime; }
}
