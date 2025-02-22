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

# ExoPlayer
-keep class com.google.android.exoplayer2.** { *; }
-keep class com.google.android.exoplayer2.** { *; }
-keep class com.google.android.exoplayer2.ui.** { *; }
-keep class com.google.android.exoplayer2.source.** { *; }
-keep class com.google.android.exoplayer2.trackselection.** { *; }
-keep class com.google.android.exoplayer2.audio.** { *; }
-keep class com.google.android.exoplayer2.video.** { *; }

# 保留反射使用的类
-keepattributes Signature
-keepattributes *Annotation*

# 保留序列化类
-keep class * implements java.io.Serializable { *; }
-keepclassmembers class * implements java.io.Serializable { static final long serialVersionUID; }
#基线包使用，生成mapping.txt
-printmapping mapping.txt
#生成的mapping.txt在app/build/outputs/mapping/release路径下，移动到/app路径下
#修复后的项目使用，保证混淆结果一致
#-applymapping mapping.txt
#hotfix
-keep class com.taobao.sophix.**{*;}
-keep class com.ta.utdid2.device.**{*;}
#防止inline
-dontoptimize