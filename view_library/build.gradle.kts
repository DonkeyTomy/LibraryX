import com.tomy.version.BuildConfig
import com.tomy.version.AndroidX
import com.tomy.version.PrimaryLib
import com.tomy.version.ThirdLib
import com.tomy.version.Google

plugins {
    id("com.android.library")
    id("com.jakewharton.butterknife")
    id("dagger.hilt.android.plugin")
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

    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions{
        jvmTarget = "1.8"
    }

    kapt {
        correctErrorTypes = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            consumerProguardFiles("proguard-rules.pro")
        }
    }

}

dependencies {

    implementation(AndroidX.annotation)
    implementation(BuildConfig.Kotlin.stdlib)
    implementation(BuildConfig.Kotlin.reflect)
    implementation(ThirdLib.RxJava.java3)
    implementation(ThirdLib.RxJava.android3)
    implementation("org.apache.karaf.http:http:3.0.8")
    implementation(ThirdLib.Glide.runtime)
    kapt(ThirdLib.Glide.compiler)

    implementation(Google.Material)

    implementation(PrimaryLib.AutoDispose.core)
    implementation(PrimaryLib.AutoDispose.android)
    implementation(PrimaryLib.AutoDispose.android_lifecycle)
    implementation(PrimaryLib.AutoDispose.lifecycle)
    implementation(AndroidX.Fragment.ktx)
    implementation(AndroidX.Activity.ktx)
    implementation(AndroidX.appCompat)
    implementation(AndroidX.constraintLayout)
    implementation(AndroidX.preferenceKtx)

    implementation(ThirdLib.ButterKnife.runtime)
    kapt(ThirdLib.ButterKnife.compiler)

//    lifecycle
    kapt(AndroidX.Lifecycle.compiler)
    implementation(AndroidX.Lifecycle.viewModel)
    implementation(AndroidX.Lifecycle.livedata)

    api(ThirdLib.swipeRecycler)
    //Paged
    implementation(AndroidX.Paging.runtimeKtx)
    api(ThirdLib.loadView)
    implementation(ThirdLib.SmartShow.toast)
    api(ThirdLib.SmartRefresh.kernel)
    api(ThirdLib.SmartRefresh.headerMaterial)
    api(ThirdLib.SmartRefresh.footerClassics)

    implementation(Google.Hilt.core)
    kapt(Google.Hilt.compiler)
//    implementation deps.dagger.android
//    kapt deps.dagger.compiler
    //本地库使用
    // implementation(project(path: ":LibraryX:utils_library"))
    //提交JitPack库时使用
    implementation(project(":utils_library"))

    /*androidTestImplementation('com.android.support.test.espresso:espresso-core:3.0.2', {
        exclude group: 'com.android.support', module: 'support-annotations'
    })*/

    compileOnly("com.android.support:design:28.0.0")
}

