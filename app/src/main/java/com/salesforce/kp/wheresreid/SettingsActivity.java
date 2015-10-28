package com.salesforce.kp.wheresreid;

import android.os.Bundle;
import com.exacttarget.etpushsdk.util.EventBus;

/**
 * SettingsActivity is the primary settings activity.
 *
 * This activity extends PreferenceActivity to provide the primary settings interface to collect user preferences.
 *
 * It handles settings that would normally be included within your customer facing app.  These settings that are sent to
 * the Marketing Cloud will take up to 15 minutes to take effect.  So, after setting or changing these settings, you
 * should wait at least 15 minutes before sending a message either from the Marketing Cloud or from the SDK_ExplorerSendMessagesDialog
 * found within this app.
 *
 * Your app design may be different (for example, you may set notifications on by default in your Application class if you assume
 * permission was given by the user due to the permission settings set within the Google Play definition.
 *
 * Settings:
 *
 * 1) Subscriber Key.
 * This attribute provides a primary key for the Contact record stored in the Salesforce Marketing Cloud.
 *
 * @author Salesforce (R) 2015.
 */

public class SettingsActivity extends BasePreferenceActivity {
    private static final String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getInstance().register(this);

        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new SettingsFragment()).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getInstance().unregister(this);
    }

}