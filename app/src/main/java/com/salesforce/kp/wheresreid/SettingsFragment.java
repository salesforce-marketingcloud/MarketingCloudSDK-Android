package com.salesforce.kp.wheresreid;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.exacttarget.etpushsdk.ETException;
import com.exacttarget.etpushsdk.ETPush;
import com.salesforce.kp.wheresreid.R;
import com.salesforce.kp.wheresreid.utils.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@TargetApi(11)
public class SettingsFragment extends PreferenceFragment {
    // Set of current tags
    private Set<String> allTags;
    private ETPush pusher;
    private SharedPreferences sp;
    private PreferenceScreen prefScreen;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        this.sp = getActivity().getPreferences(Context.MODE_PRIVATE);
        this.prefScreen = getPreferenceScreen();

        try {
            this.pusher = ETPush.getInstance();
            // get the tags
            Log.e("TAGSS", this.pusher.getTags().toString());
            storeAllTags(this.pusher.getTags());
        } catch (Exception e){
            e.printStackTrace();
        }

        // getFragmentManager().executePendingTransactions();

        //
        // SUBSCRIBER KEY PREFERENCE
        //
        // KEY_PREF_SUBSCRIBER_KEY must match the key of the EditTextPreference correspondent to the subscriber key.
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
                            // save the preference to Shared Preferences
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

        final Preference et = findPreference("pref_new_tag");

        et.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {

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

                return true;
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    /**
     * Receives a Set of Tags, those are stored in Shared preferences
     *
     * @param pSet Parameter 1.
     */
    private void storeAllTags(Set<String> pSet) throws ETException{
        // Retrieve the tags stored in Shared preferences
        Set<String> setToLoad = sp.getStringSet("tags", null) == null ? new HashSet<String>() : sp.getStringSet("tags", null);
        // Adds the tags from the Set passed as parameter
        for (String t : pSet){
            setToLoad.add(t);
        }
        // Stores the tags in Shared Preferences
        SharedPreferences.Editor editor = sp.edit();
        Set<String> setToSave = new HashSet<String>();
        setToSave.addAll(setToLoad);
        editor.putStringSet("tags", setToSave);
        editor.commit();
        allTags = setToLoad;
    }

    /**
     * Receives a Tag to Store in Shared preferences
     *
     * @param tag Parameter 1.
     * @return A new instance of fragment SettingsFragment.
     */
    private void addNewTag(String tag) throws ETException{
        Set tempSet = new HashSet<String>();
        tempSet.add(tag);
        storeAllTags(tempSet);
    }

    /**
     * Configures the Shared Preferences section to be displayed
     *
     * @param String Parameter 1.
     * @return A new instance of fragment SettingsFragment.
     */
    private void configureTags(){
        // Creates a new PreferenceCategory if is not already created..
        PreferenceCategory tagsSection = (PreferenceCategory)this.prefScreen.findPreference("pref_tag_section");
        if (this.prefScreen.findPreference("pref_tag_section") == null) {
            tagsSection = new PreferenceCategory(getActivity());
            tagsSection.setTitle(getResources().getString(R.string.pref_tag_category_title));
            tagsSection.setKey("pref_tag_section");
            // Creates the Add new Tag section.
            EditTextPreference et = new EditTextPreference(getActivity());
            et.setDefaultValue("");
            et.setDialogMessage(getResources().getString(R.string.pref_new_tag_summ));
            et.setKey("pref_new_tag");
            et.setSummary(getResources().getString(R.string.pref_new_tag_summ));
            et.setTitle(getResources().getString(R.string.pref_new_tag));
            // Adds the PreferenceCategory to the Preference's screen.
            this.prefScreen.addPreference(tagsSection);
            // Adds the new Tag section to the PreferenceCategory.
            tagsSection.addPreference(et);
        }
        // Creates the rows out of the tag's list.
        for (String tag : this.allTags){
            addTagCheckbox(tagsSection, tag);
        }
    }

    /**
     * Creates a row from the Tag passed as parameter to be displayed.
     *
     * @param prefCat Parameter 1.
     * @param tag Parameter 2.
     */
    private void addTagCheckbox(PreferenceCategory prefCat, final String tag) {
        // Creates a new row if is not already created for the Tag.
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
                    // Adds the Tag to the Pusher instance if it is checked, removes it otherwise.
                    Boolean enabled = (Boolean) newValue;
                    try {
                        if (enabled) {
                            pusher.addTag(tag);
                        } else {
                            pusher.removeTag(tag);
                        }
                    } catch (ETException e) {
                        if (pusher.getLogLevel() <= Log.ERROR) {
                            Log.e("TAG", e.getMessage(), e);
                        }
                    }
                    return true;
                }
            });
            // Adds the row to the section.
            prefCat.addPreference(cbp);
        }
    }

}
