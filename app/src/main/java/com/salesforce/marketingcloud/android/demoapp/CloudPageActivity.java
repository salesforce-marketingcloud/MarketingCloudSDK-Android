package com.salesforce.marketingcloud.android.demoapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.webkit.WebView;
import android.widget.LinearLayout;
import com.exacttarget.etpushsdk.ETAnalytics;

/**
 * CloudPageActivity displays a Cloud Page.
 *
 * @author Salesforce &reg; 2015.
 */
public class CloudPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.action_cloudpage_inbox));

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayout.setGravity(Gravity.CENTER);

        WebView webView = new WebView(this);
        webView.loadUrl(this.getIntent().getExtras().getString("_x"));
        webView.getSettings().setJavaScriptEnabled(true);
        linearLayout.addView(webView);
        ETAnalytics.trackPageView(this.getIntent().getExtras().getString("_x"), "Cloud Page displayed");
        setContentView(linearLayout);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
    }

}
