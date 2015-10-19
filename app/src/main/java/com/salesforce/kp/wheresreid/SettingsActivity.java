package com.salesforce.kp.wheresreid;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.exacttarget.etpushsdk.ETException;
import com.exacttarget.etpushsdk.ETPush;
import com.salesforce.kp.wheresreid.utils.Utils;

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

public class SettingsActivity extends PreferenceActivity {
    private static final String TAG = "SettingsActivity";
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        addPreferencesFromResource(R.xml.preferences);

        //
        // SUBSCRIBER KEY PREFERENCE
        //
        // KEY_PREF_SUBSCRIBER_KEY must match the key of the EditTextPreference correspondent to the subscriber key.
        final String KEY_PREF_SUBSCRIBER_KEY = "pref_subscriber_key";

        final Preference skPref = findPreference(KEY_PREF_SUBSCRIBER_KEY);
        if (!sp.getString(KEY_PREF_SUBSCRIBER_KEY, "").isEmpty()) {
            skPref.setSummary(sp.getString(KEY_PREF_SUBSCRIBER_KEY, ""));
        }

        skPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {

                PreferenceScreen prefSet = getPreferenceScreen();

                final EditTextPreference skETP = (EditTextPreference) prefSet.findPreference(KEY_PREF_SUBSCRIBER_KEY);

                final AlertDialog d = (AlertDialog) skETP.getDialog();
                final EditText skET = skETP.getEditText();
                skET.setText(sp.getString(KEY_PREF_SUBSCRIBER_KEY, ""));

                Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(android.view.View v) {
                        String newSubscriberKey = skET.getText().toString().trim();
                        if (newSubscriberKey.isEmpty()) {
                            Utils.flashError(skET, getString(R.string.error_cannot_be_blank));
                            return;
                        } else {

                            // save the preference to Shared Preferences
                            updatePreferencesForKey(KEY_PREF_SUBSCRIBER_KEY, newSubscriberKey);
                            skPref.setSummary(newSubscriberKey);

                            try {
                                ETPush.getInstance().setSubscriberKey(newSubscriberKey);
                            } catch (ETException e) {
                                if (ETPush.getLogLevel() <= Log.ERROR) {
                                    Log.e(TAG, e.getMessage(), e);
                                }
                            }
                        }

                        d.dismiss();
                    }
                });

                return true;
            }
        });
    }


    //
    // updatePreferencesForKey
    //
    // Update the Shared Preferences file for the given String key.
    //
    private void updatePreferencesForKey(String key, String value) {
        sp.edit().putString(key, value).apply();
    }
}