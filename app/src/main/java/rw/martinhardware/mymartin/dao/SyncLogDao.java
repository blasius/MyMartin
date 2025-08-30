package rw.martinhardware.mymartin.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import rw.martinhardware.mymartin.entities.SyncLog;

@Dao
public interface SyncLogDao {

    @Insert
    long insert(SyncLog log);

    @Query("SELECT * FROM sync_log ORDER BY lastSyncTime DESC LIMIT 1")
    SyncLog getLastSync();
}
