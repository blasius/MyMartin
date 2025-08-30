package rw.martinhardware.mymartin.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

import rw.martinhardware.mymartin.entities.Vehicle;

@Dao
public interface VehicleDao {

    @Insert
    long insert(Vehicle vehicle);

    @Update
    void update(Vehicle vehicle);

    @Delete
    void delete(Vehicle vehicle);

    @Query("SELECT * FROM vehicles ORDER BY inspectionDate ASC")
    List<Vehicle> getAllVehicles();

    @Query("SELECT * FROM vehicles WHERE id = :id LIMIT 1")
    Vehicle findById(Long id);
}
