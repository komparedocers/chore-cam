package com.choreocam.app.utils;

import android.util.Log;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import timber.log.Timber;

/**
 * Custom Timber tree that logs to Firebase Crashlytics in production
 */
public class CrashReportingTree extends Timber.Tree {

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            // Don't log verbose or debug in production
            return;
        }

        FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();

        // Log message
        crashlytics.log(priority + "/" + tag + ": " + message);

        // If there's an exception, record it
        if (t != null) {
            if (priority == Log.ERROR) {
                crashlytics.recordException(t);
            } else if (priority == Log.WARN) {
                crashlytics.recordException(new Exception("Warning: " + message, t));
            }
        } else if (priority == Log.ERROR) {
            // Log error without exception
            crashlytics.recordException(new Exception("Error: " + message));
        }
    }
}
