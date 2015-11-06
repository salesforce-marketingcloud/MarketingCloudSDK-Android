package com.salesforce.marketingcloud.android.demoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * MainActivity is the primary activity.
 *
 * This activity extends AppCompatActivity to provide the primary interface for user interaction.
 *
 * @author Salesforce &reg; 2015.
 */
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        prepareDisplay();
    }

    /**
     *  Add items to the action bar if present.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Handles action bar item clicks
     * The action bar automatically handles clicks on the Home/Up button if
     * a parent activity in AndroidManifest.xml has been specified.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));

        } else if (id == R.id.action_map) {
            startActivity(new Intent(this, MapsActivity.class));
        }
        else if (id == R.id.action_cloudpage_inbox){
            startActivity(new Intent(this, CloudPageInboxActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Loads the webView with project's readme at github
     */
    private void prepareDisplay(){
        WebView markdownView = (WebView) findViewById(R.id.markdownView);
        markdownView.getSettings().setJavaScriptEnabled(true);
        markdownView.loadUrl(getResources().getString(R.string.readme_remote_url));
        markdownView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });
    }
}
