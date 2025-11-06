/**
 * Copyright 2019 Salesforce, Inc.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.salesforce.marketingcloud.learningapp

import android.app.Activity
import android.util.Log
import android.view.View
import com.google.android.material.snackbar.Snackbar
import java.lang.ref.WeakReference

/**
 * Application-level notification manager for showing global Snackbars.
 * This allows any part of the app to show notifications on the current activity,
 * even from background threads or after fragment navigation.
 */
object NotificationManager {

    private const val TAG = "~#NotificationManager"
    private var currentActivity: WeakReference<Activity>? = null

    /**
     * Register the current activity for displaying notifications.
     * Should be called in onResume() of each activity.
     */
    fun setCurrentActivity(activity: Activity) {
        currentActivity = WeakReference(activity)
        Log.v(TAG, "Current activity set: ${activity.javaClass.simpleName}")
    }

    /**
     * Clear the current activity reference.
     * Should be called in onPause() of each activity.
     */
    fun clearCurrentActivity() {
        currentActivity = null
        Log.v(TAG, "Current activity cleared")
    }

    /**
     * Show a Snackbar on the current activity.
     * This method is thread-safe and can be called from any thread.
     *
     * @param message The message to display
     * @param duration Snackbar duration (default: LENGTH_LONG)
     */
    fun showSnackbar(message: String, duration: Int = Snackbar.LENGTH_LONG) {
        val activity = currentActivity?.get()
        if (activity != null) {
            activity.runOnUiThread {
                try {
                    val rootView = activity.findViewById<View>(android.R.id.content)
                    if (rootView != null) {
                        Log.v(TAG, "Showing Snackbar: $message")
                        Snackbar.make(rootView, message, duration).show()
                    } else {
                        Log.w(TAG, "Root view not found, cannot show Snackbar: $message")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error showing Snackbar: $message", e)
                }
            }
        } else {
            Log.w(TAG, "No current activity available, cannot show Snackbar: $message")
        }
    }

    /**
     * Show a success Snackbar with consistent styling.
     */
    fun showSuccess(message: String) {
        showSnackbar("✅ $message")
    }

    /**
     * Show an error Snackbar with consistent styling.
     */
    fun showError(message: String) {
        showSnackbar("❌ $message")
    }

    /**
     * Show an info Snackbar with consistent styling.
     */
    fun showInfo(message: String) {
        showSnackbar("ℹ️ $message")
    }
}
