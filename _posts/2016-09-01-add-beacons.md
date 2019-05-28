---
layout: page
title: "Beacons"
subtitle: "Add Beacons and Beacon Debug Logging"
category: location
date: 2016-09-01 12:00:00
order: 3
---

{% assign _names="Kotlin,Java" %}

### Add Beacons

#### 1. Add AltBeacon and Google Play Service Location Dependency to your build

<script src="https://gist.github.com/sfmc-mobilepushsdk/799a322831fc00ef306d02e781d3e1d3.js"></script><br/>

> To be compatible with the current version of the SDK, replace {currentBeaconVersion} with {{ site.currentAltBeaconVersion}}, and replace {currentLocationVersion} with {{ site.currentGmsLocationVersion }}. These versions have been tested with the latest version of the SDK. Behavior is not guaranteed if you use a different version of either dependency.

#### 2. Add these permissions to your application's **AndroidManifest.xml**

<script src="https://gist.github.com/sfmc-mobilepushsdk/d5a1d188e7288787bc27b164a3f8d423.js"></script>

#### 3. Enable beacon messaging during SDK initialization

{% include gist.html sectionId="init_enable_messaging" names=_names gists="https://gist.github.com/sfmc-mobilepushsdk/0b844c9a6a8a97699dacdf4e3d213e10.js,https://gist.github.com/sfmc-mobilepushsdk/4dae38330a98001e4596a8f1ee444c42.js" %}

> To troubleshoot information related to beacons in the InitializationStatus that is returned during the SDK's initialization call, see [initialization status]({{ site.baseurl }}/trouble-shooting/init-status.html) documentation.

#### 4. Enable beacon messaging at runtime

You *must* request the runtime location permission before calling the following code.  See [Request App Permissions](https://developer.android.com/training/permissions/requesting.html) for more information.

Once the permission is granted you can enable beacon notification by calling `enableProximityMessaging()`.

{% include gist.html sectionId="enable_messaging" names=_names gists="https://gist.github.com/sfmc-mobilepushsdk/4cece3020878bbf654bc596d901b0ca3.js,https://gist.github.com/sfmc-mobilepushsdk/c50d7f877140729a9d36d5d5b364c307.js" %}

You can disable beacon notifications by calling `disableProximityMessaging()`.

{% include gist.html sectionId="disable_messaging" names=_names gists="https://gist.github.com/sfmc-mobilepushsdk/12d88c94cae4a916887cd3a0022b79ce.js,https://gist.github.com/sfmc-mobilepushsdk/6d01ac52f8a0b21c8b10aa4a4104f9de.js" %}

> MobilePush prevents the app from displaying a beacon message with an empty alert. If you include AMPscript in your message that returns no content or an empty string, the mobile app will not display that message.

> To understand how beacons behave in different situations, see the <a href="https://help.salesforce.com/articleView?id=mc_mp_beacon_behavior.htm&type=5" target="_blank">MobilePush beacons help documentation</a>.

### Add Debug Logging for Beacons

Proximity logging is off by default. To turn it on, use AltBeacon’s `LogManager` class. Our AltBeacon Logger implementation sends their logs through our internal logging interface. Use the following example code as a guide.
<script src="https://gist.github.com/sfmc-mobilepushsdk/c73fca7651549f5c26acf081ce194481.js"></script>
