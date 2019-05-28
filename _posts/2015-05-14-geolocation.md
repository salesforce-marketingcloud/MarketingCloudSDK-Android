---
layout: page
title: "Geofences"
subtitle: "Add Geofences"
category: location
date: 2015-05-14 08:43:35
order: 2
---

{% assign _names="Kotlin,Java" %}

#### 1. Add Google Play Service Location dependency to your build

<script src="https://gist.github.com/sfmc-mobilepushsdk/0273a54fdf0e51395a986f56290df069.js"></script><br/>

> To be compatible with the current version of the SDK, replace {currentSupportedVersion} with {{ site.currentGmsLocationVersion }}. This version of Google Play Services has been tested with the latest version of the SDK. Behavior is not guaranteed if you use a different version of Google Play Services.

#### 2. Add these permissions to your application's *AndroidManifest.xml*

<script src="https://gist.github.com/sfmc-mobilepushsdk/ebad1ff6495407404254cde0778241c5.js"></script>

#### 3. Enable geofence messaging during SDK initialization

{% include gist.html sectionId="init_enable_messaging" names=_names gists="https://gist.github.com/sfmc-mobilepushsdk/6bd87b1fbf9a049c46762adfaae92418.js,https://gist.github.com/sfmc-mobilepushsdk/1b3857b510278eab34e3f950b251d6b4.js" %}

> To troubleshoot information related to geofence messaging in the InitializationStatus that is returned during the SDK's initialization call, see [initialization status]({{ site.baseurl }}/trouble-shooting/init-status.html) documentation.

#### 4. Enable geofence messaging at runtime

> You **must** request the runtime location permission before calling the following code.  See [Request App Permissions](https://developer.android.com/training/permissions/requesting.html) for more information.

{% include gist.html sectionId="enable_messaging" names=_names gists="https://gist.github.com/sfmc-mobilepushsdk/70262e9c24d4f43e925cf1a01c337a03.js,https://gist.github.com/sfmc-mobilepushsdk/9cdb89bd42990a5b3d793a0e2e82e3c0.js" %}

You can disable geofence notifications by calling `disableGeofenceMessaging()`.

{% include gist.html sectionId="disable_messaging" names=_names gists="https://gist.github.com/sfmc-mobilepushsdk/f9e971a34513b27c8797f7857263efb3.js,https://gist.github.com/sfmc-mobilepushsdk/28e4f4745454be5fdbaf7d6c54c7c917.js" %}

> MobilePush prevents your app from displaying a geofence message with an empty alert. If you include AMPscript in your message that returns no content or an empty string, the mobile app doesn’t display that message.
