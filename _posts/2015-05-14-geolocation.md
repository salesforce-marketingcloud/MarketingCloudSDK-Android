---
layout: page
title: "Geofences"
subtitle: "Add Geofences"
category: location
date: 2015-05-14 08:43:35
order: 2
---

#### 1. Add Google Play Service Location dependency to your build

<script src="https://gist.github.com/sfmc-mobilepushsdk/0273a54fdf0e51395a986f56290df069.js"></script><br/>

> To be compatible with the current version of the SDK, replace {currentSupportedVersion} with {{ site.currentGmsLocationVersion }}. This version of Google Play Services has been tested with the latest version of the SDK. Behavior is not guaranteed if you use a different version of Google Play Services.

#### 2. Add these permissions to your application's *AndroidManifest.xml*

<script src="https://gist.github.com/sfmc-mobilepushsdk/f7f7828859ec952a37a6b3c5b88c2eed.js"></script>

#### 3. Enable geofence messaging during SDK initialization

<script src="https://gist.github.com/sfmc-mobilepushsdk/6bd87b1fbf9a049c46762adfaae92418.js"></script>

> To troubleshoot information related to geofence messaging in the InitializationStatus that is returned during the SDK's initialization call, see [initialization status]({{ site.baseurl }}/trouble-shooting/init-status.html) documentation.

#### 4. Request location permission

To enable geofence messaging, request the required location permissions from your users at runtime. For users on devices running Android Q, request both the `ACCESS_FINE_LOCATION` and `ACCESS_BACKGROUND_LOCATION` permissions. For users on devices older than Android Q, you can request only the `ACCESS_FINE_LOCATION` permission.

See [Request App Permissions](https://developer.android.com/training/permissions/requesting.html) for more information on requesting runtime permissions.  See [Android Q privacy change](https://developer.android.com/preview/privacy/device-location) documentation for more information on this change.

#### 5. Enable geofence messaging at runtime

Once the permission is granted you can enable beacon notification by calling `enableGeofenceMessaging()`.

<script src="https://gist.github.com/sfmc-mobilepushsdk/70262e9c24d4f43e925cf1a01c337a03.js"></script>

You can disable geofence notifications by calling `disableGeofenceMessaging()`.

<script src="https://gist.github.com/sfmc-mobilepushsdk/f9e971a34513b27c8797f7857263efb3.js"></script>

> MobilePush prevents your app from displaying a geofence message with an empty alert. If you include AMPscript in your message that returns no content or an empty string, the mobile app doesn’t display that message.
