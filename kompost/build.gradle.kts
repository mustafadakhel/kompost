@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.dakhel.kompost"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}
dependencies {

    testImplementation("io.mockk:mockk:1.12.0")

    testImplementation("org.robolectric:robolectric:4.6.1")

    testImplementation("junit:junit:4.13.2")

    testImplementation("org.jetbrains.kotlin:kotlin-test:1.8.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.8.0")
}