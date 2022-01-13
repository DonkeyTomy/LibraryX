import com.tomy.buildsrc.AndroidX
import com.tomy.buildsrc.Libs
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = Libs.Build.compile

    defaultConfig {
        minSdk = Libs.Build.min
        targetSdk = Libs.Build.target

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    implementation(AndroidX.CoreKtx)
    implementation(AndroidX.AppCompat)
    implementation(Libs.Material)
    testImplementation(Libs.Junit)
    androidTestImplementation(AndroidX.Test.Ext.junit)
    androidTestImplementation(AndroidX.Test.espressoCore)
}