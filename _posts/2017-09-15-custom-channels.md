---
layout: page
title: "Notification Channels"
subtitle: "Implement Default and Custom Notification Channels"
category: notifications
date: 2017-09-15 12:00:00
order: 1
---

### Default Configuration

On devices running Android Oreo or newer, the SDK creates a notification channel called “marketing” to assign all notifications to. To change the name of the default channel, set a string resource value for `mcsdk_default_notification_channel_name`.

After the SDK creates the NotificationChannel, the channel isn’t updated unless your application calls [NotificationManager#createDefaultNotificationChannel(Context context, boolean forceRecreate)]({{ site.baseurl}}/javadocs/{{ site.currentMajorMinor }}/com.salesforce.marketingcloud.notifications/-notification-manager/create-default-notification-channel.html).

### Custom Configuration

You can create custom notification channels and assign channels to each notification. See the example code as a guide.
{% include gist.html sectionId="notif_custom_channel" names="8.x,7.x" gists="https://gist.github.com/sfmc-mobilepushsdk/6b8ec470ac81ad2ce9d93c548357ec17.js,https://gist.github.com/sfmc-mobilepushsdk/039b14ebd4e9913950dd10a8daa1f54d.js" %}

To assign a notification to a channel, override the SDK channel callback. Make sure that you include a channel with your message. Otherwise, the app doesn’t display the message to the recipient.

> You can customize channel priority, colors, and sounds. To use custom sounds, give each channel its own sound configuration. You can’t use sounds with the SDK default channel option. For other notification customizations, see the [customize notifications](customize-notifications) documentation.
