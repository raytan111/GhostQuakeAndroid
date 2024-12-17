plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.hilt)
    kotlin("kapt")
}

android {
    namespace = "ghost.quake"
    compileSdk = 34

    defaultConfig {
        applicationId = "ghost.quake"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    kapt {
        correctErrorTypes = true
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.lifecycle.runtime.compose.android)
    implementation(libs.androidx.hilt.common)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Navigation Compose
    implementation(libs.androidx.navigation.compose)

    // ViewModel con Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Coil para cargar imágenes
    implementation(libs.coil.compose)

    // Acompañante de Material 3
    implementation(libs.androidx.material3.window.size)

    // Corrutinas
    implementation(libs.kotlinx.coroutines.android)
    // OkHTTP
    implementation(libs.okhttp)

    implementation (libs.androidx.material.v154)
    implementation (libs.androidx.animation.v154)
    implementation (libs.material3)

    implementation (libs.androidx.material.icons.extended)

    // Retrofit
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    kapt(libs.androidx.hilt.compiler)

    // Maps
    implementation(libs.google.maps.compose)
    implementation(libs.google.play.services.maps)
    implementation(libs.google.play.services.location)

    // WorkManager
    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)
    kapt(libs.androidx.hilt.compiler)
}