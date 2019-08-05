---
layout: page
title: "Device and Contact Registration"
subtitle: "Registration Updates Via Contact Key, Attributes, and Tags"
category: sdk-implementation
date: 2018-09-12 12:00:00
order: 3
---
The SDK will send changes to the Marketing Cloud servers within one minute of the first change to any Marketing Cloud Mobile Push device or contact registration record and no more frequently than once per minute after as required.  This can be data set by your application like Contact Key, Tags or Attributes or it could be system related and set by the SDK like Time Zone, Locale or Application Version, etc.

If the registration update fails the SDK will retry using a backoff algorithm with increasing retry intervals to a maximum delay of 1 day at which point the SDK will retry daily until it is successful.

It will take up to {{ site.propagationDelay }} for the registration data to be propagated throughout the Marketing Cloud servers and services once the REST call is successfully completed by the SDK.

> The SDK is considered the source of truth with regards to the contact's device registration.  Data is replicated 1-way from the SDK to the Marketing Cloud servers.

> Only modify the contact record while your UI is in the foreground - not the background.  This will optimize API calls to the Marketing Cloud servers.  For instance, setting an Attribute like “LastAppUsed” to a date-timestamp upon successful SDK initialization will result in an updated registration record every time the SDK is initialized.  If your app is backgrounded the SDK may initialize to show a notification or pre-cache messages.  This will result in unnecessary communication with the Marketing Cloud servers.

---
### Delay Registration Until Contact Key is Set

You can configure the SDK in a mode that delays registration until a contact key is set by the application. Use this configuration mode **only** in implementations where the device running your app is tied to a specific contact key in Marketing Cloud and not to a generic device contact.

<script src="https://gist.github.com/sfmc-mobilepushsdk/00ec2a1cce7803be9e91e0e2ce795394.js"></script>

---
### Contact Key

Contact key is the unique identifier used to aggregate a contact's devices within Marketing Cloud. Set the contact key to a specific value provided by your customer or to another unique identifier for the contact, such as mobile number, email address, customer number, or another value.

> Contact key can’t be null and can’t be an empty string. Leading or trailing whitespace is removed. After you set the contact key for a contact, you can change it, but you can’t clear it. Don’t set the contact key to a generic, non-unique value.

<script src="https://gist.github.com/sfmc-mobilepushsdk/41b11be25b6a9797ab6da63f0fbc91ca.js"></script>

> If your app doesn’t set the contact key using `setContactKey()`, Marketing Cloud uses the registration sent with a contact record that matches the system token included in the registration payload. If Marketing Cloud doesn’t find a match, it sets a new contact key and doesn’t send the value back to the SDK.

> Prior to Android SDK version 4.0.0, the SDK set a default contact key to a unique hash called `DeviceId`. For SDK versions 4.0.0 and later, the SDK no longer sets a default contact key because setting a default can create duplicate records for companies that import contacts.

---
### Attributes

You can use attributes to segment your audience and personalize your messages. Before you can use attributes, create them in your MobilePush account. Attributes may only be set or cleared by the SDK. See the [Reserved Words](#reserved-words) section for a list of attribute keys that can’t be modified by the SDK or your application.

<script src="https://gist.github.com/sfmc-mobilepushsdk/d08b52107fac6a18aafe9b900fb292fa.js"></script>

> The attribute key can not be one of the [reserved words](#reserved-words); it can’t be null or an empty string; and whitespace is trimmed.

> The attribute value can not be null and whitespace will be trimmed from the input.

---
### Tags

You can use tags to segment your audience. Tags are commonly used to allow customers to express their interests. For example, a news app offers predefined article categories, such as sports, headlines, and entertainment. App users select the categories, or tags, that they want to receive notifications for.

Dynamically add and remove tags via the SDK. You don’t have to create tags in Marketing Cloud.

<script src="https://gist.github.com/sfmc-mobilepushsdk/3447f513c11c5113134c3da45f3bcc6f.js"></script>

> Tags can not be null or an empty string, and whitespace is trimmed.

---
### Reserved Words

The SDK ignores calls to modify values associated with the following attribute keys because these attributes are reserved for internal use.

<script src="https://gist.github.com/sfmc-mobilepushsdk/d203ad25ac96ed8cb570d9c40910cf0a.js"></script>
