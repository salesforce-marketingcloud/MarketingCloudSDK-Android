package com.salesforce.kp.wheresreid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * MainActivity is the primary activity.
 *
 * This activity extends AppCompatActivity to provide the primary interface for user interaction.
 *
 *
 * @author Salesforce (R) 2015.
 *
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* adds items to the action bar if present. */
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /**
         * Handles action bar item clicks
         * The action bar automatically handles clicks on the Home/Up button if
         * a parent activity in AndroidManifest.xml has been specified.
         */

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }
}
