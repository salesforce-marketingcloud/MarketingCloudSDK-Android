---
layout: page
title: "Push Setup"
subtitle: "Push Messages Not Displayed"
category: trouble-shooting
date: 2015-05-14 12:00:00
order: 1
---
If you encounter issues receiving messages in your app, consider these troubleshooting items:

#### Check the SDK's log output

 [Enabling logging]({{ site.baseurl }}/trouble-shooting/loginterface.html) in the SDK and verify no errors are being logged.
 > Ensure you attempt this important debugging step. The SDK provides very verbose messages, and you can correct many errors and issues by reviewing the logcat.

#### Send a test push

Test that your device can receive a push directly from FCM.

* Get the push token from the SDK

<script src="https://gist.github.com/sfmc-mobilepushsdk/37dbb372438c4ceda897b235c93ab073.js"></script>

* Use this script to send yourself a push message

<script src="https://gist.github.com/sfmc-mobilepushsdk/699fa2b23dd68377a8d835ac22b1dfad.js"></script>

* If you successfully receive a message using the sample script but still cannot to receive a message from the Marketing Cloud to your device, follow these steps:
    1. Wait {{ site.propagationDelay }} after the first registration call for the device you're testing with to ensure your device properly registered in the Marketing Cloud.
    1. Check the List you created in the Marketing Cloud and ensure the DeviceId you printed in the logcat shows up in the list.

#### Evaluate the SDK state

Look through the output from the MarketingCloudSDK's `getSdkState()` method.
<script src="https://gist.github.com/sfmc-mobilepushsdk/426e01da0ed63d9e2580eafe6bee8ea3.js"></script>

There is a lot of information provided in this method, but for debugging push messaging you will want to look specifically at the NotificationManager and PushMessageManager sections.
<script src="https://gist.github.com/sfmc-mobilepushsdk/91d0a4018c72d484c30414c74fc7841c.js"></script>

If you implement multiple push providers, review the [Multiple Push SDKs]({{ site.baseurl }}/trouble-shooting/multiple-push-sdks.html) page for additional information.
