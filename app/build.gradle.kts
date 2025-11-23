import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("com.google.gms.google-services")
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 36

    // --- Load local.properties ---
    val localProps = Properties()
    localProps.load(project.rootProject.file("local.properties").inputStream())

    // Read RapidAPI key from local.properties
    val rapidApiKey = localProps.getProperty("RAPIDAPI_KEY") ?: ""

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        // --- Expose API key to BuildConfig ---
        buildConfigField("String", "RAPIDAPI_KEY", "\"$rapidApiKey\"")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2025.09.00"))

    // Compose
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material3:material3-window-size-class")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.activity:activity-compose")

    // AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Firebase BOM
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")

    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:21.1.1")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("androidx.datastore:datastore-core:1.1.1")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Lottie
    implementation("com.airbnb.android:lottie-compose:6.0.0")

    // Ktor Client (if you still need)
    implementation("io.ktor:ktor-client-core:2.3.5")
    implementation("io.ktor:ktor-client-okhttp:2.3.5")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.5")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.5")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.2")

    // Location
    implementation("com.google.android.gms:play-services-location:21.2.0")

    // Retrofit + Gson
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")

    // Logging
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Permissions
    implementation("com.google.accompanist:accompanist-permissions:0.31.3-beta")
}
