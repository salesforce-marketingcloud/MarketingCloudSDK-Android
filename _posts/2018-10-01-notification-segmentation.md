---
layout: page
title: "Notification Segmentation"
subtitle: "Notification Segmentation"
category: notifications
date: 2018-10-01 12:00:00
order: 4
---
You can deliver location messages to a subset of your MobilePush audience by using the Android SDK. For example, when a mobile device breaks a geofence or comes into proximity of a Bluetooth beacon, the SDK can trigger a callback to your mobile app to evaluate whether to show the message.

Your app can apply criteria, including information within the MobilePush message itself, to determine whether to show the push notification, geofence message, or beacon message. Here are some use cases.

- Show the message if the user’s `my store` location is within the geofence region.
- Show the message if the user has items in the app shopping cart and if the message has an "abandonedCart":"true" custom key.
- Show the message only when the store is open based on the app’s store location list and the region and local time.
- Don’t show messages if the user isn’t logged in to the app.
- Show specific messages based on the user’s customer profile. For example, customers who are coffee lovers get messages about coffee, but customers who prefer pastry receive a message to “add a coffee” when they're in the shop.

### Segment Messages

Provide an implementation of [ShouldShowNotificationListener]({{ site.baseurl }}/javadocs/{{ site.currentMajorMinor }}/com.salesforce.marketingcloud.notifications/-notification-manager/-should-show-notification-listener/index.html) to the [NotificationManager]({{ site.baseurl }}/javadocs/{{ site.currentMajorMinor }}/com.salesforce.marketingcloud.notifications/-notification-manager/index.html). When you implement [shouldShowNotification]({{ site.baseurl }}/javadocs/{{ site.currentMajorMinor }}/com.salesforce.marketingcloud.notifications/-notification-manager/-should-show-notification-listener/should-show-notification.html), you can access all information about the triggering message, including [Region]({{ site.baseurl }}/javadocs/{{ site.currentMajorMinor }}/com.salesforce.marketingcloud.messages/-region/index.html) data for geofence and beacon triggers.

#### Example
To make sure that only gold loyalty-level customers receive the message in this example, the marketer sets the custom key “loyaltyLevel” to “gold” when creating the location message. Then, the message is shown only if the customer's contact attribute is also set to “gold”.
{% include gist.html sectionId="notification_segment" names="8.x,7.x" gists="https://gist.github.com/sfmc-mobilepushsdk/a6cbc33a0ee28bd5c077a13e2bbd042d.js,https://gist.github.com/sfmc-mobilepushsdk/3cc858af908f1b559677294abd1a1364.js" %}
