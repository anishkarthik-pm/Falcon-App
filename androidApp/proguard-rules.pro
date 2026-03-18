# KPN Falcon ProGuard rules
-keep class com.kpn.falcon.** { *; }
-keep class kotlinx.serialization.** { *; }
-keepattributes *Annotation*
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}
