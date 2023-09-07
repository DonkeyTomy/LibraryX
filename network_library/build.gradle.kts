import com.tomy.version.BuildConfig
import com.tomy.version.AndroidX
import com.tomy.version.PrimaryLib
import com.tomy.version.ThirdLib

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdk = BuildConfig.compileSdkVersion

    defaultConfig {
        namespace = "com.zzx.network"
        minSdk = BuildConfig.minSdkVersion
        targetSdk = BuildConfig.targetSdkVersion

        testInstrumentationRunner = BuildConfig.testInstrumentationRunner

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions{
        jvmTarget = "1.8"
    }

}

dependencies {
    api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(AndroidX.annotation)

    implementation(ThirdLib.RxJava.java3)
    implementation(ThirdLib.RxJava.android3)
    implementation(ThirdLib.timber)

    implementation(PrimaryLib.Coroutines.core)
    implementation(PrimaryLib.Coroutines.android)

    implementation(ThirdLib.Retrofit.runtime)
    implementation(ThirdLib.Retrofit.gson)
    implementation(ThirdLib.Retrofit.adapter3)
    implementation(ThirdLib.Okhttp.core)
    implementation(ThirdLib.Okhttp.log)

//    implementation 'commons-net:commons-net:20030805.205232'
}
