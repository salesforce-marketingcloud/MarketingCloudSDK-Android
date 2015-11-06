package com.salesforce.marketingcloud.android.demoapp.utils;

import android.widget.EditText;

/**
 * Utils is a class of utility methods.
 *
 * @author Salesforce (R) 2015.
 */
public class Utils {


    public static void flashError(final EditText et, String message) {
        et.setError(message);

        // reset message after 3 second delay
        et.postDelayed(new Runnable() {
            public void run() {
                et.setError(null);
            }
        }, 3000);

    }

}
