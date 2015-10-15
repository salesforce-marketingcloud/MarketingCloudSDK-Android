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

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.exacttarget.etpushsdk.ETPush;
import com.salesforce.marketingcloud.utils.Utils;
import com.salesforce.marketingcloud.R;

import org.json.JSONObject;

import java.io.InputStream;

public class DiscountActivity extends BaseActivity {
    private static final String TAG = Utils.formatTag(DiscountActivity.class.getSimpleName()) ;
    private int currentPage = CONSTS.DISCOUNT_ACTIVITY;
    private String payloadStr = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discount_layout);

        if (savedInstanceState == null) {
            payloadStr = getIntent().getExtras().getString(CONSTS.KEY_PUSH_RECEIVED_PAYLOAD, "");
        } else {
            payloadStr = savedInstanceState.getString(CONSTS.KEY_PUSH_RECEIVED_PAYLOAD, "");
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CONSTS.KEY_CURRENT_PAGE, currentPage);
        outState.putString(CONSTS.KEY_PUSH_RECEIVED_PAYLOAD, payloadStr);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Utils.setActivityTitle(this, R.string.discount_activity_title);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        prepareDisplay();
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

    private void prepareDisplay() {

        if (!payloadStr.equals("")) {
            // convert JSON String of saved payload back to bundle to display
            JSONObject jo = null;

            try {
                jo = new JSONObject(payloadStr);

                if (jo != null) {

                    String discountStr = jo.getString(CONSTS.KEY_PAYLOAD_DISCOUNT);

                    if (!discountStr.equals("")) {
                        int discount = Integer.valueOf(discountStr);
                        String message = jo.getString(CONSTS.KEY_PAYLOAD_ALERT);
                        String imageFile;

                        switch (discount) {
                            case 10:
                                imageFile = "10percentoffQR.png";
                                break;
                            case 15:
                                imageFile = "15percentoffQR.png";
                                break;
                            case 20:
                                imageFile = "20percentoffQR.png";
                                break;
                            default:
                                imageFile = "10percentoffQR.png";
                                break;
                        }

                        TextView messageTV = (TextView) findViewById((R.id.messageTV));
                        messageTV.setText(message + "\n\n" + "Custom Keys (Discount Code):  " + discount);

                        try {
                            InputStream ims = getAssets().open(imageFile);
                            // load image as Drawable
                            Drawable d = Drawable.createFromStream(ims, null);

                            ImageView qrIV = (ImageView) findViewById(R.id.QRcodeIV);
                            qrIV.setImageDrawable(d);
                        } catch (Exception e) {
                            if (ETPush.getLogLevel() <= Log.ERROR) {
                                Log.e(TAG, e.getMessage(), e);
                            }
                        }
                    } else {
                        showNoDiscounts();
                    }
                }
            } catch (Exception e) {
                if (ETPush.getLogLevel() <= Log.ERROR) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        } else {
            showNoDiscounts();
        }

    }

    private void showNoDiscounts() {
        TextView messageTV = (TextView) findViewById((R.id.messageTV));
        messageTV.setText("Problem finding discount information.");
        messageTV.setTypeface(null, Typeface.BOLD);
    }
}
