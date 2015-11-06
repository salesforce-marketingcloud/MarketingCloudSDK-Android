package com.salesforce.marketingcloud.android.demoapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.exacttarget.etpushsdk.ETException;
import com.exacttarget.etpushsdk.ETPush;
import com.salesforce.marketingcloud.android.demoapp.utils.Utils;

import java.util.HashSet;
import java.util.Set;

/**
 * SettingsFragment handles settings that would normally be included within your customer facing app.
 * These settings that are sent to the Marketing Cloud will take up to 15 minutes to take effect.
 * So, after setting or changing these settings, you should wait at least 15 minutes before sending
 * a message from the Marketing Cloud.
 *
 * Your app design may be different (for example, you may set notifications on by default in your
 * Application class if you assume permission was given by the user due to the permission settings
 * set within the Google Play definition.
 * <p/>
 * Settings:
 * <ol>
 *     <li>
 *         <b>Subscriber Key</b>
 *         <br/>
 *         This attribute provides a primary key for the Contact record stored in the Salesforce Marketing Cloud.
 *     </li>
 *     <li>
 *         <b>Tags</b>
 *         <br/>
 *         The Tags section show examples of using Tags to allow your customers to select which type
 *         of notification they are interested in receiving, and create new tags.
 *         The tags are sent to the Marketing Cloud and can be used to select customers to send the notification to.
 *     </li>
 * </ol>
 *
 *
 *
 * @author Salesforce &reg; 2015.
 */
public class SettingsFragment extends PreferenceFragment {
    /* Current set of tags */
    private Set<String> allTags;
    private ETPush pusher;
    private SharedPreferences sp;
    private PreferenceScreen prefScreen;


