package com.salesforce.marketingcloud.android.demoapp;

import android.preference.PreferenceActivity;
import android.util.Log;
import com.exacttarget.etpushsdk.ETPush;

public class BasePreferenceActivity extends PreferenceActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    /** Calling ETPush.activityPaused is required only if you are targeting earlier than Android API 14.
     * For apps targeting Android 14 or later, the SDK will implement these calls using
     * registerActivityLifecycleCallbacks().
     * If you are targeting earlier than Android API 14, you must also call ETPush.activityResumed(this)
     * in onResume method. */
    @Override
    protected void onPause() {
        super.onPause();

        try {
            // Let JB4A SDK know when each activity paused
            ETPush.activityPaused(this);
        } catch (Exception e) {
            if (ETPush.getLogLevel() <= Log.ERROR) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    /** Calling ETPush.activityResumed is required only if you are targeting earlier than Android API 14.
     * For apps targeting Android 14 or later, the SDK will implement these calls using
     * registerActivityLifecycleCallbacks().
     * If you are targeting earlier than Android API 14, you must also call ETPush.activityPaused(this)
     * in onPause method. */
    @Override
    protected void onResume() {
        super.onResume();
        try {
            // Let JB4A SDK know when each activity is resumed
            ETPush.activityResumed(this);
        } catch (Exception e) {
            if (ETPush.getLogLevel() <= Log.ERROR) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }
}
