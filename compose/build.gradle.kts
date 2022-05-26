import com.tomy.version.BuildConfig
import com.tomy.version.AndroidX
import com.tomy.version.PrimaryLib
import com.tomy.version.Google
import com.tomy.version.ThirdLib

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdk = BuildConfig.compileSdkVersion

    defaultConfig {
        minSdk = BuildConfig.minSdkVersion
        targetSdk = BuildConfig.targetSdkVersion

        testInstrumentationRunner = BuildConfig.testInstrumentationRunner

    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    composeOptions {
        kotlinCompilerExtensionVersion = AndroidX.Compose.compilerVersion
    }

    kotlinOptions{
        jvmTarget = "11"
    }

    kapt {
        correctErrorTypes = true
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

}

dependencies {

    implementation(AndroidX.annotation)
    // implementation(ThirdLib.RxJava.java3)
    // implementation(ThirdLib.RxJava.android3)

    implementation(Google.material)

    implementation(AndroidX.Compose.constraintLayout)
    implementation(AndroidX.Compose.ui)
    implementation(AndroidX.Compose.foundation)
    implementation(AndroidX.Compose.material)
    implementation(AndroidX.Compose.Material3.material3)
    debugImplementation(AndroidX.Compose.tooling)
    implementation(AndroidX.Compose.viewBinding)
    implementation(AndroidX.Paging.compose)
    implementation(AndroidX.Paging.runtimeKtx)
    implementation(AndroidX.Compose.uiPreview)

    implementation(Google.Accompanist.insets)
    implementation(Google.Accompanist.systemUiController)
    implementation(Google.Accompanist.swipeRefresh)

    implementation(PrimaryLib.AutoDispose.core)
    implementation(PrimaryLib.AutoDispose.android)
    implementation(PrimaryLib.AutoDispose.android_lifecycle)
    implementation(PrimaryLib.AutoDispose.lifecycle)
    implementation(AndroidX.Fragment.ktx)
    implementation(AndroidX.Activity.ktx)
    implementation(AndroidX.Activity.compose)
    implementation(AndroidX.appCompat)
//    implementation(AndroidX.constraintLayout)

    implementation(AndroidX.Navigation.fragment)
    implementation(AndroidX.Navigation.uiKtx)

    implementation(PrimaryLib.Koin.compat)
//    lifecycle
    kapt(AndroidX.Lifecycle.compiler)
    implementation(AndroidX.Lifecycle.viewModel)
    implementation(AndroidX.Lifecycle.viewModelCompose)
    implementation(AndroidX.Lifecycle.livedata)

    implementation(ThirdLib.timber)

    //本地库使用
//     implementation(project(":LibraryX:utils_library"))
    //提交JitPack库时使用
//    implementation(project(":utils_library"))


}

