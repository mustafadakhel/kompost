# Kompost Core - ProGuard Rules
# These rules ensure that Kompost works correctly with ProGuard/R8 obfuscation

# Preserve all Kompost core classes and members
-keep class com.mustafadakhel.kompost.core.** { *; }

# Preserve annotations - required for DSL markers and reified generics
-keepattributes *Annotation*

# Preserve generic signatures - required for type-safe keys and reified generics
-keepattributes Signature

# Preserve inner classes - required for SeedBed and Crop
-keepattributes InnerClasses

# Preserve source file names and line numbers for better stack traces
-keepattributes SourceFile,LineNumberTable

# Keep Producer interface and all implementations
-keep interface com.mustafadakhel.kompost.core.Producer { *; }
-keep class * implements com.mustafadakhel.kompost.core.Producer { *; }

# Keep DSL marker annotations
-keep @interface com.mustafadakhel.kompost.core.KompostDsl

# Keep all classes annotated with @KompostDsl
-keep @com.mustafadakhel.kompost.core.KompostDsl class * { *; }
-keepclassmembers class * {
    @com.mustafadakhel.kompost.core.KompostDsl *;
}

# Keep exception classes for proper error reporting
-keep class com.mustafadakhel.kompost.core.NoSuchSeedException { *; }
-keep class com.mustafadakhel.kompost.core.CannotCastHarvestedSeedException { *; }
-keep class com.mustafadakhel.kompost.core.DuplicateProduceException { *; }

# Preserve value class (ProduceKey)
-keep @kotlin.jvm.JvmInline class * { *; }

# Keep synthetic methods for inline functions
-keepclassmembers class * {
    synthetic <methods>;
}

# Keep companion objects
-keepclassmembers class * {
    public static ** Companion;
}

# Preserve Kotlin metadata for reflection and reified generics
-keep class kotlin.Metadata { *; }
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

# Keep all reified type parameters
-keepclassmembers class * {
    private static synthetic <methods>;
}
