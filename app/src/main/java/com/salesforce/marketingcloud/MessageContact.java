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

package com.salesforce.marketingcloud;

import android.util.Log;

import com.salesforce.marketingcloud.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * MessageContact to provide data to Middle Tier to send a message to a list of devices or subscribers.
 *
 * @author awestberg
 */

@SuppressWarnings("unused")
public class MessageContact {

    private static final String TAG = Utils.formatTag(MessageContact.class.getSimpleName());

    private ArrayList<String> inclusionTags = null;
    private ArrayList<String> exclusionTags = null;
    private ArrayList<String> deviceTokens = null;
    private String messageText = null;
    private String sound = null;
    private String openDirect = null;
    private HashMap<String, String> customKeys = null;

    public ArrayList<String> getInclusionTags() {
        return inclusionTags;
    }

    public void setInclusionTags(ArrayList<String> inclusionTags) {
        this.inclusionTags = inclusionTags;
    }

    public ArrayList<String> getExclusionTags() {
        return exclusionTags;
    }

    public void setExclusionTags(ArrayList<String> exclusionTags) {
        this.exclusionTags = exclusionTags;
    }

    public ArrayList<String> getDeviceTokens() {
        return deviceTokens;
    }

    public void setDeviceTokens(ArrayList<String> deviceTokens) {
        this.deviceTokens = deviceTokens;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public String getOpenDirect() {
        return openDirect;
    }

    public void setOpenDirect(String openDirect) {
        this.openDirect = openDirect;
    }

    public HashMap<String, String> getCustomKeys() {
        return customKeys;
    }

    public void setCustomKeys(HashMap<String, String> customKeys) {
        this.customKeys = new HashMap<>(customKeys);
    }

    public String toJson() {
        /*
           {
             "DeviceTokens":["this device token"],
             "InclusionTags":["the list of tags that this message should go to"],
             "ExclusionTags":["the list of tags that this message should not go to"],
             "CustomKeys":{"key":"value", "key":"value"},
             "MessageText":"alert",
             "OpenDirect":"the URL to open when a message is clicked",
             "Override":true,
             "Sound":"default"
         }
         */
        try {
            JSONObject jsonObject = new JSONObject();

            if (messageText != null) {
                jsonObject.put("MessageText", messageText);
            }

            if (openDirect != null) {
                jsonObject.put("OpenDirect", openDirect);
            }

            jsonObject.put("Override", Boolean.TRUE);

            if (sound != null) {
                jsonObject.put("Sound", sound);
            } else {
                jsonObject.put("Sound", "default");
            }

            if (deviceTokens != null) {
                JSONArray jsonArray = new JSONArray(deviceTokens);
                jsonObject.put("deviceTokens", jsonArray);
            }

            if (inclusionTags != null) {
                JSONArray jsonArray = new JSONArray(inclusionTags);
                jsonObject.put("inclusionTags", jsonArray);
            }

            if (exclusionTags != null) {
                JSONArray jsonArray = new JSONArray(exclusionTags);
                jsonObject.put("inclusionTags", jsonArray);
            }

            if (customKeys != null) {
                JSONObject customKeysJson = new JSONObject();

                Iterator it = customKeys.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    customKeysJson.put((String) pair.getKey(), pair.getValue());
                    it.remove(); // avoids a ConcurrentModificationException
                }
                jsonObject.put("CustomKeys", customKeysJson);
            }
            return jsonObject.toString();
        } catch (JSONException e) {
            Log.e(TAG, String.format("Error converting AnalyticItem to JSON: %1$s", e.getMessage()), e);
            return null;
        }
    }
}
