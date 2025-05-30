plugins {
    id("com.android.application")
}

android {
    namespace = "com.sumonkmr.the_reader"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.sumonkmr.the_reader"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.activity:activity:1.10.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    implementation ("com.android.volley:volley:1.2.1")
    implementation("com.github.fornewid:neumorphism:0.3.2")
    implementation("com.github.barteksc:AndroidPdfViewer:3.1.0-beta.1")
    implementation ("com.airbnb.android:lottie:6.1.0")
    implementation ("com.squareup.picasso:picasso:2.8")
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")


}

