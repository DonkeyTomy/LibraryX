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
        namespace = "com.tomy.compose"
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
    api(platform(AndroidX.Compose.bom))

    implementation(Google.material)

    api(AndroidX.Compose.constraintLayout)
    api(AndroidX.Compose.foundation)
    api(AndroidX.Compose.material)
    api(AndroidX.Compose.runtime)
    api(AndroidX.Compose.runtimeLivedata)
    api(AndroidX.Compose.ui)
    api(AndroidX.Compose.uiUtil)
    api(AndroidX.Compose.Material3.material3)
    implementation(AndroidX.Compose.preferences)
    implementation(AndroidX.DataStore.preference)
    debugApi(AndroidX.Compose.tooling)
    api(AndroidX.Compose.viewBinding)
    implementation(AndroidX.Paging.compose)
    implementation(AndroidX.Paging.runtimeKtx)
    api(AndroidX.Compose.uiPreview)
    androidTestApi(platform(AndroidX.Compose.bom))

//    implementation(Google.Accompanist.insets)
    api(Google.Accompanist.systemUiController)
    api(Google.Accompanist.drawable)
//    implementation(Google.Accompanist.swipeRefresh)

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
    implementation(AndroidX.Navigation.compose)
    implementation(AndroidX.Navigation.uiKtx)
    implementation(AndroidX.Hilt.navigationCompose)

    implementation(PrimaryLib.Koin.compat)
//    lifecycle
    kapt(AndroidX.Lifecycle.compiler)
    implementation(AndroidX.Lifecycle.viewModel)
    implementation(AndroidX.Lifecycle.viewModelCompose)
    implementation(AndroidX.Lifecycle.runtimeCompose)
    implementation(AndroidX.Lifecycle.livedata)

    implementation(ThirdLib.timber)

    //本地库使用
     api(project(":LibraryX:component"))
//     api(project(":component"))
    //提交JitPack库时使用
//    implementation(project(":utils_library"))


}

