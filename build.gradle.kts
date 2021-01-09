// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        //noinspection AndroidGradlePluginVersion
        classpath("com.android.tools.build:gradle:4.2.0-beta03")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Version.kotlin}")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}
