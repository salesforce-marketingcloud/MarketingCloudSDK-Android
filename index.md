---
layout: default
title: "Home"
---

## Android Q

Support for Android Q was added in version 6.3.4 of the SDK.  See the [Releases]({{site.baseurl}}/#releases) section for more details.

## Get Started

Integrate MobilePush into your mobile app using the MarketingCloudSdk:

1. Provision your app with [Google]({{ site.baseurl }}/provisioning/google.html).
1. [Create your app]({{ site.baseurl }}/create-apps/create-apps-overview.html) in MobilePush.
1. [Download the SDK and implement it for Google]({{ site.baseurl }}/sdk-implementation/implement-sdk-google.html). Add the SDK repository URL and dependency to your build script. The SDK is downloaded when you run the build.
1. Test your push messages.
    * Create a [test audience](https://help.salesforce.com/articleView?id=mc_mp_create_an_audience.htm&type=5).
    * Create a [test message](https://help.salesforce.com/articleView?id=mc_mp_outbound_message.htm&type=5) in the Marketing Cloud MobilePush app.
1. Implement additional optional features of the SDK to take full advantage of Salesforce Marketing Cloud.
    * Use a [contact key]({{ site.baseurl }}/sdk-implementation/device-contact-registration.html#contact-key) to set the unique identifier used to aggregate a contact's devices within Marketing Cloud. Set the contact key to a specific value provided by your customer or to another unique identifier for the contact, such as mobile number, email address, customer number, or another value.
    * Add [attributes]({{ site.baseurl }}/sdk-implementation/device-contact-registration.html#attributes) and [tags]({{ site.baseurl }}/sdk-implementation/device-contact-registration.html#tags) -- Enhance your ability to segment your push message audiences.
    * Add [predictive intelligence]({{ site.baseurl }}/features/analytics.html) using Personalization Builder -- Track cart and cart conversions. Purchase this feature separately.
    * Add other features -- Send push notifications along with your [inbox messages]({{ site.baseurl }}/inbox/inbox.html) and trigger location-based messages with [location and beacon messaging]({{ site.baseurl }}/location/geolocation-overview.html).

## Contact Us

Sign up for email announcements and contact us with questions or feedback about the Android SDK.
  * Email us at <marketingcloudsdkfeedback@salesforce.com>.
  * Post on our [Stack Exchange](https://salesforce.stackexchange.com/). Example tags: mobilepush-android and marketing-cloud
  * Sign up for [email updates](http://pub.s1.exacttarget.com/2ujjacpet3t) about release announcements and other important information. (10 or fewer emails per year)

## Additional Resources

* The [Marketing Cloud MobilePush Documentation](http://help.exacttarget.com/en/documentation/mobilepush/) contains information on the Marketing Cloud MobilePush app, including information on associating MobilePush with a mobile app.
* Review the [Javadocs]({{ site.baseurl }}/javadocs/{{site.currentMajorMinor}}/index.html) for the SDK.
* The Marketing Cloud provides the [LearningApp for Android](https://github.com/salesforce-marketingcloud/MarketingCloudSDK-Android) which implements features of the SDK to allow you to explore how a native app properly utilizes the MarketingCloudSDK.
* [Sign up for email updates about the Android SDK](http://pub.s1.exacttarget.com/2ujjacpet3t)
* View the [iOS MobilePush SDK docs](https://salesforce-marketingcloud.github.io/MarketingCloudSDK-iOS/)

## Hybrid Mobile Apps

We provide support for plugins, such as Cordova, to implement the MobilePush SDK for your Android applications.
* [Cordova Plugin](https://www.npmjs.com/package/cordova-plugin-marketingcloudsdk)

{% include release_notes.html %}
