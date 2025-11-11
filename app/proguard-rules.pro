# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Gson specific classes
# Preserve generic signatures - required for TypeToken to work with R8/ProGuard
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# Preserve Parcelable implementations - required for Google Play Services Location
-keepattributes *Parcelable*

# Prevent R8 from leaving Data object members always
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# For using GSON @Expose annotation
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.Expose <fields>;
}

# Keep TypeToken and its subclasses for Gson - this is critical for TypeToken to work
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken { *; }

# Keep all anonymous TypeToken classes in CustomEventTracking (including companion object)
-keep class com.salesforce.marketingcloud.learningapp.screens.CustomEventTracking$Companion$* extends com.google.gson.reflect.TypeToken { *; }
-keep class com.salesforce.marketingcloud.learningapp.screens.CustomEventTracking$*$* extends com.google.gson.reflect.TypeToken { *; }
-keep class com.salesforce.marketingcloud.learningapp.screens.CustomEventTracking$* extends com.google.gson.reflect.TypeToken { *; }

# Keep the StoredCustomEvent class for Gson serialization
-keep class com.salesforce.marketingcloud.learningapp.screens.CustomEventTracking$StoredCustomEvent { *; }
-keepclassmembers class com.salesforce.marketingcloud.learningapp.screens.CustomEventTracking$StoredCustomEvent { *; }

# Google Play Services Location - required for LocationReceiver
# Keep Parcelable classes used in Intents
-keep class com.google.android.gms.location.** { *; }
-keep interface com.google.android.gms.location.** { *; }

# Preserve Parcelable implementations
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# Keep LocationAvailability and LocationResult classes specifically
-keep class com.google.android.gms.location.LocationAvailability { *; }
-keep class com.google.android.gms.location.LocationResult { *; }
-keep class com.google.android.gms.location.LocationRequest { *; }
-keep class com.google.android.gms.location.Geofence { *; }
-keep class com.google.android.gms.location.GeofencingRequest { *; }

# Other google
-keep class com.google.auth.**