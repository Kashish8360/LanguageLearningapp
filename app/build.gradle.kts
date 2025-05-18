plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.hilt.android)
}

android {

    namespace = "com.example.languagelearningapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.languagelearningapp"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.lottie)
    implementation(libs.circleimageview)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.okhttp)
    implementation(libs.swiperefreshlayout)
    implementation(libs.material)

    // Hilt dependencies
    implementation(libs.hilt.android)
    annotationProcessor(libs.hilt.compiler)



}