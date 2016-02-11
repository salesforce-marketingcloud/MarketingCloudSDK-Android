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
