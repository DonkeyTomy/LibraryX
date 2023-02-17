import com.tomy.version.*
import java.text.SimpleDateFormat
import java.util.*

plugins {
    id("com.android.library")
    id("com.jakewharton.butterknife")
    kotlin("android")
//    id("kotlin-android-extensions")
    kotlin("kapt")
}

// 定义打包时间
fun releaseTime(): String {
    val time = SimpleDateFormat("MMdd")
    return "${time.format(Date())}"
}

android {
    compileSdk = BuildConfig.compileSdkVersion

    defaultConfig {
        minSdk = BuildConfig.minSdkVersion
        targetSdk = BuildConfig.targetSdkVersion
//        applicationId = "com.zzx.camera"

        multiDexEnabled = true

        testInstrumentationRunner = BuildConfig.testInstrumentationRunner

        ndk {
            abiFilters += arrayOf("armeabi-v7a")//, "arm64-v8a"
        }

    }

    /*signingConfigs {
        getByName("debug") {
            storeFile       = file("E:\\AndroidEnvironment\\release.keystore")
            storePassword   = "android"
            keyAlias        = "platform"
            keyPassword     = "android"
        }

        create("release") {
            storeFile = file("E:\\AndroidEnvironment\\release.keystore")
            storePassword   = "android"
            keyAlias        = "platform"
            keyPassword     = "android"
        }
    }*/

    lint {
        checkDependencies = true
        baseline = file("lint-baseline.xml")
        abortOnError = false//不在error情况下中断
        disable.add("MissingTranslation")//无视多做了本地化的字符串
        disable.add("ExtraTranslation")//无视字符串缺少本地化的情况
    }

    buildFeatures {
        compose = false
        dataBinding = false
        viewBinding = false

        // Disable unused AGP features
        buildConfig = true
        aidl = true
        renderScript = false
        resValues = false
        shaders = false
    }


    composeOptions {
        kotlinCompilerExtensionVersion = AndroidX.Compose.compilerVersion
    }

    sourceSets {
        getByName("main") {
            jniLibs {
                srcDir("libs")
            }
        }

    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions{
        jvmTarget = "11"
    }

    kapt {
        correctErrorTypes = true
    }

    /*buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.findByName("release")
        }
        debug {
            signingConfig = signingConfigs.findByName("debug")
        }
    }*/

    packagingOptions {
        jniLibs {
            excludes.add("META-INF/licenses/**")
        }
        resources {
            excludes.addAll(arrayOf(
                "META-INF/licenses/**",
                "META-INF/AL2.0",
                "META-INF/LGPL2.1"
            ))
        }
    }

}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
//    implementation(platform(AndroidX.Compose.bom))

    implementation(AndroidX.annotation)
//    implementation(AndroidX.Fragment.ktx)
    implementation(AndroidX.Activity.ktx)
    implementation(AndroidX.appCompat)
    implementation(AndroidX.constraintLayout)
    implementation(Google.material)
    implementation(AndroidX.preferenceKtx)
//    implementation(AndroidX.recyclerView)
    implementation(AndroidX.multidex)
//    implementation(AndroidX.Navigation.fragment)
//    implementation(AndroidX.Navigation.uiKtx)
//    implementation(AndroidX.splashScreen)
//    implementation(AndroidX.documentFile)

    implementation(ThirdLib.RxJava.java3)
    implementation(ThirdLib.RxJava.android3)

    implementation(ThirdLib.Bugly.runtime)

    implementation(AndroidX.coreKtx)
    /**compose**/
    /*implementation(AndroidX.Compose.viewBinding)
    implementation(AndroidX.Activity.compose)
    implementation(AndroidX.Compose.ui)
    implementation(AndroidX.Compose.material)
    implementation(AndroidX.Compose.Material3.material3)
    implementation(AndroidX.Compose.uiPreview)
    implementation(AndroidX.Compose.animation)
    implementation(AndroidX.Compose.animationGraphics)
    implementation(Google.Accompanist.insets)
    implementation(Google.Accompanist.drawable)
    debugImplementation(AndroidX.Compose.tooling)
    implementation(PrimaryLib.Coroutines.android)
    implementation(PrimaryLib.Coroutines.core)
    implementation(PrimaryLib.Coroutines.reactive)
    implementation(PrimaryLib.Coroutines.rxjava3)
    androidTestImplementation(platform(AndroidX.Compose.bom))*/

//    implementation(PrimaryLib.Koin.android)
//    implementation(PrimaryLib.Koin.core)

    implementation(ThirdLib.eventBus)

    implementation(ThirdLib.ButterKnife.runtime)
    kapt(ThirdLib.ButterKnife.compiler)

    implementation(ThirdLib.Glide.runtime)
    kapt(ThirdLib.Glide.compiler)
    implementation(ThirdLib.Dagger.runtime)
    kapt(ThirdLib.Dagger.compiler)

    implementation(project(":LibraryX:utils_library"))
    implementation(project(":LibraryX:view_library"))
    implementation(project(":LibraryX:media_library"))
    implementation(project(":LibraryX:compose"))
    api(project(":RV123:audio_recorder"))

    /*implementation(project(":utils_library"))
    implementation(project(":view_library"))
    implementation(project(":media_library"))
    implementation(project(":compose"))*/

//    implementation(AndroidX.Work.ktx)
//    implementation(AndroidX.Work.rxjava3)

    /*implementation(PrimaryLib.AutoDispose.core)
    implementation(PrimaryLib.AutoDispose.android)
    implementation(PrimaryLib.AutoDispose.android_lifecycle)
    implementation(PrimaryLib.AutoDispose.lifecycle)*/

//    implementation(ThirdLib.litePal)

}
