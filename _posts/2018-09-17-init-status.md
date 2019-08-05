---
layout: page
title: "Initialization Errors"
subtitle: "Troubleshoot Initialization Errors"
category: trouble-shooting
date: 2018-09-17 12:00:00
order: 4
---
The [InitializationStatus]({{ site.baseurl}}/javadocs/{{ site.currentMajorMinor }}/reference/com/salesforce/marketingcloud/InitializationStatus.html) is provided via the [InitializationListener]({{ site.baseurl}}/javadocs/{{ site.currentMajorMinor }}/reference/com/salesforce/marketingcloud/MarketingCloudSdk.InitializationListener.html) passed into the call to the [MarketingCloudSdk]({{ site.baseurl}}/javadocs/{{ site.currentMajorMinor }}/reference/com/salesforce/marketingcloud/MarketingCloudSdk.html)'s [init]({{ site.baseurl}}/javadocs/{{ site.currentMajorMinor }}/reference/com/salesforce/marketingcloud/MarketingCloudSdk.html#init(android.content.Context,%20com.salesforce.marketingcloud.MarketingCloudConfig,%20com.salesforce.marketingcloud.MarketingCloudSdk.InitializationListener)) method.  There is a lot of information provided in this class that are useful for debugging issues with the SDK's initialization, but only a few things can be addressed and runtime.

Below We'll detail the methods from [InitializationStatus]({{ site.baseurl}}/javadocs/{{ site.currentMajorMinor }}/reference/com/salesforce/marketingcloud/InitializationStatus.html) that are useful to check at runtime and how you might be able to recover from them.

#### [isUsable()]({{ site.baseurl}}/javadocs/{{ site.currentMajorMinor }}/reference/com/salesforce/marketingcloud/InitializationStatus.html#isUsable())

If all you want to know is whether or not the SDK is un a "usable" state then you can use this method.  The term "usable" here simply means that the SDK's initialization did not fail.  However, there might be other issues you would want to address.

#### [status()]({{ site.baseurl}}/javadocs/{{ site.currentMajorMinor }}/reference/com/salesforce/marketingcloud/InitializationStatus.html#status())

This method will return the [Status]({{ site.baseurl}}/javadocs/{{ site.currentMajorMinor }}/reference/com/salesforce/marketingcloud/InitializationStatus.Status.html) of the initialization call.  

#### [locationsError()]({{ site.baseurl}}/javadocs/{{ site.currentMajorMinor }}/reference/com/salesforce/marketingcloud/InitializationStatus.html#locationsError())

If you have configured the SDK to enable either [geofence]({{ site.baseurl}}/javadocs/{{ site.currentMajorMinor }}/reference/com/salesforce/marketingcloud/MarketingCloudConfig.Builder.html#setGeofencingEnabled(boolean)) or [proximity]({{ site.baseurl}}/javadocs/{{ site.currentMajorMinor }}/reference/com/salesforce/marketingcloud/MarketingCloudConfig.Builder.html#setProximityEnabled(boolean)) messaging this method will indicate whether the SDK encountered an issue with the Google Play Service location library.  In additional to this method you can also use [playServicesMessage()]({{ site.baseurl}}/javadocs/{{ site.currentMajorMinor }}/reference/com/salesforce/marketingcloud/InitializationStatus.html#playServicesMessage()) and [playServicesStatus()]({{ site.baseurl}}/javadocs/{{ site.currentMajorMinor }}/reference/com/salesforce/marketingcloud/InitializationStatus.html#playServicesStatus()) to determine the exact action that you will need to take to resolve this issue; however, the most likely issue is that the device does not have a version of Google Play Services installed that is compatible and you should prompt the user to perform an update.

#### [messagingPermissionError()]( {{ site.baseurl}}/javadocs/{{ site.currentMajorMinor }}/reference/com/salesforce/marketingcloud/InitializationStatus.html#messagingPermissionError())

Enabling [geofence]({{ site.baseurl}}/javadocs/{{ site.currentMajorMinor }}/reference/com/salesforce/marketingcloud/MarketingCloudConfig.Builder.html#setGeofencingEnabled(boolean)) or [proximity]({{ site.baseurl}}/javadocs/{{ site.currentMajorMinor }}/reference/com/salesforce/marketingcloud/MarketingCloudConfig.Builder.html#setProximityEnabled(boolean))  messaging requires you to request the runtime permission for location.  Once you have do this, and called the corresponding messaging enablement method ([enableGeofenceMessaging]({{ site.baseurl}}/javadocs/{{ site.currentMajorMinor }}/reference/com/salesforce/marketingcloud/messages/RegionMessageManager.html#enableGeofenceMessaging()) / [enableProximityMessaging]({{ site.baseurl}}/javadocs/{{ site.currentMajorMinor }}/reference/com/salesforce/marketingcloud/messages/RegionMessageManager.html#enableProximityMessaging())) the SDK will keep track of this and verify that the permission is still granted during each subsequent initialization.  If, the user has revoked the location permission from your application then the SDK will disable the previously enabled messaging type and required you to re-request the permission from the user, and re-enable the messaging in the SDK.

### Example InitializationListener Implementation

<script src="https://gist.github.com/sfmc-mobilepushsdk/ad48b629a87c262c4d0d672940c26b1a.js"></script>
