import com.tomy.version.BuildConfig
import com.tomy.version.AndroidX
import com.tomy.version.PrimaryLib
import com.tomy.version.ThirdLib

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("com.tomy.version") apply false
}

android {
    compileSdk = BuildConfig.compileSdkVersion

    defaultConfig {
        minSdk = BuildConfig.minSdkVersion
        targetSdk = BuildConfig.targetSdkVersion

        testInstrumentationRunner = BuildConfig.testInstrumentationRunner

        ndk {
            abiFilters.add("armeabi-v7a")
            abiFilters.add("arm64-v8a")
        }

    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            consumerProguardFiles("proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions{
        jvmTarget = "1.8"
    }

    useLibrary("org.apache.http.legacy")
    externalNativeBuild {
        cmake {
            path = file("CMakeLists.txt")
        }
    }
}

dependencies {
    implementation(AndroidX.appCompat)

    implementation(AndroidX.annotation)
    implementation(BuildConfig.Kotlin.stdlib)
    implementation(BuildConfig.Kotlin.reflect)
    implementation(ThirdLib.RxJava.java3)
    implementation(ThirdLib.RxJava.android3)

    implementation(PrimaryLib.Coroutines.core)
    implementation(PrimaryLib.Coroutines.android)


    //本地库使用
    // implementation(project(path: ":LibraryX:utils_library"))
    //提交JitPack库时使用
    implementation(project(":utils_library"))
}
