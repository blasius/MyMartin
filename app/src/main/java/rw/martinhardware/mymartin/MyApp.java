package rw.martinhardware.mymartin;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import io.objectbox.BoxStore;
import rw.martinhardware.mymartin.entities.MyObjectBox;

public class MyApp extends Application {

    private BoxStore boxStore;

    @Override
    public void onCreate() {
        super.onCreate();
        boxStore = MyObjectBox.builder().androidContext(this).build();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // Disable dark mode
    }

    public BoxStore getBoxStore() {
        return boxStore;
    }
}
