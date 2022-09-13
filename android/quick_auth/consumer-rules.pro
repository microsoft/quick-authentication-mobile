# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Program Files\Android\android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

##---------------Begin: proguard configuration for QuickAuth  --------
-keep class com.microsoft.quickauth.signin.callback.** { *; }
-keep class com.microsoft.quickauth.signin.error.** { *; }
-keep class com.microsoft.quickauth.signin.logger.** { *; }
-keep class com.microsoft.quickauth.signin.view.** { *; }
-keep public class com.microsoft.quickauth.signin.AccountInfo { *; }
-keep public class com.microsoft.quickauth.signin.TokenResult { *; }
-keep public class com.microsoft.quickauth.signin.ClientCreatedListener { *; }
-keep public class com.microsoft.quickauth.signin.ISignInClient { *; }
-keep public class com.microsoft.quickauth.signin.MSQASignInClient { *; }
-keep public class com.microsoft.quickauth.signin.MSQASignInOptions { *; }
-keep class com.microsoft.quickauth.signin.MSQASignInOptions$* { *; }