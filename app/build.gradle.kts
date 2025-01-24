plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.stemp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.stemp"
        minSdk = 24
        targetSdk = 34
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

    // added to make views more easy to do
    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.ui.text.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // for splash screen
    implementation(libs.core.splashscreen)

    // for local database (with room)
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)

    // to handle persistent work that should still work if user navigates off screen, app exits or restarts
    implementation(libs.work.runtime)

}