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

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.exacttarget.etpushsdk.ETPush;
import com.salesforce.marketingcloud.utils.Utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * SDK_ExplorerSendMessagesDialog is dialog that will send messages and test the receipt of those
 * messages for the device it is running on.
 * <p/>
 * This dialog extends Dialog to kick off messages quickly without using the Marketing Cloud.
 * <p/>
 * This dialog would not normally be part of your delivered application.  However, it has been
 * created to test the functionality of the app, check the setup of your app within the Marketing
 * Cloud and Google Cloud Messaging, and provide a quick way to determine whether the setup is
 * correct.
 *
 * @author pvandyk
 */

public class SendMessagesDialog extends Dialog {

    private static final Handler uiThread = new Handler(Looper.getMainLooper());
    private final UpdateUIRunnable updateUiRunnable = new UpdateUIRunnable();
    private static final String TAG = Utils.formatTag(SendMessagesDialog.class.getSimpleName());
    private static Activity callingActivity;

    public SendMessagesDialog(Activity inActivity) {
        super(inActivity);

        // set variables
        callingActivity = inActivity;
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_messages_dialog);
//        setTitle("Send Message");

        prepareDisplay();
    }

    @Override
    public void onBackPressed() {
        dismiss();
        super.onBackPressed();
    }

    private void prepareDisplay() {

        // You can choose to select a Standard message or a CloudPage Message
        // OpenDirect can not be sent with a CloudPage message, so remove from the list of choices
        //
        final RadioGroup messageTypeRG = (RadioGroup) findViewById(R.id.chooseMessageTypeRG);
        final EditText openDirectET = (EditText) findViewById(R.id.openDirectET);
        messageTypeRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                TextView openDirectDesc = (TextView) findViewById(R.id.openDirectDescriptor);
                if (checkedId == R.id.Standard) {
                    openDirectDesc.setVisibility(View.VISIBLE);
                    openDirectET.setVisibility(View.VISIBLE);
                } else {
                    openDirectDesc.setVisibility(View.GONE);
                    openDirectET.setVisibility(View.GONE);
                }
            }
        });

        TextView demoDisclaimer = (TextView) findViewById(R.id.demoDisclaimerTV);
        demoDisclaimer.setText("Sending messages from an app that also displays Salesforce Marketing Cloud messages is not normally done.  We " +
                "have added this capability in this explorer app for demonstration purposes only, allowing you to " +
                "fully test the functionality of the Salesforce Marketing Cloud within this app.\n\nMessages will only be sent to this device.\n\nPlease enter " +
                "the values that would normally be set for a specific message by the marketing team in the Salesforce Marketing Cloud.");

        // get custom key (discount_code)
        List<String> discountCodesDescriptive = new ArrayList<>();
        final List<String> discountCodeKeys = new ArrayList<>();
        discountCodesDescriptive.add("No code");
        discountCodeKeys.add("none");
        discountCodesDescriptive.add("10%");
        discountCodeKeys.add("10");
        discountCodesDescriptive.add("15%");
        discountCodeKeys.add("15");
        discountCodesDescriptive.add("20%");
        discountCodeKeys.add("20");

        ArrayAdapter<String> dcAdapter = new ArrayAdapter<>(callingActivity, android.R.layout.simple_spinner_item, discountCodesDescriptive);
        dcAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final Spinner dcSpinner = (Spinner) findViewById(R.id.discountSpinner);
        dcSpinner.setSelection(0);
        dcSpinner.setAdapter(dcAdapter);

        // prep sport spinner
        String[] activityNames = callingActivity.getResources().getStringArray(R.array.activity_names);
        String[] activityKeys = callingActivity.getResources().getStringArray(R.array.activity_keys);

        List<String> allTeamNames = new ArrayList<>();
        final List<String> allTeamKeys = new ArrayList<>();
        allTeamNames.add("None");
        allTeamKeys.add("none");

        for (int i = 0; i < activityNames.length; i++) {
            allTeamNames.add(activityNames[i]);
            allTeamKeys.add(activityKeys[i]);
        }

        ArrayAdapter<String> activityAdapter = new ArrayAdapter<>(callingActivity, android.R.layout.simple_spinner_item, allTeamNames);
        activityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final Spinner tagSpinner = (Spinner) findViewById(R.id.tagSpinner);
        tagSpinner.setSelection(0);
        tagSpinner.setAdapter(activityAdapter);

        Button btSend = (Button) findViewById(R.id.sendButton);
        btSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                int messageTypeSelected = messageTypeRG.getCheckedRadioButtonId();

                MessageType messageType;
                if (messageTypeSelected == R.id.Standard) {
                    messageType = MessageType.STANDARD;
                } else {
                    messageType = MessageType.CLOUDPAGE;
                }

                EditText messageET = (EditText) findViewById(R.id.messageET);
                String outMsg = messageET.getText().toString();

                RadioGroup soundRG = (RadioGroup) findViewById(R.id.soundRG);
                int selSoundId = soundRG.getCheckedRadioButtonId();
                RadioButton soundRB = (RadioButton) findViewById(selSoundId);
                String outSound = (String) soundRB.getTag();

                String outOD = openDirectET.getText().toString();

                int dcSelectedIndex = dcSpinner.getSelectedItemPosition();
                String outKey = discountCodeKeys.get(dcSelectedIndex);

                int tagSelectedIndex = tagSpinner.getSelectedItemPosition();
                String outTag = allTeamKeys.get(tagSelectedIndex);

                if (ETPush.getLogLevel() <= Log.DEBUG) {
                    Log.i(TAG, "Sending message to: " + outKey + " with code : " + outTag + " : " + outMsg);
                }

                sendMessage(outMsg, outSound, outTag, outOD, outKey, messageType);

                dismiss();
            }

        });

        Button btCancel = (Button) findViewById(R.id.cancelButton);
        btCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }

        });
    }

    // sendMessage()
    //
    //		This method is not normally found within a client app.  This code is typically found
    //      within a server app to control sending of messages.
    //
    private void sendMessage(final String outMsg, final String outSound, final String outTag, final String outOD, final String outKey, final MessageType messageType) {

        new Thread(new Runnable() {
            public void run() {
                try {
                    //
                    //	get an api access token
                    //  the clientId and the clientSecret are found in the Marketing Cloud setup for the Server-Server application.
                    //
                    //  the CONSTS_API will return the appropriate development or production version depending on the setting from
                    //  CONSTS_API.setDevelopment() that is called ApplicationClass.
                    //

                    URL url = new URL(CONSTS_API.getFuel_url());
                    URLConnection uc = url.openConnection();
                    HttpsURLConnection conn = (HttpsURLConnection) uc;
                    conn.setUseCaches(false);
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-type", "application/json");

                    //Send request
                    String jsonBody = "{\"clientId\":\"" + CONSTS_API.getClientId() + "\",\"clientSecret\":\"" + CONSTS_API.getClientSecret() + "\"}";

                    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                    wr.writeBytes(jsonBody);
                    wr.flush();
                    wr.close();

                    String responseStr;
                    String accessToken;
                    if (conn.getResponseCode() == 200 || conn.getResponseCode() == 201 || conn.getResponseCode() == 202) {
                        //Get Response
                        InputStream is = conn.getInputStream();
                        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                        String line;
                        StringBuilder response = new StringBuilder();
                        while ((line = rd.readLine()) != null) {
                            response.append(line);
                            response.append('\r');
                        }
                        rd.close();
                        responseStr = response.toString();

                        JSONObject jsonObject = new JSONObject(responseStr);
                        accessToken = jsonObject.getString("accessToken");
                    } else {
                        responseStr = String.format("Unable to retrieve accessToken for Fuel request.  BAD RESPONSE CODE: %1$d", conn.getResponseCode());

                        if (Looper.myLooper() == Looper.getMainLooper()) {
                            postToast(responseStr);
                        } else {
                            updateUiRunnable.setMessage(responseStr);
                            uiThread.post(updateUiRunnable);
                        }
                        return;
                    }

                    //
                    // once the Access Token is retrieved, it can be used in to send the actual message.
                    //
                    // The messageId is the Id of the API Message set in the Marketing Cloud.  This API Message creates a template in the
                    // Marketing Cloud (including security that connects to this app), and allows you to override the fields required for
                    // the message being sent.
                    //
                    // Two API Messages have been created in the Cloud.  One for standard messages.  One that includes a CloudPage.
                    //

                    String messageId;
                    if (messageType == MessageType.STANDARD) {
                        messageId = CONSTS_API.getStandardMessageId();
                    } else {
                        messageId = CONSTS_API.getCloudPageMessageId();
                    }

                    //
                    // MessageContact is a JSON class that will provide the details to override in the Message.
                    //
                    // Details can be found here:
                    // https://code.exacttarget.com/api/messagecontact-send-0
                    //
                    MessageContact messageData = new MessageContact();

                    // set the text of the message
                    messageData.setMessageText(outMsg);

                    // set the sound for the message
                    if (!outSound.equals("none")) {
                        // the sound key is either default to use the default sound that the customer has set for
                        // notifications.  Or it will be "custom.caf" which will then sound the sound found in the
                        // raw folder of this project called custom.mp3.
                        messageData.setSound(outSound);
                    }

                    // set to current device token
                    // you must set a list of system tokens or subscriber keys
                    // using this message type will ensure that you don't accidentally send to all your devices!
                    ArrayList<String> deviceTokens = new ArrayList<>(1);
                    deviceTokens.add(ETPush.getInstance().getSystemToken());
                    messageData.setDeviceTokens(deviceTokens);

                    // set the Open Direct URL.  OpenDirect takes the mobile app user to a specific location within the app after
                    // that user interacts with a push message. The marketing user will specify a full URL address with subdomains (if any),
                    // including http:// or https:// as applicable in the Create Message page of the Salesforce Marketing Cloud.
                    if (!outOD.isEmpty()) {
                        messageData.setOpenDirect(outOD);
                    }

                    // Here a key is set to dictate special processing within the app once the message is received.
                    // in this JB4A SDK Explorer, this key will determine whether to display the 10, 15, or 20% discount QR code
                    HashMap<String, String> customKeys = new HashMap<>();
                    if (!outKey.equals("none")) {
                        customKeys.put(CONSTS.KEY_PAYLOAD_DISCOUNT, outKey);
                        messageData.setCustomKeys(customKeys);
                    }

                    // the following are the tags that will determine whether the user as subscribed to a particular service
                    // in this JB4A SDK Explorer, these are the Activities subscribed to in Settings.
                    ArrayList<String> inclusionTags = new ArrayList<>(1);
                    if (!outTag.equals("none")) {
                        inclusionTags.add(outTag);
                        messageData.setInclusionTags(inclusionTags);
                    }

                    //
                    // Send to MarketingCloud to request message to be sent
                    //
                    url = new URL(String.format("%1$s%2$s/send", "https://www.exacttargetapis.com/push/v1/messageContact/", messageId));
                    uc = url.openConnection();
                    conn = (HttpsURLConnection) uc;
                    conn.setUseCaches(false);
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-type", "application/json");
                    conn.setRequestProperty("Authorization", String.format("Bearer %1$s", accessToken));

                    //Send request
                    jsonBody = messageData.toJson();

                    if (ETPush.getLogLevel() <= Log.DEBUG) {
                        Log.i(TAG, String.format("Sending: %s", jsonBody));
                    }

                    wr = new DataOutputStream(conn.getOutputStream());
                    wr.writeBytes(jsonBody);
                    wr.flush();
                    wr.close();

                    if (conn.getResponseCode() == 200 || conn.getResponseCode() == 201 || conn.getResponseCode() == 202) {
                        //Get Response
                        InputStream is = conn.getInputStream();
                        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                        String line;
                        StringBuilder response = new StringBuilder();
                        while ((line = rd.readLine()) != null) {
                            response.append(line);
                            response.append('\r');
                        }
                        rd.close();

                    } else {
                        responseStr = String.format("Unable to send message.  BAD RESPONSE CODE: %1$d", conn.getResponseCode());

                        if (Looper.myLooper() == Looper.getMainLooper()) {
                            postToast(responseStr);
                        } else {
                            updateUiRunnable.setMessage(responseStr);
                            uiThread.post(updateUiRunnable);
                        }
                        return;
                    }

                    if (Looper.myLooper() == Looper.getMainLooper()) {
                        postToast(callingActivity.getString(R.string.alert_send_message1_text));
                    } else {
                        updateUiRunnable.setMessage(callingActivity.getString(R.string.alert_send_message1_text));
                        uiThread.post(updateUiRunnable);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        }).start();

    }

    private enum MessageType {
        STANDARD,
        CLOUDPAGE
    }

    private void postToast(String toastMessage) {
        if (toastMessage != null) {
            Toast.makeText(ApplicationClass.context(), toastMessage, Toast.LENGTH_LONG).show();
        }
    }

    private class UpdateUIRunnable implements Runnable {
        String message;

        @Override
        public void run() {
            postToast(message);
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}

