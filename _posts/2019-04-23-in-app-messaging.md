---
layout: page
title: "In-App Messaging"
subtitle: "In-App Messaging"
category: in-app-message
date: 2019-04-23 12:00:00
order: 1
---

Deliver relevant, personalized messages to your app’s users with in-app messaging. You can now send messages without relying on users having enabled push notifications. Engaging full-screen, modal, or banner messages are presented while your users are interacting with your app.

To ensure that your app has the latest message data, in-app messages are loaded each time your app comes to the foreground. When your app comes into the foreground, any messages marked for display are prepared to be presented at the top of your app's view stack. Only one message is displayed each time the app comes into the foreground. Depending on how your app is enabled, the SDK can be triggered to download in-app messages in the background.

You can fully enable in-app messages in [Marketing Cloud Journey Builder](https://help.salesforce.com/articleView?id=mc_jb_configure_inapp_in_journey_builder.htm&type=5). To use certain in-app messaging functionality, such as button actions, some SDK configuration is required.

### Required Methods for Button Actions

Marketers can configure the action that occurs when an end-user taps a button on an in-app message. Actions for *Notification Settings* and *Location Settings* are handled by the SDK, while actions for *Web URL* and *App URL* require that you implement a new SDK initialization method, as shown in the following example.

<script src="https://gist.github.com/sfmc-mobilepushsdk/e26e7a94744e27cda902b88ececb42b9.js"></script>

### Optional Methods

You can use the SDK’s optional in-app messaging [EventListener]({{ site.baseurl }}/javadocs/{{ site.currentMajorMinor }}/com.salesforce.marketingcloud.messages.iam/-in-app-message-manager/-event-listener/index.html) to control aspects of message display and to get information about the in-app message display lifecycle.


After the SDK's initialization is complete you can register your in-app message [EventListener]({{ site.baseurl }}/javadocs/{{ site.currentMajorMinor }}/com.salesforce.marketingcloud.messages.iam/-in-app-message-manager/-event-listener/index.html) with the [InAppMessageManager]({{ site.baseurl }}/javadocs/{{ site.currentMajorMinor }}/com.salesforce.marketingcloud.messages.iam/-in-app-message-manager/index.html).

<script src="https://gist.github.com/sfmc-mobilepushsdk/93b17c5aae85b915e0e8b46d5b66d570.js"></script>

#### didShowMessage and didCloseMessage
The following delegate methods help ensure that you can appropriately manage your app's view state. In-app messages are shown in an activity controlled by the SDK in your app's activity stack. Your app may need to respond to a view appearing or disappearing.

[didShowMessage]({{ site.baseurl }}/javadocs/{{ site.currentMajorMinor }}/com.salesforce.marketingcloud.messages.iam/-in-app-message-manager/-event-listener/did-show-message.html) is called when the in-app message is initially presented.

[didCloseMessage]({{ site.baseurl }}/javadocs/{{ site.currentMajorMinor }}/com.salesforce.marketingcloud.messages.iam/-in-app-message-manager/-event-listener/did-close-message.html) is called after the user performs closes the message.

#### Prevent or Delay Message Display
You can delay or prevent an in-app message’s display. For example, prevent an in-app message from displaying during loading, instructions, the sign-in flow, and other situations. To prevent display of the message, make the [shouldShowMessage]({{ site.baseurl }}/javadocs/{{ site.currentMajorMinor }}/com.salesforce.marketingcloud.messages.iam/-in-app-message-manager/-event-listener/should-show-message.html) method return false.

You can capture the message data and show that message later. For example, present the message after the end-user has signed in. Capture the ID of the message and show that message later.

<script src="https://gist.github.com/sfmc-mobilepushsdk/ccdaba4245c2e28b631bf2ca5bb9c453.js"></script>

#### Customize Display

In-app messages use your device's system font. You can override the default font face to customize the display of an in-app message title, body, and button labels.

> You can’t alter the font size because this is defined by the message's design.

To set the display font, pass the SDK a valid [TypeFace](https://developer.android.com/reference/android/graphics/Typeface) for the device's installed fonts or your app's custom fonts via the following method.

<script src="https://gist.github.com/sfmc-mobilepushsdk/d26c90a87b3f782192dfad8f943ae070.js"></script>

The SDK can’t use AppCompatActivity and can’t inherit your app's status bar color. Instead, set a custom status bar color in your app's theme. The SDK applies this color on Android API versions that support status bar color, which is Lollipop and newer.

<script src="https://gist.github.com/sfmc-mobilepushsdk/481804515687b4d2e8882a2e2a12e1af.js"></script>
