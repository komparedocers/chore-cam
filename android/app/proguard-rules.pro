# Add project specific ProGuard rules here.

# Keep database classes
-keep class com.choreocam.app.database.** { *; }
-keep class com.choreocam.app.models.** { *; }

# Keep API models
-keep class com.choreocam.app.api.models.** { *; }

# Retrofit
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }

# Gson
-keep class com.google.gson.** { *; }
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# AdMob
-keep public class com.google.android.gms.ads.** { *; }

# Firebase
-keep class com.google.firebase.** { *; }
