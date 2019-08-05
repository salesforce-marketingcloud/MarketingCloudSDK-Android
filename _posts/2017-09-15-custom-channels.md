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

After the SDK creates the NotificationChannel, the channel isn’t updated unless your application calls [NotificationManager#createDefaultNotificationChannel(Context context, boolean forceRecreate)]({{ site.baseurl}}/javadocs/{{ site.currentMajorMinor }}/reference/com/salesforce/marketingcloud/notifications/NotificationManager.html#createDefaultNotificationChannel(android.content.Context,%20boolean)).

### Custom Configuration

You can create custom notification channels and assign channels to each notification. See the example code as a guide.

<script src="https://gist.github.com/sfmc-mobilepushsdk/039b14ebd4e9913950dd10a8daa1f54d.js"></script>

To assign a notification to a channel, override the SDK channel callback. Make sure that you include a channel with your message. Otherwise, the app doesn’t display the message to the recipient.

> You can customize channel priority, colors, and sounds. To use custom sounds, give each channel its own sound configuration. You can’t use sounds with the SDK default channel option. For other notification customizations, see the [customize notifications](customize-notifications) documentation.
