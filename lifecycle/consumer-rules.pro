# Kompost Lifecycle - ProGuard Rules
# These rules ensure that Kompost Lifecycle module works correctly with ProGuard/R8

# Keep all Kompost Lifecycle classes
-keep class com.mustafadakhel.kompost.lifecycle.** { *; }

# Keep all farm classes
-keep class com.mustafadakhel.kompost.lifecycle.activity.** { *; }
-keep class com.mustafadakhel.kompost.lifecycle.fragment.** { *; }
-keep class com.mustafadakhel.kompost.lifecycle.viewModel.** { *; }

# Preserve lifecycle DSL marker
-keep @interface com.mustafadakhel.kompost.lifecycle.KompostLifecycleDsl
-keep @com.mustafadakhel.kompost.lifecycle.KompostLifecycleDsl class * { *; }

# Keep ViewModel-related classes - CRITICAL for ViewModel factory
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
    public <init>(...);
}

# Keep ViewModel factory
-keep class androidx.lifecycle.ViewModelProvider$Factory { *; }
-keep class * implements androidx.lifecycle.ViewModelProvider$Factory {
    public <methods>;
}

# Keep ViewModelsFarm and factory methods
-keep class com.mustafadakhel.kompost.lifecycle.viewModel.ViewModelsFarm { *; }
-keep class com.mustafadakhel.kompost.lifecycle.viewModel.ViewModelsFarm$* { *; }

# Keep lifecycle observer classes
-keep class * implements androidx.lifecycle.DefaultLifecycleObserver {
    public <methods>;
}
-keep class * implements androidx.lifecycle.LifecycleObserver {
    public <methods>;
}

# Keep exception classes
-keep class com.mustafadakhel.kompost.lifecycle.activity.ActivityScopedFarmAlreadyExistsException { *; }
-keep class com.mustafadakhel.kompost.lifecycle.fragment.FragmentScopedFarmAlreadyExistsException { *; }
-keep class com.mustafadakhel.kompost.lifecycle.activity.RootActivitiesFarmAlreadyExistsException { *; }
-keep class com.mustafadakhel.kompost.lifecycle.fragment.RootFragmentsFarmAlreadyExistsException { *; }

# Preserve ComponentActivity and Fragment extension functions
-keepclassmembers class androidx.activity.ComponentActivity {
    public <methods>;
}
-keepclassmembers class androidx.fragment.app.Fragment {
    public <methods>;
}

# Keep all public extension functions (supply, viewModel, etc.)
-keepclassmembers class * {
    public static ** activitySupply(...);
    public static ** lazyActivitySupply(...);
    public static ** fragmentSupply(...);
    public static ** lazyFragmentSupply(...);
    public static ** viewModel(...);
    public static ** lazyViewModel(...);
    public static ** rootActivitiesFarm(...);
    public static ** rootFragmentsFarm(...);
    public static ** viewModelsFarm(...);
}

# Keep SavedStateHandle for ViewModel with saved state
-keep class androidx.lifecycle.SavedStateHandle { *; }
-keep class androidx.lifecycle.createSavedStateHandle { *; }

# Keep CreationExtras for ViewModel creation
-keep class androidx.lifecycle.viewmodel.CreationExtras { *; }
-keep class androidx.lifecycle.ViewModelStore { *; }

# Preserve annotations and signatures
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes SourceFile,LineNumberTable

# Keep Kotlin reflect for KClass operations
-keep class kotlin.reflect.** { *; }
-keepclassmembers class kotlin.reflect.** {
    public <methods>;
}
