# Kompost Android - ProGuard Rules
# These rules ensure that Kompost Android module works correctly with ProGuard/R8

# Keep all Kompost Android classes
-keep class com.mustafadakhel.kompost.android.** { *; }

# Keep ApplicationFarm and related classes
-keep class com.mustafadakhel.kompost.android.application.ApplicationFarm { *; }
-keep class com.mustafadakhel.kompost.android.application.ApplicationFarmAlreadyExistsException { *; }

# Preserve DSL marker annotations
-keep @interface com.mustafadakhel.kompost.android.application.KompostDsl
-keep @com.mustafadakhel.kompost.android.application.KompostDsl class * { *; }

# Keep extension functions for Application class
-keepclassmembers class android.app.Application {
    public <methods>;
}

# Preserve Android context - required for supplyApplicationContext
-keep class android.content.Context { *; }

# Keep all inline extension functions (supply, produce, etc.)
-keepclassmembers class * {
    public static ** applicationSupply(...);
    public static ** lazyApplicationSupply(...);
    public static ** applicationFarm(...);
    public static ** applicationFarmOrNull(...);
    public static ** createApplicationFarm(...);
}

# Preserve annotations and signatures
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes SourceFile,LineNumberTable
