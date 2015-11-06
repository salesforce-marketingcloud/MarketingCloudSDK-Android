package com.salesforce.marketingcloud.android.demoapp;

import android.os.Bundle;

/**
 * SettingsActivity is the primary settings activity.
 *
 * This activity extends BaseActivity and provides the primary settings interface to collect user preferences.
 *
 * It is implemented calling a SettingsFragment class.
 *
 *
 * @author Salesforce &reg; 2015.
 */

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setHomeButtonEnabled(true);

        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new SettingsFragment()).commit();
    }
}