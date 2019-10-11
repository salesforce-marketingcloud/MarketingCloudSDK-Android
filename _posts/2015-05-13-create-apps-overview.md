---
layout: page
title: "Overview"
subtitle: "Connect Marketing Cloud Account to Your Android App"
category: create-apps
date: 2015-05-14 12:00:00
order: 1
---
Review the steps to connect your Android app to your Marketing Cloud account in [MobilePush documentation](https://help.salesforce.com/articleView?id=mc_mp_provisioning_info.htm&type=5).

---

### Required Configuration Data <a name="configuration_requirements"></a>

Locate your app ID, access token, and app endpoint on your app's administration page in [MobilePush Administration](https://mc.exacttarget.com/cloud/#app/MobilePush/MobilePush/).

<img class="img-responsive" src="{{ site.baseurl }}/assets/setupConfigValues-222.png"   style="border:1px solid black"/><br/>

Locate your MID under your app’s name in the Marketing Cloud navigation bar.

<img class="img-responsive" src="{{ site.baseurl }}/assets/setupMidValues.png"  style="border:1px solid black" />

#### App Endpoint (Tenant-Specific Endpoint)

New implementations: Your app must use your app’s Marketing Cloud app endpoint value as part of the SDK configuration. Pass this value to `setMarketingCloudServerUrl()`  in the `MarketingCloudConfig.builder`.

Upgrades: If you upgrade to the SDK version released with the April 2019 release or later, update your configuration to include the app endpoint value. App endpoint is required beginning with the April 2019 release. If your app isn’t configured with an app endpoint, the SDK returns an error from the configuration method, and the SDK does _not_ function.

#### Access Token

Pass this value to `setAccessToken()` in the `MarketingCloudConfig.builder`.

#### App ID

Pass this value to `setApplicationId()` in the `MarketingCloudConfig.builder`.
