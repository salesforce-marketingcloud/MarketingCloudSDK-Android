---
layout: page
title: "Multiple Push SDKs"
subtitle: "Troubleshooting Multiple Push SDKs"
category: trouble-shooting
date: 2016-07-15 12:00:00
order: 4
---
MobilePush is able to work in the same app as other push vendor SDKs. However, we recommend checking with your other SDK vendor to ensure they also support a multi-push provider implementation. This page provides considerations for multiple push SDKs to successfully work together. Common areas for poor implementation can include device registration, geolocation and more. Note that this is not an exhaustive list.


> Any other push provider you choose must also allow a multiple push provider implementation.

#### Remove SenderId from SDK init

> **Do not** set the sender id during the SDK's initialization.

If the sender id is set during initialization the SDK will attempt to fetch a push token from the Firebase SDK.  This could lead to inconsistencies in the token that is registered with the Marketing Cloud.

#### Handle Push Token

Set the push token in the SDK whenever it has been retrieved/updated from the Firebase SDK.

{% include gist.html sectionId="handle_push_token" names="8.x,7.x" gists="https://gist.github.com/sfmc-mobilepushsdk/d8bc5b0cb6b31c5e47fddf823c4b58de.js,https://gist.github.com/sfmc-mobilepushsdk/01bfc40f0e326bbf72f10f0998e66992.js" %}

> Not updating the SDK with a new push token when [onNewToken](https://firebase.google.com/docs/reference/android/com/google/firebase/messaging/FirebaseMessagingService) is triggered will prevent the Marketing Cloud from sending push messages to your application.

#### Handle Push Message

When a push message is received from the Marketing Cloud pass it into the SDK to be presented.

{% include gist.html sectionId="handle_push_message" names="8.x,7.x" gists="https://gist.github.com/sfmc-mobilepushsdk/f87bca3235790967b41b34a44cae0e36.js,https://gist.github.com/sfmc-mobilepushsdk/66659bd5487a3dc2ff240284626fbc97.js" %}

> Messages passed into `handleMessage` that are not from the Marketing Cloud will be ignored.
