/**
 * Copyright (c) 2015 Salesforce Marketing Cloud.
 * All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * <p/>
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * <p/>
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 * <p/>
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * <p/>
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

package com.salesforce.marketingcloud.utils;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.exacttarget.etpushsdk.ETException;
import com.exacttarget.etpushsdk.ETLocationManager;
import com.exacttarget.etpushsdk.ETPush;
import com.exacttarget.etpushsdk.data.Attribute;
import com.salesforce.marketingcloud.ApplicationClass;
import com.salesforce.marketingcloud.CONSTS;
import com.salesforce.marketingcloud.CONSTS_API;
import com.salesforce.marketingcloud.DisplayMessageActivity;
import com.salesforce.marketingcloud.R;
import com.salesforce.marketingcloud.SendMessageActivity;
//import com.exacttarget.jb4a.sdkexplorer.SDK_ExplorerCloudPageInboxActivity;
//import com.exacttarget.jb4a.sdkexplorer.SDK_ExplorerDebugSettingsActivity;
//import com.exacttarget.jb4a.sdkexplorer.DisplayMessageActivity;
//import com.exacttarget.jb4a.sdkexplorer.SDK_ExplorerEulaActivity;
//import com.exacttarget.jb4a.sdkexplorer.SDK_ExplorerInfoActivity;
//import com.exacttarget.jb4a.sdkexplorer.SDK_ExplorerLocationsActivity;
//import com.exacttarget.jb4a.sdkexplorer.SDK_ExplorerSettingsActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * UTILS
 * <p/>
 * A class of utility methods.
 *
 * @author pvandyk
 *
 * Modified by Romina on 10/14/15.
 */

public class Utils {

    private static final String TAG = Utils.formatTag(Utils.class.getSimpleName());

    public static void prepareMenu(int currentPage, Menu menu) {
        menu.findItem(R.id.nav_send_message).setVisible(false);
        menu.findItem(R.id.nav_view_last_messages).setVisible(false);
        switch (currentPage) {
            case CONSTS.HOME_ACTIVITY:
                if (!CONSTS_API.getClientId().equals("")) {
                    // can't send messages if Custom keys and no client id provided
                    menu.findItem(R.id.nav_send_message).setVisible(true);
                }
                menu.findItem(R.id.nav_view_last_messages).setVisible(true);
                break;
            case CONSTS.SEND_MESSAGE_ACTIVITY:
                menu.findItem(R.id.nav_view_last_messages).setVisible(true);
                break;
            case CONSTS.LOCATION_ACTIVITY:
                break;
            case CONSTS.DISPLAY_MESSAGE_ACTIVITY:
                break;
            case CONSTS.VIEW_WEB_CONTENT_ACTIVITY:
                break;
            case CONSTS.CLOUDPAGE_ACTIVITY:
                break;
            case CONSTS.CLOUDPAGE_INBOX_ACTIVITY:
                break;
            case CONSTS.DISCOUNT_ACTIVITY:
                break;
            case CONSTS.INFO_ACTIVITY:
                break;
            case CONSTS.SETTINGS_ACTIVITY:
                break;
            case CONSTS.DEBUG_SETTINGS_ACTIVITY:
                break;
        }
    }

    public static Boolean selectMenuItem(Activity activity, int currentPage, MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                switch (currentPage) {
                    case CONSTS.HOME_ACTIVITY:
                        break;
                    case CONSTS.SEND_MESSAGE_ACTIVITY:
                    case CONSTS.LOCATION_ACTIVITY:
                    case CONSTS.DISPLAY_MESSAGE_ACTIVITY:
                    case CONSTS.VIEW_WEB_CONTENT_ACTIVITY:
                    case CONSTS.CLOUDPAGE_ACTIVITY:
                    case CONSTS.CLOUDPAGE_INBOX_ACTIVITY:
                    case CONSTS.DISCOUNT_ACTIVITY:
                    case CONSTS.INFO_ACTIVITY:
                    case CONSTS.SETTINGS_ACTIVITY:
                    case CONSTS.DEBUG_SETTINGS_ACTIVITY:
                    case CONSTS.EULA_ACTIVITY:
                        activity.onBackPressed();
                        break;
                }
                return true;

            case R.id.nav_send_message:
                try {
                    if (ETPush.getInstance().isPushEnabled()) {
                        intent = new Intent(activity, SendMessageActivity.class);
                        activity.startActivity(intent);
                    } else {
                        // can't send messages to this device if Push isn't enabled
                        Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.alert_utils5_text), Toast.LENGTH_LONG).show();
                    }
                } catch (ETException e) {
                    if (ETPush.getLogLevel() <= Log.ERROR) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
                return true;

            case R.id.nav_view_last_messages:
                intent = new Intent(activity, DisplayMessageActivity.class);
                activity.startActivity(intent);
                return true;
        }
        return false;
    }

    public static void setWebView(Activity activity, int res, StringBuilder sb, boolean wideView) {

        sb.insert(0, "<html><body " + (wideView ? "style=\"white-space: nowrap;\")" : "") + "><font size=\"4\">");
        sb.append("</font></body></html>");

        WebView webView = (WebView) activity.findViewById(res);
        webView.loadData(sb.toString(), "text/html", "UTF-8");
        if (wideView) {
            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }
        webView.setBackgroundColor(0x00000000);
        webView.setScrollbarFadingEnabled(false);

        if (android.os.Build.VERSION.SDK_INT >= 11) {
            webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        }
    }

    public static WebView createWebView(Activity activity, StringBuilder sb) {

        WebView webView = new WebView(activity);
        webView.loadData(sb.toString(), "text/html", "UTF-8");
        webView.setBackgroundColor(0x00000000);
        webView.setScrollbarFadingEnabled(false);

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        p.leftMargin = 15;
        p.rightMargin = 15;
        p.topMargin = 15;
        p.bottomMargin = 15;
        webView.setLayoutParams(p);

        // for transparency
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        }

        return webView;
    }

    public static void setActivityTitle(Activity activity, int titleRes) {
        updateAppNameWithVersion(activity, titleRes);
    }

    private static String updateAppNameWithVersion(Activity activity, int titleRes) {
        return updateAppNameWithVersion(activity, activity.getString(titleRes));
    }

    private static String updateAppNameWithVersion(Activity activity, String inputString) {
        return inputString.replace("SDK Explorer", activity.getString(R.string.app_name));
    }

    public static void setActivityTitle(Activity activity, String titleStr) {
        ActionBar ab = activity.getActionBar();
        ab.setTitle(updateAppNameWithVersion(activity, titleStr));
        //		ab.setBackgroundDrawable(new ColorDrawable(R.color.ET_blue));
        ab.setBackgroundDrawable(new ColorDrawable(activity.getResources().getColor(R.color.action_bar_color)));
        ab.setDisplayShowTitleEnabled(false);  // required to force redraw, without, gray color
        ab.setDisplayShowTitleEnabled(true);

        int actionBarTitleId = ApplicationClass.context().getResources().getSystem().getIdentifier("action_bar_title", "id", "android");
        if (actionBarTitleId > 0) {
            TextView title = (TextView) activity.findViewById(actionBarTitleId);
            if (title != null) {
                title.setTextColor(Color.WHITE);
            }
        }
    }

    public static String formatTag(String simpleName) {
        return String.format("%-25s", String.format("~#%1.21s", simpleName.replace("SDK_Explorer", "SDKX")));
    }
}
