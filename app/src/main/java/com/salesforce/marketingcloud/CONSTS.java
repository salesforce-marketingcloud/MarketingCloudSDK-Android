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

/**
 * CONSTS
 * <p/>
 * Global constants.
 *
 * @author pvandyk
 */
public class CONSTS {

    public static final String KEY_CURRENT_PAGE = "currentPage";

    public static final int HOME_ACTIVITY = 0;
    public static final int SETTINGS_ACTIVITY = 1;
    public static final int DEBUG_SETTINGS_ACTIVITY = 2;
    public static final int LOCATION_ACTIVITY = 3;
    public static final int SEND_MESSAGE_ACTIVITY = 4;
    public static final int DISPLAY_MESSAGE_ACTIVITY = 5;
    public static final int CLOUDPAGE_ACTIVITY = 6;
    public static final int CLOUDPAGE_INBOX_ACTIVITY = 7;
    public static final int VIEW_WEB_CONTENT_ACTIVITY = 8;
    public static final int DISCOUNT_ACTIVITY = 9;
    public static final int INFO_ACTIVITY = 10;
    public static final int EULA_ACTIVITY = 11;

    public static final String KEY_ATTRIB_FIRST_NAME = "FirstName";
    public static final String KEY_ATTRIB_LAST_NAME = "LastName";

    public static final String KEY_PREF_FIRST_NAME = "pref_first_name";
    public static final String KEY_PREF_LAST_NAME = "pref_last_name";
    public static final String KEY_PREF_SUBSCRIBER_KEY = "pref_subscriber_key";
    public static final String KEY_PREF_PUSH = "pref_push";
    public static final String KEY_PREF_LOCATION = "pref_location";
    public static final String KEY_PREF_CAT_SPORTS = "pref_cat_activities";
    public static final String[] KEY_PREF_PUSH_DEPENDENT = {KEY_PREF_LOCATION, KEY_PREF_CAT_SPORTS};
    public static final String KEY_DEBUG_PREF_ENABLE_DEBUG = "debug_pref_enable_debug";
    public static final String KEY_DEBUG_PREF_COLLECT_LOGCAT = "debug_pref_collect_logcat";
    public static final String KEY_PUSH_RECEIVED_DATE = "push_received_date";
    public static final String KEY_PUSH_RECEIVED_PAYLOAD = "push_received_payload";
    public static final String KEY_PAYLOAD_DISCOUNT = "discount_code";
    public static final String KEY_PAYLOAD_ALERT = "alert";
    public static final String PAGE_TITLE = "<b>Journey Builder For Apps (JB4A)<br/>SDK Explorer</b><br/><br/>";
}
