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
 * CONSTS_API
 *
 * This class holds the constants for the API keys used within the app. You must fill in the appropriate values for your app.
 *
 */

public class CONSTS_API {

    // getEtAppId()
    //
    // get the ET App Id from the App Center in the Marketing Cloud for your Mobile Push App
    //
    public static String getEtAppId() {
        return "acd38e4b-4739-4a65-b5cc-e708c0e5f999";
    }

    // getAccessToken()
    //
    // get the Access Token from the App Center in the Marketing Cloud for your Mobile Push App
    //
    public static String getAccessToken() {
        return "8prxemk3q6knbkzs8hd8wmj9";
    }

    // getGcmSenderId()
    //
    // get the GCM Sender Id setup for your Google Cloud Messaging account.
    //
    public static String getGcmSenderId() { return "40182511138"; }

    // getClientId()
    //
    // get the Client ID from the App Center in the Marketing Cloud for your Server to Server App in App Center in the Marketing Cloud
    //
    public static String getClientId() {
        return "[***REPLACE WITH YOUR CLIENT SECRET***]";
    }

    // getClientSecret()
    //
    // get the Client Secret from the App Center in the Marketing Cloud for your Server to Server App in App Center in the Marketing Cloud
    //
    public static String getClientSecret() {
        return "[***REPLACE WITH YOUR CLIENT SECRET***]";
    }

    // getStandardMessageId()
    //
    // get the Message ID from the API Message you set up in the Marketing Cloud for standard alerts
    //
    public static String getStandardMessageId() {
        return "[***REPLACE WITH YOUR MESSAGE ID***]";
    }

    // getMessageId()
    //
    // get the Message ID from the API Message you set up in the Marketing Cloud for a CloudPage alert
    //
    public static String getCloudPageMessageId() {
        return "[***REPLACE WITH YOUR CLOUDPAGE MESSAGE ID***]";
    }

    public static String getFuel_url() {
        return "https://auth.exacttargetapis.com/v1/requestToken";
    }
}

