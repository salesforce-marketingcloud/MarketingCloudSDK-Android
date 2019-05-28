---
layout: page
title: "Upgrading"
subtitle: "Upgrading From a Previous Release"
category: overview
date: 2015-05-14 12:00:00
order: 3
---
Below are the required changes to upgrade from an older SDK release to a newer SDK release. These instructions assume Android Studio is the IDE.

# 6.0.x

With the 6.0.x release of the SDK we have integrated Firebase Cloud Messaging (FCM), simplified the steps required to customize notifications and removed transient dependencies for non-push functionality to simplify integration for customers not using geofence/beacon messaging.

* Replaced Google Cloud Messaging (GCM) with Firebase Cloud Messaging (FCM).  This change will require additional steps be taken before initializing the SDK.  See the [SDK integrations steps]({{ site.baseurl }}/sdk-implementation/implement-sdk-google.html) page for more details.
* Notification Customizations have been aggregated into one [NotificationCustomizationOptions]({{ site.baseurl}}/javadocs/{{ site.currentMajorMinor }}/reference/com/salesforce/marketingcloud/notifications/NotificationCustomizationOptions.html) class.  This class can be used to setup the Launch Intent for notification taps, customized the notification channel (see [Simplified Customization]({{ site.baseurl}}/notifications/customize-notifications#simplified-customization)) or take full control of the notification by providing a builder (see [Full Control Customization]({{ site.baseurl}}/notifications/customize-notifications#full-control-customization)).
* Default Title has been removed from the SDK's Configuration Builder as the title is part of the [NotificationMessage]({{ site.baseurl}}/javadocs/6.0/reference/com/salesforce/marketingcloud/notifications/NotificationMessage.html) payload.
* A default Notification Channel is created by the SDK.  See [Custom Channels]({{ site.baseurl}}/notifications/custom-channels) for details on how to override this behavior.
* Previous versions of the SDK would package in the dependencies required for geofence message (play-services-location) and beacon message (android-beacon-library).  This would require customer who did not utilize these feature to take extra steps to exclude them from their builds.  With this release, if you are using these features you will need to add their dependencies to your build. For more details see the [Geofences]({{ site.baseurl }}/location/geolocation.html) and [Beacons]({{ site.baseurl }}/location/add-beacons.html) pages.
* Removed class that were deprecated in previous versions:
  * Attribute
  * CloudPageMessage
  * CloudPageMessageManager
  * DefaultUrlPresenter