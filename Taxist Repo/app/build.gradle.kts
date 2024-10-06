import java.util.Properties

plugins {
    id("com.android.application")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    kotlin("android")
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.taxi.taxist"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.taxi.taxist"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "T1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        resourceConfigurations += listOf("en","pl","uk","de","es","fr")

        val keystoreFile = project.rootProject.file("local.properties")
        val properties = Properties()
        properties.load(keystoreFile.inputStream())

        buildConfigField("String", "GOOGLE_MAP_KEY","\"${properties.getProperty("GM_API_KEY")}\"")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        kotlinCompilerExtensionVersion = "1.5.15"
    }
    packaging {
        resources {
            exclude("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
}

dependencies {
    implementation(libs.material)
    implementation(libs.compiler)
    implementation(libs.ui.tooling.preview)
    implementation(libs.activity.compose)
    implementation(libs.constraintlayout.compose)
    implementation(libs.material3.android)
    debugImplementation(libs.ui.tooling)

    implementation(libs.appcompat)
    implementation(libs.google.material)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    
    // splash screen
    implementation(libs.core.splashscreen)

    // Maps SDK for Android
    implementation(libs.play.services.maps)
    implementation(libs.android.maps.utils)
    implementation(libs.google.maps.services)
    implementation(libs.maps.compose)
    implementation(libs.play.services.location)
    implementation(libs.places)

    //retrofit
    implementation (libs.retrofit)
    implementation (libs.converter.gson)


}