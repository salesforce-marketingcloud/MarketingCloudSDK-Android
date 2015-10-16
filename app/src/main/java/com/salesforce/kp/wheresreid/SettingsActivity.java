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
 * SDK_ExplorerSettingsActivity is the primary settings activity within the JB4A SDK Explorer.
 * <p/>
 * This activity extends PreferenceActivity to provide the primary settings interface to collect user preferences.
 * <p/>
 * It handles settings that would normally be included within your customer facing app.  These settings that are sent to
 * the Marketing Cloud will take up to 15 minutes to take effect.  So, after setting or changing these settings, you
 * should wait at least 15 minutes before sending a message either from the Marketing Cloud or from the SDK_ExplorerSendMessagesDialog
 * found within this app.
 * <p/>
 * We have setup the JB4A SDK Explorer to require several settings before allowing the user to provide permission to receive notifications.
 * <p/>
 * Your app design may be different (for example, you may set notifications on by default in your Application class if you assume
 * permission was given by the user due to the permission settings set within the Google Play definition.
 * <p/>
 * Settings:
 * <p/>
 * 1) First and Last Name.
 * These are attributes saved in the Salesforce Marketing Cloud.
 * <p/>
 * 2) Subscriber Key.
 * This attribute provides a primary key for the Contact record stored in the Salesforce Marketing Cloud.
 * <p/>
 * 3) Enable Push Preferences
 * This preference is the heart of the SDK. Without Push turned on, not much will happen!
 * <p/>
 * 4) Enable Location (for Geo Fencing) Preference
 * This preference will test Geo Fencing.  For the JB4A SDK Explorer, we have setup several fences around the
 * national parks.  A tool like Fake GPS can be used to test these location settings.
 * <p/>
 * 5) Custom Ringtone and Custom Vibration
 * This app shows a way to completely customize the way notification sounds work.  Within the Marketing Cloud, you can
 * normally set to use the Default of a Custom Sound.  This SDK Explorer, takes it a step further and will have it's own custom
 * sound (sports whistle) or allow the user to select their own sound.  The settings from the Marketing Cloud are
 * essentially ignored.
 * <p/>
 * All of this work is done locally and not through the SDK.
 * <p/>
 * 6) Team Tags
 * The Team Tags show examples of using Tags to allow your customers to select which type of notification they are interested in
 * receiving.  The example within this JB4A SDK Explorer are Activities.  The tags are sent to the Marketing Cloud and can
 * be used to select customers to send the notification to.
 * <p/>
 * 7) Custom Keys
 * In the JB4A SDK Explorer App in the Marketing Cloud, we set up a custom key to drive processing within the app when the user
 * opens the notification.  This processing shows how to save the data sent.  As well as how to show different Activity or
 * at least different information in the Activity as dictated by the value of the custom key.
 *
 * @author pvandyk
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