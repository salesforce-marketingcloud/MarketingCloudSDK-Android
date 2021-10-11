---
layout: page
title: "Analytics, Einstein Recommendations, and Collect API"
subtitle: "Analytics, Einstein Recommendations, and Collect API"
category: analytics
date: 2015-05-14 12:00:00
order: 6
---
### Add Analytics

Enable analytics by setting parameters in `MarketingCloudConfig.Builder()`. To implement analytics in your mobile app, set the `analyticsEnabled` parameter to `true`. To implement [web and mobile analytics](https://help.salesforce.com/articleView?id=mc_anb_web__mobile_analytics.htm&type=5){:target=_blank} in your mobile app, set the `piAnalyticsEnabled` parameter to `true`.

{% include gist.html sectionId="add_analytics" names="8.x,7.x" gists="https://gist.github.com/sfmc-mobilepushsdk/ac5805c81cce41b7cfa7dcb56575bbdf.js,https://gist.github.com/sfmc-mobilepushsdk/e3a0ff48adb26a5a8b5da0dc1a1fc7e4.js" %}

### Integrate Einstein Recommendations and Collect API

#### Analytic Attribution
Einstein Recommendations analytics uses a unique identifier to attribute collected analytics to a specific user. By default, the SDK uses the contact key as this identifier, called the PIID. Your app can explicitly set this value. Review [setPiIdentifier]({{ site.baseurl }}/javadocs/MarketingCloudSdk/{{ site.currentMajorMinor }}/com.salesforce.marketingcloud.analytics/-analytics-manager/set-pi-identifier.html) and [setUseLegacyPiIdentifier]({{ site.baseurl }}/javadocs/MarketingCloudSdk/{{ site.currentMajorMinor }}/com.salesforce.marketingcloud/-marketing-cloud-config/-builder/set-use-legacy-pi-identifier.html) for details and configuration information.

> If the PIID isn’t set or is null and the SDK is configured to use the Legacy PI identifier, the SDK sends the contact key as the PIID.


##### Example: Analytic Attribution
{% include gist.html sectionId="analytic_attribution" names="8.x,7.x" gists="https://gist.github.com/sfmc-mobilepushsdk/223a16692da2f18a1f2acfe3c0a4f207.js,https://gist.github.com/sfmc-mobilepushsdk/5693edfd147bd8ca8a38cae30318fe0a.js" %}

#### Integration Methods
These methods integrate your mobile app with Einstein Recommendations. To use the methods, you must have an existing [Einstein Recommendations](https://help.salesforce.com/articleView?id=mc_pb_personalization_builder.htm&type=5) deployment, and you must enable the "PiAnalytics" option when you configure your SDK.

#### Track Cart

To track the contents of an in-app shopping cart, use trackCartContents(), as shown in the example. For more information about this method’s general use with Einstein Recommendations, see [Track Items in Shopping Cart](https://help.salesforce.com/articleView?id=mc_ctc_track_cart.htm{:target="_blank"}).

{% include gist.html sectionId="track_cart" names="8.x,7.x" gists="https://gist.github.com/sfmc-mobilepushsdk/47672986d40b4c2344407ca14bcdb993.js,https://gist.github.com/sfmc-mobilepushsdk/b7d308f9f70a0619e819992a37c8e64e.js" %}

#### Track Conversion

To track a purchase made through your mobile app, use trackCartConversion(), as shown in the example. For more information about this method’s general use with Einstein Recommendations, see [Track Purchase Details](https://help.salesforce.com/articleView?id=mc_ctc_track_conversion.htm&type=5){:target="_blank"}.

{% include gist.html sectionId="track_conversion" names="8.x,7.x" gists="https://gist.github.com/sfmc-mobilepushsdk/a497798d60461c237ad2d67bce6dcb3d.js,https://gist.github.com/sfmc-mobilepushsdk/7581ee94379c32d26d90c0c96ff5f173.js" %}

#### Track Page Views

To implement page-view analytics in your app, use trackPageView(), as shown in the example. For more information about this method’s general use with Einstein Recommendations, see [Track Items Viewed](http://help.marketingcloud.com/en/documentation/collect_code/install_collect_code/track_page_view/){:target="_blank"}.

{% include gist.html sectionId="track_page_views" names="8.x,7.x" gists="https://gist.github.com/sfmc-mobilepushsdk/39449d406c061de2b023ca49f5b02308.js,https://gist.github.com/sfmc-mobilepushsdk/a5c622993aa5339ab595accbbb57dfc2.js" %}

#### Track Inbox Message Opens

To track analytics for inbox messages, use trackInboxOpenEvent(). This method sends the open analytic value to Marketing Cloud, as in the example. The SDK automatically provides analytics for message downloads.

{% include gist.html sectionId="track_inbox_message_open" names="8.x,7.x" gists="https://gist.github.com/sfmc-mobilepushsdk/f4ad543f6f687869731746ec4767aeab.js,https://gist.github.com/sfmc-mobilepushsdk/0a9ce312b1386b5dcbfa15b1bc1273dd.js" %}

> Rights of ALBERT EINSTEIN are used with permission of The Hebrew University of Jerusalem. Represented exclusively by Greenlight.
