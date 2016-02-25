/*
 * Copyright (c) 2016, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license.
 * For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.marketingcloud.android.demoapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exacttarget.etpushsdk.ETAnalytics;

/**
 * A placeholder fragment containing the home view.
 *
 * @author Salesforce &reg; 2015.
 */
public class MainActivityFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ETAnalytics.trackPageView("data://LaunchedMainActivity", "Main Activity Launched");
        ETAnalytics.trackPageView("LaunchedMainActivity", "Main Activity Launched");
        ETAnalytics.trackPageView("MainActivityLaunched");
        return inflater.inflate(R.layout.fragment_main, container, false);
    }
}
