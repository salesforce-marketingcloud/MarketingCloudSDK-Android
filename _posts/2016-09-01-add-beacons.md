---
layout: page
title: "Beacons"
subtitle: "Add Beacons and Beacon Debug Logging"
category: location
date: 2016-09-01 12:00:00
order: 3
---

### Add Beacons

#### 1. Add AltBeacon and Google Play Service Location Dependency to your build

<script src="https://gist.github.com/sfmc-mobilepushsdk/799a322831fc00ef306d02e781d3e1d3.js"></script>

> To be compatible with the current version of the SDK, replace {currentBeaconVersion} with {{ site.currentAltBeaconVersion}}, and replace {currentLocationVersion} with {{ site.currentGmsLocationVersion }}. These versions have been tested with the latest version of the SDK. Behavior is not guaranteed if you use a different version of either dependency.

#### 2. Add these permissions to your application's **AndroidManifest.xml**

<script src="https://gist.github.com/sfmc-mobilepushsdk/50d0b19cc5b6f6e10caea9dcf2cfa8ce.js"></script>

#### 3. Enable beacon messaging during SDK initialization

{% include gist.html sectionId="enable_beacon_at_init" names="8.x,7.x" gists="https://gist.github.com/sfmc-mobilepushsdk/b3025715fee8356a6f68fb04ebff9576.js,https://gist.github.com/sfmc-mobilepushsdk/0b844c9a6a8a97699dacdf4e3d213e10.js" %}

> To troubleshoot information related to beacons in the InitializationStatus that is returned during the SDK's initialization call, see [initialization status]({{ site.baseurl }}/trouble-shooting/init-status.html) documentation.

#### 4. Request permissions

* ##### Location permissions

   To enable proximity messaging, request the required location permissions from your users at runtime. For users on devices running Android Q, request both the `ACCESS_FINE_LOCATION` and `ACCESS_BACKGROUND_LOCATION` permissions. For users on devices older than Android Q, you can request only the `ACCESS_FINE_LOCATION` permission.

* ##### Bluetooth permission

  To enable proximity messaging, request the required location permissions from your users at runtime. For users on devices running Android S and above, request the `BLUETOOTH_SCAN` permission.

See [Request App Permissions](https://developer.android.com/training/permissions/requesting.html) for more information on requesting runtime permissions.  See [Android Q privacy change](https://developer.android.com/preview/privacy/device-location) documentation for more information on location permission change.


#### 5. Enable beacon messaging at runtime

Once all the permissions are granted you can enable beacon notification by calling `enableProximityMessaging()`.

{% include gist.html sectionId="enable_beacon_message" names="8.x,7.x" gists="https://gist.github.com/sfmc-mobilepushsdk/15ec0557f76e3a0b01305c5e987036bf.js,https://gist.github.com/sfmc-mobilepushsdk/4cece3020878bbf654bc596d901b0ca3.js" %}

You can disable beacon notifications by calling `disableProximityMessaging()`.

{% include gist.html sectionId="disable_beacon_message" names="8.x,7.x" gists="https://gist.github.com/sfmc-mobilepushsdk/e414f2f7cc441ea3c785ad06fadcbcbe.js,https://gist.github.com/sfmc-mobilepushsdk/12d88c94cae4a916887cd3a0022b79ce.js" %}

> MobilePush prevents the app from displaying a beacon message with an empty alert. If you include AMPscript in your message that returns no content or an empty string, the mobile app will not display that message.

> To understand how beacons behave in different situations, see the <a href="https://help.salesforce.com/articleView?id=mc_mp_beacon_behavior.htm&type=5" target="_blank">MobilePush beacons help documentation</a>.

### Add Debug Logging for Beacons

Proximity logging is off by default. To turn it on, use AltBeacon’s `LogManager` class. Our AltBeacon Logger implementation sends their logs through our internal logging interface. Use the following example code as a guide.

<script src="https://gist.github.com/sfmc-mobilepushsdk/ff3f0f800a81d9cd1717005dc9602968.js"></script>
