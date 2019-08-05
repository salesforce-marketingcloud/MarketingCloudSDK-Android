---
layout: page
title: "Analytics, Personalization Builder, and Collect API"
subtitle: "Analytics, Personalization Builder, and Collect API"
category: features
date: 2015-05-14 12:00:00
order: 6
---
### Add Analytics

Enable analytics by setting parameters in `MarketingCloudConfig.Builder()`. To implement analytics in your mobile app, set the `analyticsEnabled` parameter to `true`. To implement [web and mobile analytics](https://help.salesforce.com/articleView?id=mc_anb_web__mobile_analytics.htm&type=5){:target=_blank} in your mobile app, set the `piAnalyticsEnabled` parameter to `true`.

<script src="https://gist.github.com/sfmc-mobilepushsdk/e3a0ff48adb26a5a8b5da0dc1a1fc7e4.js"></script>

### Integrate Personalization Builder and Collect API

#### Analytic Attribution
Personalization Builder analytics uses a unique identifier to attribute collected analytics to a specific user. By default, the SDK uses the contact key as this identifier, called the PIID. Your app can explicitly set this value. Review [AnalyticsManager#setPiIdentifier]({{ site.baseurl}}/javadocs/{{ site.currentMajorMinor }}/reference/com/salesforce/marketingcloud/analytics/AnalyticsManager.html#setPiIdentifier(java.lang.String)) and [MarketingCloudConfig.Builder#setUseLegacyPiIdentifier]({{ site.baseurl}}/javadocs/{{ site.currentMajorMinor }}/reference/com/salesforce/marketingcloud/MarketingCloudConfig.Builder.html#setUseLegacyPiIdentifier(boolean)) for details and configuration information.

> If the PIID isn’t set or is null and the SDK is configured to use the Legacy PI identifier, the SDK sends the contact key as the PIID.


##### Example: Analytic Attribution
<script src="https://gist.github.com/sfmc-mobilepushsdk/5693edfd147bd8ca8a38cae30318fe0a.js"></script>


#### Integration Methods
These methods integrate your mobile app with Personalization Builder. To use the methods, you must have an existing [Personalization Builder](https://help.salesforce.com/articleView?id=mc_pb_personalization_builder.htm&type=5) deployment, and you must enable the "PiAnalytics" option when you configure your SDK.

#### Track Cart

To track the contents of an in-app shopping cart, use trackCartContents(), as shown in the example. For more information about this method’s general use with Personalization Builder, see [Track Items in Shopping Cart](https://help.salesforce.com/articleView?id=mc_ctc_track_cart.htm{:target="_blank"}).

<script src="https://gist.github.com/sfmc-mobilepushsdk/b7d308f9f70a0619e819992a37c8e64e.js"></script>

#### Track Conversion

To track a purchase made through your mobile app, use trackCartConversion(), as shown in the example. For more information about this method’s general use with Personalization Builder, see [Track Purchase Details](https://help.salesforce.com/articleView?id=mc_ctc_track_conversion.htm&type=5){:target="_blank"}.

<script src="https://gist.github.com/sfmc-mobilepushsdk/7581ee94379c32d26d90c0c96ff5f173.js"></script>

#### Track Page Views

To implement page-view analytics in your app, use trackPageView(), as shown in the example. For more information about this method’s general use with Personalization Builder, see [Track Items Viewed](http://help.marketingcloud.com/en/documentation/collect_code/install_collect_code/track_page_view/){:target="_blank"}.

<script src="https://gist.github.com/sfmc-mobilepushsdk/a5c622993aa5339ab595accbbb57dfc2.js"></script>

#### Track Inbox Message Opens

To track analytics for inbox messages, use trackInboxOpenEvent(). This method sends the open analytic value to Marketing Cloud, as in the example. The SDK automatically provides analytics for message downloads.

<script src="https://gist.github.com/sfmc-mobilepushsdk/0a9ce312b1386b5dcbfa15b1bc1273dd.js"></script>
