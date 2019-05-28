---
layout: page
title: "Silent Pushes"
subtitle: "Using Silent Pushes"
category: features
date: 2015-07-02 12:00:00
order: 13
---
#### Silent Push Messages for MobilePush
This document contains conceptual and procedural information about sending a silent push message to a mobile app using the MobilePush app and the REST API.
 
#### What are Silent Push Messages
A silent push message appears on a mobile app without triggering a visual or audible alert on the mobile device. Examples include subscriptions read inside the Google Newsstand app or updates to messages within an app that do not require notifications.
 
#### How to Send Silent Push Messages
Follow the steps below to create and send silent push messages:
 
1. Create an API-triggered MobilePush message.
1. Ensure you set the **content-available** property to **1**.
1. Set the override property to true.
1. Use the sample payload below as a model for your own message:
 
<script src="https://gist.github.com/sfmc-mobilepushsdk/4f4ab7311a39657e9356.js"></script>
 
Once you create the original message, you can pass text to the message using subsequent messages as part of the Override value.
 
#### Receiving Silent Push Messages
Refer to the Events documentation for information on how to listen for silent push notifications.
 
