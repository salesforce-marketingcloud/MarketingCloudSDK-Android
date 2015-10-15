package com.salesforce.marketingcloud;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.exacttarget.etpushsdk.ETPush;
import com.exacttarget.etpushsdk.ETPushConfig;
import com.exacttarget.etpushsdk.ETException;

/**
 * Created by sebastian on 10/13/15.
 */
public class ApplicationClass extends Application{

    private static final String TAG = ApplicationClass.class.getSimpleName();
    private static Context appContext;

    public static Context context() {
        return appContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        try {
            ETPush.readyAimFire(new ETPushConfig.Builder(this)
                            .setEtAppId(CONSTS_API.getEtAppId())
                            .setAccessToken(CONSTS_API.getAccessToken())
                            .setGcmSenderId(CONSTS_API.getGcmSenderId())
                            .setAnalyticsEnabled(false)
                            .setPiAnalyticsEnabled(false)
                            .setLocationEnabled(true)      // set to true ONLY if you purchased Location as it requires additional overhead
                            .setCloudPagesEnabled(false)    // set to true ONLY if you purchased RichPush as it requires additional overhead
                            .setLogLevel(BuildConfig.DEBUG ? android.util.Log.VERBOSE : android.util.Log.ERROR)
    		/* Builder methods to override SDK behavior */
                                    //.setCloudPageRecipientClass(SomeActivity.class) // Override ETLandingPagePresenter
                                    //.setOpenDirectRecipientClass(SomeActivity.class) // Override ETLandingPagePresenter
                                    //.setNotificationRecipientClass(SomeActivity.class) // Override Notification Handling
                                    //.setNotificationAction("some_string")
                                    //.setNotificationActionUri(Uri.parse("some_uri"))
                                    //.setNotificationResourceId(R.drawable.some_drawable)
                            .build()
            );
        }
        catch (ETException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }



}
