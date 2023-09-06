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
        namespace = "com.seanproctor.datatable"
        minSdk = BuildConfig.minSdkVersion
        targetSdk = BuildConfig.targetSdkVersion

        testInstrumentationRunner = BuildConfig.testInstrumentationRunner

    }

    buildFeatures {
        dataBinding = false
        viewBinding = false
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    composeOptions {
        kotlinCompilerExtensionVersion = AndroidX.Compose.compilerVersion
    }

    kotlinOptions{
        jvmTarget = "17"
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

    implementation(platform(AndroidX.Compose.bom))

    implementation(Google.material)

    implementation(AndroidX.Compose.foundation)
    implementation(AndroidX.Compose.materialIconsExtended)
    implementation(AndroidX.Compose.runtime)
    implementation(AndroidX.Compose.material)
    implementation(AndroidX.Compose.Material3.material3Version)
    androidTestApi(platform(AndroidX.Compose.bom))

    implementation(ThirdLib.timber)



}

