plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "ru.linedown.nefeslechat"
    compileSdk = 35

    defaultConfig {
        applicationId = "ru.linedown.nefeslechat"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        viewBinding = true
    }
    buildToolsVersion = "35.0.0"
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.legacy.support.v4)
    implementation(libs.annotation)
    implementation(libs.monitor)
    implementation(libs.extension.okhttp)
    implementation(libs.okhttp)
    implementation(libs.okhttp.urlconnection)
    testImplementation(libs.junit)
    testImplementation(libs.monitor)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(libs.rxandroid)
    implementation(libs.rxjava)
    implementation(libs.gson)
    implementation(libs.lombok);
}