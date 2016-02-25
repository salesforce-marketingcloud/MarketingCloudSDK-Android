/*
 * Copyright (c) 2016, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license.
 * For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.marketingcloud.android.demoapp.utils;

import android.widget.EditText;

/**
 * Utils is a class of utility methods.
 *
 * @author Salesforce &reg; 2015.
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
