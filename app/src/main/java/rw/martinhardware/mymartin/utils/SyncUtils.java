package rw.martinhardware.mymartin.utils;

import android.content.Context;

public class SyncUtils {
    private static final String PREFS_NAME = "app_prefs";
    private static final String KEY_LAST_SYNC = "last_sync";

    public static void saveLastSync(Context context, long timestamp) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putLong(KEY_LAST_SYNC, timestamp)
                .apply();
    }

    public static long getLastSync(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getLong(KEY_LAST_SYNC, 0);
    }
}
