# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
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
#

-keep,allowoptimization,allowobfuscation class retrofit2.Response
-keep class com.google.maps.** { *; }
-keep class com.taxi.taxist.network.** { *; }

# Missing rules
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE

-dontwarn com.google.appengine.api.urlfetch.FetchOptions$Builder
-dontwarn com.google.appengine.api.urlfetch.FetchOptions
-dontwarn com.google.appengine.api.urlfetch.HTTPHeader
-dontwarn com.google.appengine.api.urlfetch.HTTPMethod
-dontwarn com.google.appengine.api.urlfetch.HTTPRequest
-dontwarn com.google.appengine.api.urlfetch.HTTPResponse
-dontwarn com.google.appengine.api.urlfetch.URLFetchService
-dontwarn com.google.appengine.api.urlfetch.URLFetchServiceFactory