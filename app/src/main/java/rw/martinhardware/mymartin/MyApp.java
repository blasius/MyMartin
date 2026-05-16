package rw.martinhardware.mymartin;

import android.app.Application;

import io.objectbox.BoxStore;
import rw.martinhardware.mymartin.entities.MyObjectBox;
import rw.martinhardware.mymartin.entities.SyncLog;
import rw.martinhardware.mymartin.entities.User;
import rw.martinhardware.mymartin.entities.Vehicle;

public class MyApp extends Application {

    private BoxStore boxStore;

    @Override
    public void onCreate() {
        super.onCreate();
        // Use the generated MyObjectBox (created by ObjectBox annotation processor)
        boxStore = MyObjectBox.builder().androidContext(this).build();
    }

    public BoxStore getBoxStore() {
        return boxStore;
    }
}
