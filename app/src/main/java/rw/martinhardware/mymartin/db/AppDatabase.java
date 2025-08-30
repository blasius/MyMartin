package rw.martinhardware.mymartin.db;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import rw.martinhardware.mymartin.dao.SyncLogDao;
import rw.martinhardware.mymartin.dao.VehicleDao;
import rw.martinhardware.mymartin.entities.SyncLog;
import rw.martinhardware.mymartin.entities.Vehicle;

@Database(entities = {Vehicle.class, SyncLog.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract VehicleDao vehicleDao();
    public abstract SyncLogDao syncLogDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "vehicles-db"
                    ).fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }
}
