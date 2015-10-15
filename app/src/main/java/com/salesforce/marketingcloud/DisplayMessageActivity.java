/**
 * Copyright (c) 2015 Salesforce Marketing Cloud.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.salesforce.marketingcloud;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.exacttarget.etpushsdk.ETPush;
import com.salesforce.marketingcloud.utils.Utils;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Iterator;

public class DisplayMessageActivity extends BaseActivity {
    private static final String TAG = Utils.formatTag(DisplayMessageActivity.class.getSimpleName()) ;
    private int currentPage = CONSTS.DISPLAY_MESSAGE_ACTIVITY;
    private long payloadReceived = -1;
    private String payloadStr = "";
    private String messageTitle = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_layout);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();

            if (extras == null) {
                // get fields from last push received (saved by SDK_ExplorerNotificationReceiver)
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ApplicationClass.context());

                payloadReceived = sp.getLong(CONSTS.KEY_PUSH_RECEIVED_DATE, -1);
                payloadStr = sp.getString(CONSTS.KEY_PUSH_RECEIVED_PAYLOAD, "");
                messageTitle = getString(R.string.display_last_message_activity_title);
            } else {
                payloadReceived = extras.getLong(CONSTS.KEY_PUSH_RECEIVED_DATE, -1);
                payloadStr = extras.getString(CONSTS.KEY_PUSH_RECEIVED_PAYLOAD);
                if (payloadStr == null) {
                    payloadStr = "";
                }
                messageTitle = getString(R.string.display_current_message_activity_title);
            }

            prepareDisplay(true);
        } else {
            payloadReceived = savedInstanceState.getLong(CONSTS.KEY_PUSH_RECEIVED_DATE, -1);
            payloadStr = savedInstanceState.getString(CONSTS.KEY_PUSH_RECEIVED_PAYLOAD);
            if (payloadStr == null) {
                payloadStr = "";
            }
            messageTitle = savedInstanceState.getString("messageTitle");
            if (messageTitle == null) {
                messageTitle = "";
            }
            prepareDisplay(false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CONSTS.KEY_CURRENT_PAGE, currentPage);
        outState.putLong(CONSTS.KEY_PUSH_RECEIVED_DATE, payloadReceived);
        outState.putString(CONSTS.KEY_PUSH_RECEIVED_PAYLOAD, payloadStr);
        outState.putString("messageTitle", messageTitle);
    }

    @Override
    protected void onResume() {
        super.onResume();

//        Utils.setActivityTitle(this, messageTitle);
//        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_drawer, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Utils.prepareMenu(currentPage, menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Boolean result = Utils.selectMenuItem(this, currentPage, item);
        return result != null ? result : super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void prepareDisplay(boolean firstOpen) {
        StringBuilder sb = new StringBuilder();

        if (payloadReceived == -1 | payloadStr == null) {
            // nothing to show since no push notification has been received since last installation.
            sb.append("<b>No Push notifications have been received since this app was installed.</b>  ");
        } else {

            // show previous payload
            sb.append("<b>Payload Sent on: ");

            // show date received
            Calendar payloadReceivedDate = Calendar.getInstance();
            payloadReceivedDate.setTimeInMillis(payloadReceived);

            android.text.format.DateFormat df = new android.text.format.DateFormat();
            sb.append(df.format("yyyy-MM-dd hh:mm:ss", payloadReceivedDate.getTime()));
            sb.append("</b> ");

            // convert JSON String of saved payload back to bundle to display
            JSONObject jo = null;

            try {
                jo = new JSONObject(payloadStr);
            } catch (Exception e) {
                if (ETPush.getLogLevel() <= Log.ERROR) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            if (jo != null) {
                sb.append("<b>Payload Sent with Message</b>  ");
                sb.append("<br/><br/>");
                sb.append("<i>Key/Value pairs:</i>  ");
                Iterator<String> iterator = jo.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    try {
                        Object value = jo.get(key);
                        sb.append("<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                        sb.append("<u>");
                        sb.append(key);
                        sb.append("</u>");
                        sb.append(" : ");
                        sb.append(value);
                    } catch (Exception e) {
                        if (ETPush.getLogLevel() <= Log.ERROR) {
                            Log.e(TAG, e.getMessage(), e);
                        }
                    }
                }

                try {
                    sb.append("<br/><br/>");
                    sb.append("<i>Custom Keys (Discount Code):</i>  ");
                    String payloadDiscountStr = "";
                    if (jo.has(CONSTS.KEY_PAYLOAD_DISCOUNT)) {
                        payloadDiscountStr = jo.getString(CONSTS.KEY_PAYLOAD_DISCOUNT);
                    }

                    if (!payloadDiscountStr.equals("")) {
                        // have an actual discount code
                        // CUSTOM KEYS
                        sb.append(payloadDiscountStr);

                        if (firstOpen) {
                            // if the Activity was refreshed, then don't flow to the discount screen.
                            Intent intent = new Intent(DisplayMessageActivity.this, DiscountActivity.class);
                            intent.putExtra(CONSTS.KEY_PUSH_RECEIVED_PAYLOAD, payloadStr);
                            startActivity(intent);
                        }
                    } else {
                        sb.append("n/a");
                        sb.append("<br/>");
                        sb.append("NOTE: No discount_code key was sent with this message.");
                    }
                } catch (Exception e) {
                    sb.append("Problem displaying Custom Keys (Discount Code).  Check logcat.");
                    if (ETPush.getLogLevel() <= Log.ERROR) {
                        Log.e(TAG, e.getMessage(), e);
                    }

                }
            } else {
                // show current push notification received, but payload is null
                sb.append("<b>Problem parsing payload from last push notification. Check logcat.</b>  ");
            }
        }

        Utils.setWebView(this, R.id.notificationWV, sb, false);
    }
}
