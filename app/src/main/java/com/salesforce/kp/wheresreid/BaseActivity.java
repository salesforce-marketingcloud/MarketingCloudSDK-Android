package com.salesforce.kp.wheresreid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.exacttarget.etpushsdk.ETLocationManager;
import com.exacttarget.etpushsdk.ETPush;

public class BaseActivity extends AppCompatActivity {

    //private static final String TAG = Utils.formatTag(BaseActivity.class.getSimpleName());

    @Override
    protected void onPause() {
        super.onPause();

        try {
            // Let JB4A SDK know when each activity paused
            ETPush.activityPaused(this);
            Log.e("PAUSED", "PAUSED");
            ETLocationManager.getInstance().startWatchingLocation();

        } catch (Exception e) {
            if (ETPush.getLogLevel() <= Log.ERROR) {
                //Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            // Let JB4A SDK know when each activity is resumed
            ETPush.activityResumed(this);
            Log.e("RESUMED", "RESUMED");
            ETLocationManager.getInstance().stopWatchingLocation();

        } catch (Exception e) {
            if (ETPush.getLogLevel() <= Log.ERROR) {
                //Log.e(TAG, e.getMessage(), e);
            }
        }
        /*
        if (SDK_ExplorerApp.getQuitAppNow()) {
            finish();
        }
        */
    }
}
