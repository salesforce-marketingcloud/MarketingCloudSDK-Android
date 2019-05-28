---
layout: default
title: "Home"
---
## Important Updates

> Google has announced that, as of April 11, 2019, they will deprecate the Google Cloud Messaging endpoint that Marketing Cloud MobilePush currently uses. After April 2019, devices with Android SDK versions older than 6.0.x will not receive push notifications from Marketing Cloud.

* In version 6.0.1, we removed the [deprecated](https://android-developers.googleblog.com/2018/04/time-to-upgrade-from-gcm-to-fcm.html) Google Cloud Messaging library and replaced it with Firebase Cloud Messaging.  You will now need to configure your application so that Firebase is initialized.  See [Implementing the SDK in Android]({{ site.baseurl }}/sdk-implementation/implement-sdk-google.html) for more details.
* The Android SDK now supports only TLS 1.1 and higher. You must use SDK version 6.0.x or newer to ensure communication with Marketing Cloud.

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
* The Marketing Cloud provides the [Learning App for Android](https://github.com/salesforce-marketingcloud/LearningAppAndroid) that implements features of the SDK to allow you to explore how a native app implements the MarketingCloudSDK.
* [Sign up for email updates about the Android SDK](http://pub.s1.exacttarget.com/2ujjacpet3t)
* View the [iOS MobilePush SDK docs](https://salesforce-marketingcloud.github.io/MarketingCloudSDK-iOS/)

## Hybrid Mobile Apps

We provide support for plugins, such as Cordova, to implement the MobilePush SDK for your Android applications.
* [Cordova Plugin](https://www.npmjs.com/package/cordova-plugin-marketingcloudsdk)

#### Version 6.2.2
_Released May 21st, correlating with the Marketing Cloud April 2019 release._

> Version 6.2.2 of the Android SDK has been tested with Google Play Services version 16.0.0 and AltBeacon Library version 2.16.1.  The minimum compatible Android API version is 16.  Compiled with Android API version 28.

* **Prevent crash on Android O or newer**-Some devices running Android O, or newer can exhibit a bug where the SDK will attempt to create a job using Android's JobScheduler when there are already 100 scheduled jobs.  This would result in Android throwing an IllegalStateException.  We added code to prevent this crash and improve logging when the issue occurs.

#### Version 6.2.1
_Released April 23rd, correlating with the Marketing Cloud April 2019 release._

> Version 6.2.1 of the Android SDK has been tested with Google Play Services version 16.0.0 and AltBeacon Library version 2.16.1.  The minimum compatible Android API version is 16.  Compiled with Android API version 28.

* **Required app endpoint (tenant-specific endpoint)**—To pass the SDK's configuration, an app endpoint is now required. [Find the app endpoint](https://help.salesforce.com/articleView?id=mc_mp_provisioning_info.htm&type=5#mc_mp_provisioning_info) for your app under Administration in MobilePush. Review [Configuration Requirements]({{ site.baseurl }}/create-apps/create-apps-overview.html#configuration_requirements) for details.

* **Enforced critical update to Firebase Cloud Messaging for Android SDK**—For your app to continue receiving messages, you must update your SDK to version 6.0.1 or later and [configure your app with Firebase initialized]({{ site.baseurl }}/sdk-implementation/implement-sdk-google.html).

* **Added ability to delay registration until contact key is set**—Use a new SDK configuration value to delay registrations to Marketing Cloud until a contact key is set via [`setDelayRegistrationUntilContactKeyIsSet()`]({{ site.baseurl }}/javadocs/6.2/reference/com/salesforce/marketingcloud/MarketingCloudConfig.Builder.html#setDelayRegistrationUntilContactKeyIsSet(boolean)). Review [Delay Registration]({{ site.baseurl }}/sdk-implementation/device-contact-registration.html#delay-registration-until-contact-key-is-set) for details.

* **Inbox messages support `sendDateUtc` for sorting**—Inbox messages delivered to your app now include the date that the message was sent. App users can sort their inbox based on this value. Review [Inbox]({{ site.baseurl }}/inbox/inbox.html) for more information.

* **Added ability to set read or deleted by inbox message ID only**—Added convenience methods to set the status of an inbox message to read or deleted with only the message's ID as an argument.

#### Version 6.1.0
_Released February 11, 2019, correlating with the Marketing Cloud January 2019 release._

> Version 6.1.0 of the Android SDK has been tested with Google Play Services version 16.0.0 and AltBeacon Library version 2.15.2.  The minimum compatible Android API version is 16.  Compiled with Android API version 28.

* **Implemented message segmentation**—The Android SDK now supports app control over which push notifications, geofence messages, and beacon messages are displayed. Use the region information provided for geofence and beacon messages in your notification presentation logic. Review [Notification Segmentation]({{ site.baseurl }}/notifications/notification-segmentation.html).
* **Implemented predictive intelligence identifier (PIID) configuration options and APIs**—You can configure the identifier for predictive intelligence analytics according to how you use Personalization Builder. Configure your applications to use either existing contact key data or a PI-specific identifier. Review [Analytics, Personalization Builder, and Collect API]({{ site.baseurl }}/features/analytics.html).
* **Added ability to unregister a WhenReadyListener passed to requestSdk**—Reference the [`unregisterWhenReadyListener()`]({{ site.baseurl }}/javadocs/6.1/reference/com/salesforce/marketingcloud/MarketingCloudSdk.html#unregisterWhenReadyListener(com.salesforce.marketingcloud.MarketingCloudSdk.WhenReadyListener)) documentation.

#### Version 6.0.2
_Released December 05, 2018, correlating with the Marketing Cloud 215.1 release._

> Version 6.0.2 of the Android SDK depends on Google Play Services version 15.0.1 and AltBeacon Library version 2.14.  The minimum compatible Android API version is 16.  Compiled with Android API version 27.

* **Corrected Registration Frequency Issue** -- We corrected an issue in the SDK that would allow for multiple registration requests to be sent to the Marketing Cloud within a small amount of time.  Now, registration edits will be properly batched and sent once per minute.
* **Improved SdkState Logging** -- To improve our ability to support you during debugging sessions we have improved the SDK state output.

#### Version 6.0.1
_Released October 22, 2018, correlating with the Marketing Cloud 215 release._

> Version 6.0.1 of the Android SDK depends on Google Play Services version 15.0.1 and AltBeacon Library version 2.14.  The minimum compatible Android API version is 16.  Compiled with Android API version 27.

* **Replaced GCM with FCM** -- We removed the [deprecated](https://android-developers.googleblog.com/2018/04/time-to-upgrade-from-gcm-to-fcm.html) Google Cloud Messaging library and replaced it with Firebase Cloud Messaging.  You will now need to configure your application so that Firebase is initialized.  See [Implementing the SDK in Android]({{ site.baseurl }}/sdk-implementation/implement-sdk-google.html) for more details.
* **Simplified Notification Customization & Handling** -- The [MarketingCloudConfig.Builder]({{ site.baseurl}}/javadocs/6.0/reference/com/salesforce/marketingcloud/MarketingCloudConfig.Builder.html) has been simplified by consolidating all of the notification customization options into a single [NotificationCustomizationOptions]({{ site.baseurl}}/javadocs/6.0/reference/com/salesforce/marketingcloud/notifications/NotificationCustomizationOptions.html) class.  See [Customizing Notifications]({{ site.baseurl }}/notifications/customize-notifications) for details.
* **Fewer Required Dependencies** -- The Google Play Services Location and AltBeacon dependencies are no longer declared in the SDK's pom file as required dependencies.  Follow the instructions for enabling [Geofence]({{ site.baseurl }}/location/geolocation.html) and [Beacon]({{ site.baseurl }}/location/add-beacons.html) messaging if your application requires these features.
* **Improved Initialization Verification** -- We added additional checks to the `MarketingCloudConfig` builder to ensure that the necessary information is provided before the SDK is initialized.  These checks include ensuring that an icon resource for notifications is provided and that notification channels are handled.  To allow for this change you will need to update your call to [MarketingCloudConfig.Builder#build(Context)]({{site.baseurl}}/javadocs/6.0/reference/com/salesforce/marketingcloud/MarketingCloudConfig.Builder.html#build(android.content.Context)) to pass in an Android Context.
* **Default Notification Channel** -- the SDK will now create a default notification channel named "Marketing".  See [Custom Channels]({{ site.baseurl}}/notifications/custom-channels) for details on how to modify this channel.
* **Removed Attribute class**  -- We removed the previously deprecated `Attribute` class along with any associated methods.  Registration attributes are now provided in a key-value Map.
* **Removed DefaultUrlPresenter** -- We removed the SDK's built-in URL presenter to remove security concerns.  As such, you must override notification handling if your message will redirect to a web URL, resource, file or other business logic driven custom application schema.  See [Handling URLs]({{ site.baseurl}}/sdk-implementation/url-handling.html) for more information.
* **Tenant Specific Endpoint** -- Two new methods were added to the [MarketingCloudConfig.Builder]({{ site.baseurl}}/javadocs/6.0/reference/com/salesforce/marketingcloud/MarketingCloudConfig.Builder.html) to facilitate Tenant Specific Endpoints: [`setMarketingCloudServerUrl()`]({{ site.baseurl }}/javadocs/6.0/reference/com/salesforce/marketingcloud/MarketingCloudConfig.Builder.html#setMarketingCloudServerUrl(java.lang.String)) and [`setMid()`]({{ site.baseurl }}/javadocs/6.0/reference/com/salesforce/marketingcloud/MarketingCloudConfig.Builder.html#setMid(java.lang.String)) these values will configure the SDK to communicate with the specific server for your application.  Please refer to the [documentation]({{ site.baseurl}}/create-apps/create-apps-overview.html#finding-your-marketing-cloud-application-configuration-data) for guidance on where to find your Tenant Specific Endpoint and MID.
* **Improved Kotlin interoperability** -- Addressed issues reported by the [Kotlin interoperability](https://android.github.io/kotlin-guides/interop.html) Android lint check.
  * Added nullability annotations to all public APIs.
  * Reversed parameter order of MarketingCloudSdk's [whenReady]({{site.baseurl}}/javadocs/6.0/reference/com/salesforce/marketingcloud/MarketingCloudSdk.html#requestSdk(android.os.Looper,%20com.salesforce.marketingcloud.MarketingCloudSdk.WhenReadyListener)) method to make the interface eligible for [SAM conversion](https://kotlinlang.org/docs/reference/java-interop.html#sam-conversions).
* **Fixed region message url locale bug** - For locales that do not use `.` as the decimal separator, the SDK would request region messages in a format that is not supported by the Marketing Cloud.  This was corrected to enforce that `.` would be used regardless of device locale.
