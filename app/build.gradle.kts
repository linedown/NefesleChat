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
        versionName = "0.35"

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

    packaging {
        resources {
            excludes += "META-INF/license.txt"
            excludes += "META-INF/license.md"
            excludes += "META-INF/NOTICE.txt"
            excludes += "META-INF/NOTICE.md"
            excludes += "META-INF/spring/aot.factories"
            excludes += "META-INF/spring.schemas"
            excludes += "META-INF/spring.tooling"
            excludes += "META-INF/spring.handlers"
            excludes += "META-INF/notice.txt"
        }
    }
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
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testAnnotationProcessor(libs.lombok)
    testImplementation(libs.lombok)
    implementation(libs.core.splashscreen)
    implementation(libs.circleimageview)

    implementation(libs.spring.websocket)
    implementation(libs.spring.messaging)
    //implementation(libs.jakarta.websocket.client.api)
    implementation(libs.tyrus.standalone.client){
        exclude(group = "javax.websocket", module = "javax.websocket-api")
    }

    implementation(libs.jackson.databind)
    implementation(libs.jackson.core)
    implementation(libs.jackson.annotations)
    implementation(libs.jsoup)
}