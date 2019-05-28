---
layout: page
title: "Implement the SDK in Android"
subtitle: "Integrate the SDK"
category: sdk-implementation
date: 2015-05-14 12:00:00
order: 1
---

To integrate the Marketing Cloud Mobile Push Android SDK with your Android mobile app, register a device with Marketing Cloud. To register a device, add the SDK and its dependencies to your app by following these instructions.

### Prerequisites

Create an app in MobilePush. This process connects the device to the MobilePush app you created previously in [MobilePush]({{ site.baseurl }}/create-apps/create-apps-overview.html).

> The Android SDK requires Android API {{ site.minAndroidSdkVersion }} or greater and has dependencies on the Android Support v4 and Google Play Services libraries.

## 1.  Implement the SDK

### Update module-level `build.gradle` file

Add the SDK repository:
{% include gist.html sectionId="gradle_repo" names="Gradle" gists="https://gist.github.com/sfmc-mobilepushsdk/d1caa687eedbf5792f1ea399cf8e5749.js" %}

Add the SDK dependency:
{% include gist.html sectionId="gradle_deps" names="Gradle" gists="https://gist.github.com/sfmc-mobilepushsdk/847e10adaed170e613a21986487d47d5.js" %}

> Replace `{currentVersion}` with {{ site.currentVersion }} to use the latest SDK version.

> The SDK no longer declares the Google Play Services Location or AltBeacon libraries as required dependencies.  Follow the instructions for enabling [Geofence]({{ site.baseurl }}/location/geolocation.html) and [Beacon]({{ site.baseurl }}/location/add-beacons.html) messaging if your application requires these features.

## 2.  Set up Firebase

Follow the [Android Firebase setup](https://firebase.google.com/docs/android/setup) documentation.  When you add the Firebase core dependency to your module gradle file, use `com.google.firebase:firebase-core:{{ site.firebaseMajor }}.x.x`.

> If you initialize the `FirebaseApp` manually, you must initialize Firebase before initializing the SDK.

## 3.  Initialize the SDK

The SDK **must** be initialized during the execution of your [Application](https://developer.android.com/reference/android/app/Application) class's `onCreate` method.  Initialization will require configuration data for your Marketing Cloud Application.  Please refer to the [documentation]({{ site.baseurl}}/create-apps/create-apps-overview.html#finding-your-marketing-cloud-application-configuration-data) for guidance on where to find the required pieces of data.

{% include gist.html sectionId="sdk_init" names="Kotlin,Java" gists="https://gist.github.com/sfmc-mobilepushsdk/4016de963a1e7f828faabab46c8bb1a3.js,https://gist.github.com/sfmc-mobilepushsdk/145c5fc15b5fdb0ba046a0fe7ace3f48.js" %}

> If you don’t call the SDK's `init` method from your app's `onCreate`, the SDK can’t post notifications while the app is in the background.

> To troubleshoot information the InitializationStatus returned during the SDK's initialization call, see [Initialization Status]({{ site.baseurl }}/trouble-shooting/init-status.html) in the troubleshooting section for more details.

> You can customize the display and handling of notifications. See the [customize notifications]({{ site.baseurl}}/notifications/customize-notifications.html) documentation for details.

## 4. Send a test push notification

Send a test push notification from the Marketing Cloud to your app.

---

To upgrade from a previous version, see the [upgrading documentation]({{ site.baseurl }}/overview/upgrading.html).