    /**
     * Retrieves the subscriber key and tags preference, listen for changes and propagate them to the SDK.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        this.sp = getActivity().getPreferences(Context.MODE_PRIVATE);
        this.prefScreen = getPreferenceScreen();

        try {
            this.pusher = ETPush.getInstance();

            /* Get and store tags */
            Log.i("TAGS", this.pusher.getTags().toString());
            this.allTags = this.pusher.getTags() != null ? this.pusher.getTags() : new HashSet<String>();
            storeAllTags(this.allTags);
        } catch (Exception e){
            e.printStackTrace();
        }

        /** SUBSCRIBER KEY PREFERENCE */

        /* KEY_PREF_SUBSCRIBER_KEY must match the key of the EditTextPreference correspondent to the subscriber key. */
        final String KEY_PREF_SUBSCRIBER_KEY = "pref_subscriber_key";

        final Preference skPref = findPreference(KEY_PREF_SUBSCRIBER_KEY);
        if (!this.sp.getString(KEY_PREF_SUBSCRIBER_KEY, "").isEmpty()) {
            skPref.setSummary(this.sp.getString(KEY_PREF_SUBSCRIBER_KEY, ""));
        }

        skPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {

                final EditTextPreference skETP = (EditTextPreference) prefScreen.findPreference(KEY_PREF_SUBSCRIBER_KEY);

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
                            /* Save the preference to Shared Preferences */
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString(KEY_PREF_SUBSCRIBER_KEY, newSubscriberKey);
                            editor.commit();
                            try {
                                pusher.setSubscriberKey(newSubscriberKey);
                            } catch (ETException e) {
                                if (ETPush.getLogLevel() <= Log.ERROR) {
                                    Log.e("TAG", e.getMessage(), e);
                                }
                            }
                        }
                        d.dismiss();
                    }
                });
                return true;
            }
        });

        this.configureTags();

        /**
         * Add new tags.
         */
        final Preference et = findPreference("pref_new_tag");

        et.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {

                if (pusher != null) {


                    final EditTextPreference skETP = (EditTextPreference) prefScreen.findPreference("pref_new_tag");

                    final AlertDialog d = (AlertDialog) skETP.getDialog();
                    final EditText skET = skETP.getEditText();
                    skET.setText(sp.getString("pref_new_tag", ""));

                    Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                    b.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(android.view.View v) {
                            String newTagValue = skET.getText().toString().trim();
                            if (newTagValue.isEmpty()) {
                                Utils.flashError(skET, getString(R.string.error_cannot_be_blank));
                                return;
                            } else {
                                try {
                                    addNewTag(newTagValue);
                                } catch (ETException e) {
                                    if (ETPush.getLogLevel() <= Log.ERROR) {
                                        Log.e("TAG", e.getMessage(), e);
                                    }
                                }
                                configureTags();
                            }

                            d.dismiss();
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "There was a problem while loading SDK, unable to add new Tags", Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });
    }

    /**
     * Receives a Set of tags and adds them to the Set of tags in Shared Preferences.
     *
     * @param pSet tags to be stored.
     */
    private void storeAllTags(Set<String> pSet) {
        /* Retrieves the tags stored in Shared preferences */
        //Set<String> setToLoad = sp.getStringSet("tags", null) == null ? new HashSet<String>() : sp.getStringSet("tags", null);
        Set<String> setToLoad =  sp.getStringSet("tags", new HashSet<String>());
        /* Adds the tags from the Set passed as parameter */
        for (String t : pSet){
            setToLoad.add(t);
        }
        /* Stores the tags in Shared Preferences */
        SharedPreferences.Editor editor = sp.edit();
        Set<String> setToSave = new HashSet<>();
        setToSave.addAll(setToLoad);
        editor.putStringSet("tags", setToSave);
        editor.commit();
        allTags = setToLoad;
    }

    /**
     * Receives a Tag to store in Shared preferences
     *
     * @param tag a new Tag to be added.
     *
     */
    private void addNewTag(String tag) throws ETException{
        Set tempSet = new HashSet<>();
        tempSet.add(tag);
        pusher.addTag(tag);
        storeAllTags(tempSet);
    }

    /**
     * Configures the Shared Preferences section to be displayed.
     */
    private void configureTags(){
        /* Create a new PreferenceCategory if not already created. */
        PreferenceCategory tagsSection = (PreferenceCategory)this.prefScreen.findPreference("pref_tag_section");
        if (this.prefScreen.findPreference("pref_tag_section") == null) {
            tagsSection = new PreferenceCategory(getActivity());
            tagsSection.setTitle(getResources().getString(R.string.pref_tag_category_title));
            tagsSection.setKey("pref_tag_section");
            /* Create 'About' preference */
            Preference about = new Preference(getActivity());
            about.setTitle(getResources().getString(R.string.pref_tag_about));
            about.setSelectable(false);
            about.setSummary(getResources().getString(R.string.tag_help));
            /* Create the Add new Tag section. */
            EditTextPreference et = new EditTextPreference(getActivity());
            et.setDefaultValue("");
            et.setDialogMessage(getResources().getString(R.string.pref_new_tag_summ));
            et.setKey("pref_new_tag");
            et.setSummary(getResources().getString(R.string.pref_new_tag_summ));
            et.setTitle(getResources().getString(R.string.pref_new_tag));
            /* Add the PreferenceCategory to the Preference's screen. */
            this.prefScreen.addPreference(tagsSection);
            /* Add the 'About' section to Tags section */
            tagsSection.addPreference(about);
            /* Add the new Tag section to the PreferenceCategory. */
            tagsSection.addPreference(et);
        }

        /* Create rows from list of tags. */
        for (String tag : this.allTags){
            addTagCheckbox(tagsSection, tag);
        }
    }

    /**
     * Creates a row from the tag passed in as parameter to be displayed.
     *
     * @param prefCat  the section where the Tag will be displayed.
     * @param tag      the Tag to be displayed on the screen.
     */
    private void addTagCheckbox(PreferenceCategory prefCat, final String tag) {
        /* Creates a new row if is not already created for the Tag. */
        CheckBoxPreference cbp = (CheckBoxPreference) this.prefScreen.findPreference(tag);
        if (cbp == null) {
            cbp = new CheckBoxPreference(getActivity());
            cbp.setKey(tag);
            cbp.setTitle(tag);
            cbp.setSummary("Receive notifications for " + tag);
            cbp.setDefaultValue(Boolean.TRUE);
            cbp.setChecked(true);

            cbp.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference pref, Object newValue) {
                    /* Add the Tag to the Pusher instance if checked, else remove it. */
                    Boolean enabled = (Boolean) newValue;
                    try {
                        if (enabled) {
                            pusher.addTag(tag);
                        } else {
                            pusher.removeTag(tag);
                        }
                    } catch (ETException e) {
                        if (ETPush.getLogLevel() <= Log.ERROR) {
                            Log.e("TAG", e.getMessage(), e);
                        }
                    }
                    return true;
                }
            });
            /* Add row to section. */
            prefCat.addPreference(cbp);
        }
    }

}
