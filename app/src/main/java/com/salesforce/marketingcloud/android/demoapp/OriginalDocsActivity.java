package com.salesforce.marketingcloud.android.demoapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.exacttarget.etpushsdk.ETAnalytics;
import com.exacttarget.etpushsdk.adapter.CloudPageListAdapter;
import com.exacttarget.etpushsdk.data.Message;

/**
 * CloudPageInboxActivity works as an inbox for the Cloud Pages received.
 *
 * @author Salesforce &reg; 2015.
 */
public class OriginalDocsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WebView markdownView = (WebView) findViewById(R.id.markdownView);
        markdownView.getSettings().setJavaScriptEnabled(true);
        markdownView.loadUrl(getResources().getString(R.string.official_remote_url));
        markdownView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });
        ETAnalytics.trackPageView("data://OriginalDocsActivity", getResources().getString(R.string.official_remote_url));
    }

    /**
     * Navigates back to parent's Activity: MainActivity
     *
     * @param item which is the reference to the parent's activity: MainActivity
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
