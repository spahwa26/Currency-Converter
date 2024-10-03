plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.hilt.android.plugin)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kapt)
    alias(libs.plugins.compose.compiler)
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
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

    //core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    //compose
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))

    //UI
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    //hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.work)
    kapt(libs.hilt.kapt.compiler)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.compose)


    //Worker
    implementation(libs.androidx.work.runtime.ktx)

    //room
    implementation(libs.room.db)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)

    //prefs
    implementation(libs.preferences)

    //retrofit
    implementation(libs.retrofit.api)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp3)

    //navigation
    implementation(libs.navigation.compose)
    implementation(libs.kotlin.serialization.json)

    //splash
    implementation(libs.androidx.core.splashscreen)

    //testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    testImplementation(libs.androidx.test.core.ktx)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.test.ext)
    testImplementation(libs.androidx.test.rules)
    testImplementation(libs.hilt.android.testing)
    testImplementation(libs.google.truth)
//    testImplementation(libs.mockito)
    testImplementation(libs.mockito.kotlin)
    testImplementation(project(":shared-test-code"))
}