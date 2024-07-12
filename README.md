# Kompost: Gardening-Inspired Scoping and DI 🌱
Welcome to Kompost, the scoping and dependency injection library named while I was gardening. Have fun figuring out what everything means. This isn't a serious project, just something from my free time to make DI a bit more amusing.

## Features

- **Application Scope**: Dependencies available to all other scopes within the application.
- **Activity Scope**: Dependencies tied to the lifecycle of an activity.
- **Fragment Scope**: Dependencies tied to the lifecycle of a fragment.
- **ViewModel Scope**: Manage ViewModels with or without `SavedStateHandle`.
- **Custom Scopes**: Flexible scopes to meet specific application needs.

## Quick Start

Planting your first seed is easy! Just follow the steps below and watch your dependencies sprout.

### Step 1: Add Kompost to Your Project

```kotlin
dependencies {
    // Core
    implementation("com.mustafadakhel.kompost:kompost-core:1.1.0")

    // Android application support 
    implementation("com.mustafadakhel.kompost:kompost-android:1.1.0")

    // Lifecycle support
    implementation("com.mustafadakhel.kompost:kompost-lifecycle:1.1.0")
}
```

### Step 2: Create Your Application Farm

```kotlin
class KompostSampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        kompostSampleApplicationFarm()
    }
}

private fun Application.kompostSampleApplicationFarm() = createApplicationFarm {
    // Application-wide dependencies
    produce { DependencyAvailableApplicationWide() }
}
```

### Step 3: Set Up Activity and Fragment Farms

```kotlin
fun ApplicationFarm.activities() = createActivitiesFarm {
    // Activities dependencies
    produce { ActivityRepository(supply()) }
}

fun ComponentActivity.setupFragmentScopedFarm() = getOrCreateActivityScopedFarm {
    produceFragmentScopedFarm {
        // Fragment-scoped dependencies
        produce { FragmentRepository(supply()) }
    }
}
```

## Scopes of Dependency Injection

Kompost supports various scopes to ensure your dependencies grow where they’re needed.

### Application Scope

Dependencies available to all other scopes within the application.

```kotlin
private fun Application.kompostSampleApplicationFarm() = createApplicationFarm {
    singleton(dependency = Database())
}
```

### Activity Scope

Dependencies tied to the lifecycle of an activity.

```kotlin
fun ApplicationFarm.activities() = createActivitiesFarm {
    produce { ActivityRepository(supply()) }
}
```

### Fragment Scope

Dependencies tied to the lifecycle of a fragment.

```kotlin
fun ComponentActivity.setupFragmentScopedFarm() = getOrCreateActivityScopedFarm {
    produceFragmentScopedFarm {
        produce { FragmentRepository(supply()) }
    }
}
```

### ViewModel Scope

ViewModels with and without `SavedStateHandle`.

```kotlin
fun RootActivitiesFarm.viewModels() = createViewModelsFarm {
    produce { MainViewModel(supply()) }
    produceViewModelWithSavedState { savedStateHandle ->
        MainViewModelWithSavedStateHandle(supply(), savedStateHandle)
    }
}
```

### Custom Scopes

Kompost allows you to create custom scopes tailored to your application's specific needs.

```kotlin
class CustomScopeFarm : Producer {
    // Custom scope implementation
}

fun Application.createCustomScopeFarm() {
    val customScopeFarm = CustomScopeFarm()
    // Custom scope setup
}
```

## Scope Hierarchy

Here’s a text diagram to show you how all these scopes stack up:

```
Application Scope
└── Activity Scope
    ├── Fragment Scope
    └── ViewModel Scope (Activity)
         ├── ViewModel without SavedStateHandle
         └── ViewModel with SavedStateHandle
```

## Contributing

This project is done in free time and isn’t meant to be taken too seriously. However, if you find it useful and have ideas to improve it, feel free to fork, contribute, or open a pull request.

## License

Apache License.

Happy planting! 🌻

