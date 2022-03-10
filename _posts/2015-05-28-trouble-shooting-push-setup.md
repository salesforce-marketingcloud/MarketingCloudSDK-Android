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

{% include gist.html sectionId="get_push_token" names="8.x,7.x" gists="https://gist.github.com/sfmc-mobilepushsdk/a965fb321939c5befe7b854254ae8eff.js,https://gist.github.com/sfmc-mobilepushsdk/37dbb372438c4ceda897b235c93ab073.js" %}

* Use this script to send yourself a push message

<script src="https://gist.github.com/sfmc-mobilepushsdk/699fa2b23dd68377a8d835ac22b1dfad.js"></script>

* If you successfully receive a message using the sample script but still cannot to receive a message from the Marketing Cloud to your device, follow these steps:
    1. Wait {{ site.propagationDelay }} after the first registration call for the device you're testing with to ensure your device properly registered in the Marketing Cloud.
    1. Check the List you created in the Marketing Cloud and ensure the DeviceId you printed in the logcat shows up in the list.

#### Evaluate the SDK state

Look through the output from the Sdk's `getSdkState()` method.
{% include gist.html sectionId="evaluate_sdk_state" names="8.x,7.x" gists="https://gist.github.com/sfmc-mobilepushsdk/46250dad983bdf5e56fb5f2219dd5443.js,https://gist.github.com/sfmc-mobilepushsdk/8191cbff4e46d00a8a241444dba53511.js" %}

There is a lot of information provided in this method, but for debugging push messaging you will want to look specifically at the NotificationManager and PushMessageManager sections.
{% include gist.html sectionId="sdk_state" names="8.x,7.x" gists="https://gist.github.com/sfmc-mobilepushsdk/63d896be17c5c0c0866661cdcd8e7d3f.js,https://gist.github.com/sfmc-mobilepushsdk/91d0a4018c72d484c30414c74fc7841c.js" %}

If you implement multiple push providers, review the [Multiple Push SDKs]({{ site.baseurl }}/trouble-shooting/multiple-push-sdks.html) page for additional information.
