---
layout: page
title: "Initialization Errors"
subtitle: "Troubleshoot Initialization Errors"
category: trouble-shooting
date: 2018-09-17 12:00:00
order: 4
---
## 8.x
The `InitializationStatus` is provided via the InitializationListener passed into the call to the SFMCSdk's configure method.

## 7.x
The [InitializationStatus]({{ site.baseurl}}/javadocs/{{ site.currentMajorMinor }}/com.salesforce.marketingcloud/-initialization-status/index.html) is provided via the [InitializationListener]({{ site.baseurl}}/javadocs/{{ site.currentMajorMinor }}/com.salesforce.marketingcloud/-marketing-cloud-sdk/-initialization-listener/index.html) passed into the call to the [MarketingCloudSdk]({{ site.baseurl}}/javadocs/{{ site.currentMajorMinor }}/com.salesforce.marketingcloud/-marketing-cloud-sdk/index.html)'s [init]({{ site.baseurl}}/javadocs/{{ site.currentMajorMinor }}/com.salesforce.marketingcloud/-marketing-cloud-sdk/init.html) method.  There is a lot of information provided in this class that are useful for debugging issues with the SDK's initialization, but only a few things can be addressed and runtime.

Below We'll detail the methods from [InitializationStatus]({{ site.baseurl}}/javadocs/{{ site.currentMajorMinor }}/com.salesforce.marketingcloud/-initialization-status/index.html) that are useful to check at runtime and how you might be able to recover from them.
The `InitializationStatus` is provided via the InitializationListener passed into the call to the SFMCSdk's configure method.

#### [isUsable()]({{ site.baseurl}}/javadocs/{{ site.currentMajorMinor }}/com.salesforce.marketingcloud/-initialization-status/is-usable.html)

If all you want to know is whether or not the SDK is in a "usable" state then you can use this method.  The term "usable" here simply means that the SDK's initialization did not fail.  However, there might be other issues you would want to address.
Below we'll detail the methods from InitializationStatus that are useful to check at runtime.

#### [status()]({{ site.baseurl}}/javadocs/{{ site.currentMajorMinor }}/com.salesforce.marketingcloud/-initialization-status/status.html)

This method will return the [Status]({{ site.baseurl}}/javadocs/{{ site.currentMajorMinor }}/com.salesforce.marketingcloud/-initialization-status/-status/index.html) of the initialization call.

#### [locationsError()]({{ site.baseurl}}/javadocs/{{ site.currentMajorMinor }}/com.salesforce.marketingcloud/-initialization-status/locations-error.html)

If you have configured the SDK to enable either [geofence]({{ site.baseurl}}/javadocs/{{ site.currentMajorMinor }}/com.salesforce.marketingcloud.messages/-region-message-manager/enable-geofence-messaging.html) or [proximity]({{ site.baseurl}}/javadocs/{{ site.currentMajorMinor }}/com.salesforce.marketingcloud.messages/-region-message-manager/enable-proximity-messaging.html) messaging this method will indicate whether the SDK encountered an issue with the Google Play Service location library.  In additional to this method you can also use [playServicesMessage()]({{ site.baseurl}}/javadocs/{{ site.currentMajorMinor }}/com.salesforce.marketingcloud/-initialization-status/play-services-message.html) and [playServicesStatus()]({{ site.baseurl}}/javadocs/{{ site.currentMajorMinor }}/com.salesforce.marketingcloud/-initialization-status/play-services-status.html) to determine the exact action that you will need to take to resolve this issue; however, the most likely issue is that the device does not have a version of Google Play Services installed that is compatible and you should prompt the user to perform an update.

#### [messagingPermissionError()]( {{ site.baseurl}}/javadocs/{{ site.currentMajorMinor }}/com.salesforce.marketingcloud/-initialization-status/messaging-permission-error.html)

Enabling [geofence]({{ site.baseurl}}/javadocs/{{ site.currentMajorMinor }}/com.salesforce.marketingcloud/-marketing-cloud-config/-builder/set-geofencing-enabled.html) or [proximity]({{ site.baseurl}}/javadocs/{{ site.currentMajorMinor }}/com.salesforce.marketingcloud/-marketing-cloud-config/-builder/set-proximity-enabled.html)  messaging requires you to request the runtime permission for location.  Once you have do this, and called the corresponding messaging enablement method ([enableGeofenceMessaging]({{ site.baseurl}}/javadocs/{{ site.currentMajorMinor }}/com.salesforce.marketingcloud.messages/-region-message-manager/enable-geofence-messaging.html) / [enableProximityMessaging]({{ site.baseurl}}/javadocs/{{ site.currentMajorMinor }}/com.salesforce.marketingcloud.messages/-region-message-manager/enable-proximity-messaging.html) the SDK will keep track of this and verify that the permission is still granted during each subsequent initialization.  If, the user has revoked the location permission from your application then the SDK will disable the previously enabled messaging type and required you to re-request the permission from the user, and re-enable the messaging in the SDK.

### Example InitializationListener Implementation

### Example InitializationListener Implementation

{% include gist.html sectionId="init_status" names="8.x,7.x" gists="https://gist.github.com/sfmc-mobilepushsdk/da60d39e7a5193a4a173d53ff21ff2be.js,https://gist.github.com/sfmc-mobilepushsdk/ad48b629a87c262c4d0d672940c26b1a.js" %}
