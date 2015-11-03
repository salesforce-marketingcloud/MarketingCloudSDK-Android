package com.salesforce.kp.wheresreid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.webkit.WebView;
import android.widget.LinearLayout;

public class CloudPageActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prepareDisplay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
    }

    /**
     *
     */
    private void prepareDisplay() {
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

        setContentView(linearLayout);

    }
}
