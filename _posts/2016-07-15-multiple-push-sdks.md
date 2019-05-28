---
layout: page
title: "Multiple Push SDKs"
subtitle: "Troubleshooting Multiple Push SDKs"
category: trouble-shooting
date: 2016-07-15 12:00:00
order: 4
---
While multiple push SDKs can be integrated into a single app, this may cause issues, and we cannot guarantee results. This section provides some considerations you should keep in mind as you develop your app. Areas of concern can include registration, geolocation and more. Note that this is not an exhaustive list.

> Any other push provider you choose must also allow a multiple push provider implementation.

{% assign _names="Kotlin,Java" %}

#### Remove SenderId from SDK init

**Do not** set the sender id during the Marketing Cloud SDK's initialization.  If the sender id is set during initialization the Marketing Cloud SDK will attempt to fetch a push token from the Firebase SDK.  This could lead to inconsistencies in the token that is registered with the Marketing Cloud.

#### Handle Push Token

Set the push token in the Marketing Cloud SDK whenever it has been retrieved/updated from the Firebase SDK.

{% include gist.html sectionId="handle_token" names=_names gists="https://gist.github.com/sfmc-mobilepushsdk/1be1b95c73dddcbcdb179d3d3666aa0c.js,https://gist.github.com/sfmc-mobilepushsdk/81be7f89d9ce5623ff6be52173583a84.js" %}

> The Marketing Cloud SDK **will not** send a registration request to the Marketing Cloud until the push token has been set.  <br/><br/>Not updating the SDK with a new push token when [onTokenRefresh](https://firebase.google.com/docs/reference/android/com/google/firebase/iid/FirebaseInstanceIdService.html#onTokenRefresh()) is triggered will prevent the Marketing Cloud from sending push messages to your application

#### Handle Push Message

When a push message is received from the Marketing Cloud pass it into the SDK to be presented.

{% include gist.html sectionId="handle_message" names=_names gists="https://gist.github.com/sfmc-mobilepushsdk/66659bd5487a3dc2ff240284626fbc97.js,https://gist.github.com/sfmc-mobilepushsdk/677e0fe836af0549e15e6496d02a1541.js" %}

> Messages passed into `handleMessage` that are not from the Marketing Cloud will be ignored.