---
layout: page
title: "Migrating to SFMCSdk"
subtitle: "SFMCSdk Migration"
category: sdk-implementation
date: 2021-10-14 12:00:00
order: 5
published: true
---

# Next-gen SDK migration steps

## Migrating to 8.x

This SDK update set a more modern architectural foundation to enable a variety of improvements and all new features moving forward. The next generation MobilePush SDK was designed with existing MobilePush customers in mind so that the upgrade path is light and straightforward. 


### Step 1 - Add the new Android SDK build

* *Fetch version 8.0.+ of the SDK -* Use the dependency:
* implementation('com.salesforce.marketingcloud:marketingcloudsdk:8.0.+')

### Step 2 - Initialize the SDK 

You will need to replace your existing initialization function to reference the new SDK. Once you have fetched the updated SDK version in your project, the functions that need to be modified will be highlighted in your codebase. 

1. *Update how you initialize the SDK* - Replace the legacy MarketingCloudSDK.init function with the new SFMCSdk.configure function.
2. *Note:* The SFMCSdk.configure function takes in a Marketing Cloud config as a parameter. This is the same config that you are using for your existing implementation and does not need to be changed. 

### Step 3 - Update identity functions

1. *Set the identity/ID of a known user* - Update your existing identity tracking function setContactKey to instead use the new function setProfileID
2. *Set the Attributes of a user* - Update your existing identity tracking function setAttribute to instead use the new function setProfileAttributes

Step 4 - Update remaining functions

1. Update your existing functions to reference the new SDK. All existing functions that need to be modified will be highlighted as deprecated in your codebase.