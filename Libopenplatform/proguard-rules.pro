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

-keepattributes Signature

-keep class com.pasc.lib.openplatform.forthird.** {*;}
-keep class com.pasc.lib.openplatform.network.** {*;}
-keep class com.pasc.lib.openplatform.resp.** {*;}
-keep class com.pasc.lib.openplatform.InitJSSDKBehavior {*;}
-keep class com.pasc.lib.openplatform.UserAuthBehavior {*;}
-keep class com.pasc.lib.openplatform.PascOpenPlatform {*;}
-keep class com.pasc.lib.openplatform.OpenPlatformProvider {*;}
-keep class com.pasc.lib.openplatform.CorporateAuthBehavior {*;}
-keep class com.pasc.lib.openplatform.IBizCallback {*;}
-keep class com.pasc.lib.openplatform.CertificationCallback {*;}
-keep class com.pasc.lib.openplatform.DataSecretaryListActivity {*;}
-keep class com.pasc.lib.openplatform.AuthProtocolService {*;}
-keep class com.pasc.lib.openplatform.OpenAuthorizationActivity {*;}
-keep class com.pasc.lib.openplatform.OpenCorporateAuthorizationActivity {*;}





