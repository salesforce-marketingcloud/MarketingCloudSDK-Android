/*
 * Copyright (c) 2016, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license.
 * For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.marketingcloud.android.demoapp;

import android.app.Activity;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.support.v4.app.ActivityCompat;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
/**
 * Created by kpoorman on 2/1/16.
 * Largely lifted from @AndroidFu's excellent ETPushHelloWorld.
 * Only affects applications targeting Android API23
 */
public class ActivityPermissionDelegate {
    public interface PermissionRationaleRetryBehavior {
        void requestAgain();
    }

    public interface PermissionCallback {
        void handleGranted();
        void handleRationale(PermissionRationaleRetryBehavior behavior);
        void handleDenied();
    }

    public static class PermissionRequest {
        private final String name;
        private final PermissionCallback callback;
        private final String[] permissions;

        public PermissionRequest(
                @NonNull String name,
                @NonNull PermissionCallback callback,
                @Size(min = 1) String...permissions) {
            this.name = name;
            this.callback = callback;
            this.permissions = permissions;
        }
    }

    private final Activity activity;
    private final PermissionRequest requests[];

    public ActivityPermissionDelegate(
            @NonNull Activity activity,
            @Size(min = 1) PermissionRequest[] requests) {
        this.activity = activity;
        this.requests = requests;
    }

    public void requestPermission(@NonNull String requestName) {
        final PermissionRequest request = mapRequestCodeToRequest(requestName);
        if (checkPermissionsGranted(request)) {
            request.callback.handleGranted();
        } else {
            if (shouldShowRequestPermissionRationale(request)) {
                request.callback.handleRationale(new PermissionRationaleRetryBehavior() {
                    @Override
                    public void requestAgain() {
                        requestPermissions(request);
                    }
                });
            } else {
                requestPermissions(request);
            }
        }
    }

    @CheckResult
    public boolean onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        PermissionRequest request = mapRequestCodeToRequest(requestCode);
        if (request != null) {
            if (verifyPermissions(grantResults)) {
                request.callback.handleGranted();
                return true;
            } else {
                request.callback.handleDenied();
                return true;
            }
        }

        return false;
    }

    private boolean checkPermissionsGranted(PermissionRequest request) {
        for (String permission : request.permissions) {
            if (ActivityCompat.checkSelfPermission(activity, permission) != PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    private boolean shouldShowRequestPermissionRationale(PermissionRequest request) {
        for (String permission : request.permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true;
            }
        }

        return false;
    }

    private void requestPermissions(PermissionRequest request) {
        int requestCode = mapNameToRequestCode(request.name);
        ActivityCompat.requestPermissions(activity, request.permissions, requestCode);
    }

    private int mapNameToRequestCode(String requestName) {
        for (int i = 0; i < requests.length; i++) {
            if (requests[i].name.equals(requestName)) {
                return i;
            }
        }

        throw new IllegalArgumentException(requestName);
    }

    private PermissionRequest mapRequestCodeToRequest(String requestName) {
        for (int i = 0; i < requests.length; i++) {
            if (requests[i].name.equals(requestName)) {
                return requests[i];
            }
        }

        throw new IllegalArgumentException(requestName);
    }

    private PermissionRequest mapRequestCodeToRequest(int requestCode) {
        if (requestCode < 0 || requestCode >= requests.length) {
            return null;
        }

        return requests[requestCode];
    }

    private boolean verifyPermissions(int[] grantResults) {
        if (grantResults.length < 1){
            return false;
        }

        for (int result : grantResults) {
            if (result != PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }
}
