plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdkVersion(Version.compileSdkVersion)

    defaultConfig {
        applicationId("de.pfaffenrodt.gradientview")
        minSdkVersion(Version.minSdkVersion)
        targetSdkVersion(Version.targetSdkVersion)
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(Dependencies.kotlin)
    implementation(Dependencies.Android.appCompat)
    implementation(project(":library"))
}
