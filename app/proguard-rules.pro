
# Add project specific ProGuard rules here.
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-dontobfuscate
-dontwarn

# To prevent ProGuard from stripping away required classes, add the following lines in the /proguard-project.txt file:
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-dontwarn com.google.android.gms.**
# End of section recommended by Google for Google Play Services

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# Optimization is turned off by default. Dex does not like code run
# through the ProGuard optimize and preverify steps (and performs some
# of these optimizations on its own).
-dontoptimize
-dontpreverify

-keepattributes *Annotation*
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native ;
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keep class **.R$*
-keepclassmembers class **.R$* {
    public static ;
}

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.

# support-v4
-dontwarn android.support.v4.**
-keep class android.support.v4.** { *; }
-keepnames class android.support.v4.** { *; }
-keep interface android.support.v4.** { *; }
-keepnames interface android.support.v4.** { *; }

# support-v7
-dontwarn android.support.v7.**
-keep class android.support.v7.** { *; }
-keep class android.support.v7.internal.** { *; }
-keep interface android.support.v7.internal.** { *; }

# Jackson JSON parsing library rules
-keepattributes *Annotation*,EnclosingMethod,Signature

-keepnames class com.fasterxml.jackson.** {
	*;
}

-dontwarn com.fasterxml.jackson.databind.**

# ORMLite rules - OrmLite uses reflection so we need to keep its member names
-keep class com.j256.**
-keepclassmembers class com.j256.** { *; }
-keep enum com.j256.**
-keepclassmembers enum com.j256.** { *; }
-keep interface com.j256.**
-keepclassmembers interface com.j256.** { *; }

# iBeacons rules
-keep class com.radiusnetworks.**
-keepclassmembers class com.radiusnetworks.** { *; }
-keep enum com.radiusnetworks.**
-keepclassmembers enum com.radiusnetworks.** { *; }
-keep interface com.radiusnetworks.**
-keepclassmembers interface com.radiusnetworks.** { *; }

###
# ExactTarget MobilePush SDK Rules
###

-dontnote javax.xml.**
-dontnote org.w3c.dom.**
-dontnote org.xml.sax.**

#we access the .R$raw.custom sound dynamically, so ignore that warning note
-dontnote com.exacttarget.etpushsdk.ET_GenericReceiver

-keep class com.exacttarget.etpushsdk.** {
	*;
}

-keep class com.exacttarget.etpushsdk.data.** {
	public void set*(***);
  	public *** get*();
}

-assumenosideeffects class android.util.Log {
     public static *** v(...);
}

# adding this in to preserve line numbers so that the stack traces
# can be remapped
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable