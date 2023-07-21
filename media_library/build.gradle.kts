import com.tomy.version.BuildConfig
import com.tomy.version.AndroidX
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

    flavorDimensions += listOf("config")

    productFlavors {
        create("nf") {
            dimension = "config"
        }

        create("fg") {
            dimension = "config"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions{
        jvmTarget = "11"
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

    /*implementation(AndroidX.concurrent)
    implementation(AndroidX.CameraX.core)
    implementation(AndroidX.CameraX.camera2)
    implementation(AndroidX.CameraX.extension)
    implementation(AndroidX.CameraX.lifecycle)
    implementation(AndroidX.CameraX.video)
    implementation(AndroidX.CameraX.view)
    implementation(AndroidX.Lifecycle.runtime)
    implementation(PrimaryLib.Coroutines.core)
    implementation(PrimaryLib.Coroutines.android)*/

    implementation(ThirdLib.RxJava.java3)
    implementation(ThirdLib.RxJava.android3)
    implementation(ThirdLib.Bugly.runtime)


    //本地库使用
     implementation(project(":LibraryX:utils_library"))
//    implementation(project(":utils_library"))//提交JitPack库时使用
}
