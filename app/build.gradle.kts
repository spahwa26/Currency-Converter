plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.hilt.android.plugin)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
    kotlin("kapt")
}

android {
    namespace = "com.app.currencyconverter"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.app.currencyconverter"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            keyAlias = "CurrencyConverter"
            keyPassword = "password"
            storeFile = file("keystore.jks")
            storePassword = "password"
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    externalNativeBuild {
        cmake {
            path = File("CMakeLists.txt")
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
    implementation(libs.hilt.android)
    implementation(libs.hilt.work)
    kapt(libs.hilt.kapt.compiler)
    implementation(libs.androidx.work.runtime.ktx)
    kapt(libs.hilt.compiler)
    implementation(libs.room.db)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)
    implementation(libs.hilt.compose)
    implementation(libs.preferences)
    implementation(libs.retrofit.api)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp3)
    implementation(libs.navigation.compose)
    implementation(libs.kotlin.serialization.json)
    implementation(libs.androidx.core.splashscreen)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}