import com.tomy.version.BuildConfig
import com.tomy.version.AndroidX
import com.tomy.version.Google
import com.tomy.version.Testing
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = BuildConfig.compileSdkVersion

    defaultConfig {
        minSdk = BuildConfig.minSdkVersion
        targetSdk = BuildConfig.targetSdkVersion

        testInstrumentationRunner = BuildConfig.testInstrumentationRunner
        consumerProguardFiles("consumer-rules.pro")

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
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(AndroidX.coreKtx)
    implementation(AndroidX.appCompat)
    implementation(Google.material3)
    testImplementation(Testing.jUnit)
    androidTestImplementation(AndroidX.Test.Ext.junit)
    androidTestImplementation(AndroidX.Test.espressoCore)
}