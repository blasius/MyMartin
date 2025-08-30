package rw.martinhardware.mymartin.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sync_log")
public class SyncLog {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    private long lastSyncTime; // epoch millis

    // Getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public long getLastSyncTime() { return lastSyncTime; }
    public void setLastSyncTime(long lastSyncTime) { this.lastSyncTime = lastSyncTime; }
}
