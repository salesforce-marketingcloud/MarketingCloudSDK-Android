package com.salesforce.marketingcloud.android.demoapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.exacttarget.etpushsdk.ETAnalytics;
import com.exacttarget.etpushsdk.ETException;
import com.exacttarget.etpushsdk.ETLocationManager;
import com.exacttarget.etpushsdk.ETPush;
import com.exacttarget.etpushsdk.data.Attribute;

/**
 * MainActivity is the primary activity.
 *
 * This activity extends AppCompatActivity to provide the primary interface for user interaction.
 *
 * @author Salesforce &reg; 2015.
 */
public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MAIN ACTIVITY";
    private ActivityPermissionDelegate permissionDelegate;
    private ETPush etPush;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WebView markdownView = (WebView) findViewById(R.id.markdownView);
        markdownView.getSettings().setJavaScriptEnabled(true);
        markdownView.loadUrl(getResources().getString(R.string.readme_remote_url));
        ETAnalytics.trackPageView(getResources().getString(R.string.readme_remote_url), "Displaying Learning Apps Documentation On Device: " );
        markdownView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });
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
        else if (id == R.id.action_original_docs){
            startActivity(new Intent(this, OriginalDocsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult()");
        if (!permissionDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //@Override
    public void onReadyForPush(ETPush etPush) {
        ETAnalytics.trackPageView("data://OnPushReady", "Marketing Cloud SDK Ready for Push Messages");
        this.etPush = etPush;
        try {
            /*
                A good practice is to add the application's version name as a tag that can later
                be used to target push notifications to specific application versions.
             */
            etPush.addTag(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);

             /*
                Android SDK v23 aka Marshmallow introduced a new Android permission model by which
                the application developer must request permissions when they are used as opposed to
                requesting them at installation time.
             */
            ActivityPermissionDelegate.PermissionRequest request = new ActivityPermissionDelegate.PermissionRequest(
                    "Location",
                    new ActivityPermissionDelegate.PermissionCallback() {
                        @Override
                        public void handleGranted() {
                            Log.i(TAG, "handleGranted()");
                            try {
                                ETLocationManager etLocationManager = ETLocationManager.getInstance();
                                etLocationManager.startWatchingLocation();
                                etLocationManager.startWatchingProximity();
                            } catch (ETException e) {
                                Log.e(TAG, e.getMessage(), e);
                            }
                        }

                        @Override
                        public void handleRationale(final ActivityPermissionDelegate.PermissionRationaleRetryBehavior behavior) {
                            Log.w(TAG, "handleRationale()");
                            behavior.requestAgain();
                        }

                        @Override
                        public void handleDenied() {
                            Log.e(TAG, "handleDenied()");
                            Toast.makeText(MainActivity.this, "Denied", Toast.LENGTH_LONG).show();
                        }
                    },
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            );
            permissionDelegate = new ActivityPermissionDelegate(this, new ActivityPermissionDelegate.PermissionRequest[]{request});
            permissionDelegate.requestPermission("location");

        } catch (ETException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
