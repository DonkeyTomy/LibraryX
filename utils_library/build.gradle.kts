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
    ndkVersion = "20.0.5594570"

    defaultConfig {
        minSdk = BuildConfig.minSdkVersion
        targetSdk = BuildConfig.targetSdkVersion

        testInstrumentationRunner = BuildConfig.testInstrumentationRunner

        ndk {
            abiFilters.add("armeabi-v7a")
        }

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

    implementation(AndroidX.annotation)
    api(ThirdLib.timber)
    implementation(ThirdLib.RxJava.java3)
    implementation(ThirdLib.RxJava.android3)
//    implementation("org.apache.karaf.http:http:3.0.8")
//    api 'de.greenrobot:eventbus:3.0.0-beta1'
    implementation(ThirdLib.Glide.runtime)
    kapt(ThirdLib.Glide.compiler)
    implementation(Google.Material)


    api(ThirdLib.Bugly.runtime)
    api(ThirdLib.Bugly.native)

    api(ThirdLib.eventBus)
    api(ThirdLib.permission)
//    api deps.permission_x
    api(ThirdLib.SmartShow.toast)
    api(ThirdLib.SmartShow.dialog)
    api(ThirdLib.SmartShow.snackbar)

    api(ThirdLib.litePal)

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
