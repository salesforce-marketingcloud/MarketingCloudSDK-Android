# MobilePush SDK LearningApp Documentation

The MobilePush SDK LearningApp is offered as an example of MobilePush SDK project configuration and implementation best practices.

To easily integrate the MobilePush SDK into your new or existing mobile app, use the LearningApp in conjunction with the [Android](https://developer.salesforce.com/docs/marketing/mobilepush/guide/android-sdk-integration.html) MobilePush SDK documentation, the [Android Marketing Cloud SDK Javadoc](https://salesforce-marketingcloud.github.io/MarketingCloudSDK-Android/javadocs/MarketingCloudSdk/8.0/index.html), and the [Android SFMC SDK Javadoc](https://salesforce-marketingcloud.github.io/MarketingCloudSDK-Android/javadocs/SFMCSdk/8.0/index.html)references.

Additionally, should you face a challenge in your MobilePush SDK implementation, the LearningApp can serve as a simple, streamlined test application to validate integration and configuration outside of the complexities of a modern, fully featured mobile app.

The LearningApp demonstrates

* Configuring the SDK
* Enabling push notifications
* Handling Marketing Cloud push URLs
* Enabling geofence messaging
* Setting contact values via SDK registration
* Logging SDK diagnostic information via sdkState
* A fully-featured app inbox, enabling inbox messaging
* Support for In-App Message callbacks
* Access to the MobilePush SDK documentation

For further information about implementing the MobilePush SDK, see the documentation

* https://developer.salesforce.com/docs/marketing/mobilepush/guide/android-sdk-integration.html

**Requirements**

* Android Studio 3.4 or later

**Setup**

1. Clone the Learning App from the source repository.
1. Open the project in Android Studio.
1. After the initial build completes, the following error appears. To resolve this, add the Learning App's package (`com.salesforce.marketingcloud.learningapp`) to the Firebase project that is associated with your Marketing Cloud application and download the `google-services.json` file.

        FAILURE: Build failed with an exception.
    
        * What went wrong:
        Execution failed for task ':app:processBasicDebugGoogleServices'.
        > File google-services.json is missing. The Google Services Plugin cannot function without it.
       
1. Paste the file into the /app directory of the Learning App project.
1. Open the gradle.properties file in the LearningApp directory and replace the values for MC_APP_ID, MC_ACCESS_TOKEN, MC_SENDER_ID, MC_MID and MC_SERVER_URL. Find these values on the MobilePush Administration screen in Marketing Cloud.

        MC_APP_ID="{replace with marketing cloud app id}"
        MC_ACCESS_TOKEN="{replace with marketing cloud access token}"
        MC_SENDER_ID="{replace with marketing cloud fcm sender id}"
        MC_MID="{replace with marketing cloud mid}"
        MC_SERVER_URL="{replace with marketing cloud server url}"
        
1. To deploy the Learning App to your Android device, click **Run** in Android Studio.

**Development Notes for Android SDK** 

The LearningApp uses Android product flavors to demonstrate common customer user-cases. The default product flavor (basic) shows the simplest configuration to enable push, inbox, geofence, and proximity messaging. We provide additional product flavors to demonstrate deep-linking from notifications (deeplinking) and multi-push providers (mpp).

The majority of the code is shared in the /main source set with product flavor-specific changes in the respective source set directories.

