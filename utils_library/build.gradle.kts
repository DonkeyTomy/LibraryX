import com.tomy.version.BuildConfig
import com.tomy.version.AndroidX
import com.tomy.version.PrimaryLib
import com.tomy.version.ThirdLib
import com.tomy.version.Google

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdk = BuildConfig.compileSdkVersion

    defaultConfig {
        namespace = "com.zzx.utils"
        minSdk = BuildConfig.minSdkVersion
        targetSdk = BuildConfig.targetSdkVersion

        testInstrumentationRunner = BuildConfig.testInstrumentationRunner

        /*ndk {
            abiFilters.add("armeabi-v7a")
        }*/

    }

/*    flavorDimensions += listOf("config")

    productFlavors {
        create("nf") {
            dimension = "config"
            buildConfigField("Boolean", "isFg", "false")
        }

        create("fg") {
            dimension = "config"
            buildConfigField("Boolean", "isFg", "true")
        }
    }*/

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

    kotlinOptions{
        jvmTarget = "11"
    }

    useLibrary("org.apache.http.legacy")
    buildFeatures {
        buildConfig = true
    }
    /*externalNativeBuild {
           cmake {
               path = file("CMakeLists.txt")
           }
       }*/
}

dependencies {

    implementation(AndroidX.annotation)
    implementation(AndroidX.coreKtx)
    api(ThirdLib.timber)
    implementation(ThirdLib.RxJava.java3)
    implementation(ThirdLib.RxJava.android3)
//    implementation("org.apache.karaf.http:http:3.0.8")
//    api 'de.greenrobot:eventbus:3.0.0-beta1'
    implementation(ThirdLib.Glide.runtime)
    kapt(ThirdLib.Glide.compiler)
    implementation(Google.material)

    implementation(PrimaryLib.Coroutines.android)
    implementation(PrimaryLib.Coroutines.core)


    implementation(ThirdLib.Bugly.runtime)
    implementation(ThirdLib.Bugly.native)

    implementation(ThirdLib.eventBus)
    api(ThirdLib.permission)
//    api deps.permission_x
//    implementation(ThirdLib.SmartShow.toast)
//    implementation(ThirdLib.SmartShow.dialog)
//    implementation(ThirdLib.SmartShow.snackbar)
//    implementation(project(":smart-toast"))
//    implementation(project(":smart-dialog"))
//    implementation(project(":smart-snackbar"))
    implementation(project(":LibraryX:smart-toast"))
    implementation(project(":LibraryX:smart-dialog"))
    implementation(project(":LibraryX:smart-snackbar"))
    implementation(ThirdLib.litePal)

    implementation(ThirdLib.gson)
    implementation(ThirdLib.excel)

    implementation(PrimaryLib.AutoDispose.core)
    implementation(PrimaryLib.AutoDispose.android)
    implementation(PrimaryLib.AutoDispose.android_lifecycle)
    implementation(PrimaryLib.AutoDispose.lifecycle)
}

/*uploadArchives{
    repositories.mavenDeployer{
        // 本地仓库路径D:\WorkSpace
//        repository(url: uri('../repository'))
        repository(url:"file://F://WorkSpace/myaar/repository/")
        // 唯一标识
        pom.groupId = "com.zzx.utils"
        // 项目名称
        pom.artifactId = "utils"
        // 版本号
        pom.version = "1.0.0"
    }
}*/
