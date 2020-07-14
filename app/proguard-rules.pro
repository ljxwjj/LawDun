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


-dontwarn
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-ignorewarnings                # 抑制警告

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

-keep class com.android.agnetty.core.**{*;}
-keep class com.android.agnetty.future.**{*;}
-keep class com.android.agnetty.utils.**{*;}
-keep class com.yunfa365.lawservice.app.future.**{*;}
-keep class com.yunfa365.lawservice.app.handler.**{*;}

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

#EventBus
-keep class org.greenrobot.eventbus.**{*;}
-keepclassmembers class ** {
    public void onEvent(**);
    void onEvent(**);
}
-keep class com.yunfa365.lawservice.app.pojo.**{*;}
-keep class com.yunfa365.lawservice.app.ui.activity.other.pojo.**{*;}
-keep class com.yunfa365.lawservice.app.ui.activity.schedule.pojo.**{*;}
-keep class com.yunfa365.lawservice.app.dao.table.**{*;}

#okhttp相关jar包
-dontwarn okio.**

#okhttp3.pro
# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform


#百度云推送
#-libraryjars libs/pushservice-6.1.1.21.jar
#-dontwarn com.baidu.**
#-keep class com.baidu.**{*; }


#百度地图
-keep class com.baidu.** {*;}
-keep class vi.com.** {*;}
-dontwarn com.baidu.**

#GSON
#-keepattributes *Annotation*
#-keep class sun.misc.Unsafe { *; }
#-keep class com.idea.fifaalarmclock.entity.***
#-keep class com.google.gson.stream.** { *; }

-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

#Saripaar
-keep class com.yunfa365.lawservice.app.ui.validation.** {*;}
-keep class com.mobsandgeeks.saripaar.** {*;}
-keep @com.mobsandgeeks.saripaar.annotation.ValidateUsing class * {*;}


#AndroidPdfViewer
-keep class com.shockwave.**