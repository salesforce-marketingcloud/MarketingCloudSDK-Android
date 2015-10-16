package com.salesforce.kp.wheresreid.utils;

import android.widget.EditText;

/**
 * UTILS
 * <p/>
 * A class of utility methods.
 * Created by Romina on 10/16/15.
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
