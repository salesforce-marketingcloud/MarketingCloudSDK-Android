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

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.exacttarget.etpushsdk.util.EventBus;
import com.salesforce.marketingcloud.scrollpages.CirclePageIndicator;
import com.salesforce.marketingcloud.scrollpages.PageIndicator;
import com.salesforce.marketingcloud.scrollpages.ScrollPagesAdapter;
import com.salesforce.marketingcloud.utils.Utils;

/**
 * SendMessageActivity will provide an overview of each type of message that can be sent using the SDK_ExplorerSendMessagesDialog.
 *
 * @author pvandyk
 */

public class SendMessageActivity extends BaseActivity {

    private static final String TAG = Utils.formatTag(SendMessageActivity.class.getSimpleName()) ;
    ScrollPagesAdapter mAdapter;
    ViewPager mPager;
    PageIndicator mIndicator;
    String[] pages = new String[]{"0", "1", "2", "3", "4", "5"};
    private int currentPage = CONSTS.SEND_MESSAGE_ACTIVITY;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scroll_pages);
//
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        getSupportActionBar().setElevation(0);
//        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CONSTS.KEY_CURRENT_PAGE, currentPage);
    }

    @Override
    protected void onDestroy() {
        EventBus.getInstance().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

//        Utils.setActivityTitle(this, R.string.send_message_activity_title);
//        getActionBar().setDisplayHomeAsUpEnabled(true);

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

    private void prepareDisplay() {

        StringBuilder sb = new StringBuilder();
        sb.append(CONSTS.PAGE_TITLE);
        sb.append("<b>Sending a Message - Basic</b><br/>");
        sb.append("<ul>");
        sb.append("<li>Open Preferences (see menu) to add your name and then enable Push Notifications.</li><br/>");
        sb.append("<li>Wait 15 minutes to ensure your settings have been registered.</li><br/>");
        sb.append("<li>Click Send Message to send a message to this device.</li><br/>");
        sb.append("</ul>");
        pages[0] = sb.toString();

        sb = new StringBuilder();
        sb.append(CONSTS.PAGE_TITLE);
        sb.append("<b>Sending a Message - Tag Selected in Preferences</b><br/>");
        sb.append("Tags allow you to target customers who have specified they want certain types of notifications but not others.  For example, an interest in one type of activity, but not another.<br/>");
        sb.append("<br/>");
        sb.append("You can send a message by selecting the particular tag (or group) who should receive the message.<br/>");
        sb.append("<ul>");
        sb.append("<li>Open Preferences (see menu) to select tags for the activities you are interested in.</li><br/>");
        sb.append("<li>Wait 15 minutes to ensure your settings have been registered.</li><br/>");
        sb.append("<li>Click Send Message to send a message to this device and choose one of the tags you selected in Preferences.</li><br/>");
        sb.append("<li>Within a minute you should receive the message.</li><br/>");
        sb.append("</ul>");
        pages[1] = sb.toString();

        sb = new StringBuilder();
        sb.append(CONSTS.PAGE_TITLE);
        sb.append("<b>Sending a Message - Tag Not Selected in Preferences</b><br/>");
        sb.append("Tags allow you to target customers who have specified they want certain types of notifications but not others.  For example, an interest in one activity, but not another.<br/>");
        sb.append("<br/>");
        sb.append("If you target a particular group, but the customer has not expressed interest in that group, they will not receive a message.<br/>");
        sb.append("<br/>");
        sb.append("You can test that here if you select certain Activities in Preferences, but then send to an Activity you have not selected.  Then you will not receive a message.<br/>");
        sb.append("<ul>");
        sb.append("<li>Open Preferences (see menu) to select tags for your favorite activities.</li><br/>");
        sb.append("<li>Wait 15 minutes to ensure your settings have been registered.</li><br/>");
        sb.append("<li>Click Send Message to send a message to this device and choose one of the tags you have NOT selected in Preferences.</li><br/>");
        sb.append("<li>You will not receive a message since the message is intended only for those who have selected that tag.</li><br/>");
        sb.append("</ul>");
        pages[2] = sb.toString();

        sb = new StringBuilder();
        sb.append(CONSTS.PAGE_TITLE);
        sb.append("<b>Sending a Message - Specify Open Direct URL</b><br/>");
        sb.append("An Open Direct URL allows you to specify a particular web page to view when a customer clicks on the notification received.<br/>");
        sb.append("<ul>");
        sb.append("<li>Open Preferences (see menu) to enable Push Notifications.</li><br/>");
        sb.append("<li>Wait 15 minutes to ensure your settings have been registered.</li><br/>");
        sb.append("<li>Click Send Message to send a message to this device and then enter an URL in the Open Direct field.</li><br/>");
        sb.append("<li>Within a minute, you should receive a message.</li><br/>");
        sb.append("<li>When you click on this message, the web page you entered when you sent the message will be opened.</li><br/>");
        sb.append("</ul>");
        pages[3] = sb.toString();

        sb = new StringBuilder();
        sb.append(CONSTS.PAGE_TITLE);
        sb.append("<b>Sending a Message - Custom Keys</b><br/>");
        sb.append("Custom Keys allow you to direct the app to perform certain functions when the customer clicks on the notification.<br/>");
        sb.append("<br/>");
        sb.append("We have setup a discount code as the Custom Key for the JB4A SDK Explorer.<br/>");
        sb.append("<ul>");
        sb.append("<li>Open Preferences (see menu) to enable Push Notifications.</li><br/>");
        sb.append("<li>Wait 15 minutes to ensure your settings have been registered.</li><br/>");
        sb.append("<li>Click Send Message to send a message to this device and select one of the Custom Keys specified in the drop down list.</li><br/>");
        sb.append("<li>Within a minute, you should receive a message.</li><br/>");
        sb.append("<li>When you click on this message, special processing within the app based on the value of that custom key will be performed.</li><br/>");
        sb.append("</ul>");
        pages[4] = sb.toString();

        sb = new StringBuilder();
        sb.append(CONSTS.PAGE_TITLE);
        sb.append("<b>Sending a Message - Location</b><br/>");
        sb.append("<ul>");
        sb.append("<li>Open Preferences (see menu) to enable Location Settings.</li><br/>");
        sb.append("<li>Wait 15 minutes to ensure your settings are registered.</li><br/>");
        sb.append("<li>Download a tool such as FakeGPS to mock your location.</li><br/>");
        sb.append("<li>Open FakeGPS and go to any of the following locations:</li><br/>");
        sb.append("<ul>");
        sb.append("<li>Grand Canyon National Park</li>");
        sb.append("<li>Yellowstone National Park</li>");
        sb.append("<li>Yosemite National Park</li>");
        sb.append("</ul><br/>");
        sb.append("<li>If FakeGPS works properly, then within a minute you should receive a message welcoming you to that park.</li><br/>");
        sb.append("</ul>");
        pages[5] = sb.toString();

        mAdapter = new ScrollPagesAdapter(getSupportFragmentManager(), pages, true);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
    }
}
