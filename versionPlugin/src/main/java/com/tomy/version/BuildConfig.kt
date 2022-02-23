package com.tomy.version

/**@author Tomy
 * Created by Tomy on 2022/2/21.
 */
object BuildConfig {
    const val compileSdkVersion = 30
    const val buildToolsVersion ="30.0.3"
    const val minSdkVersion = 21
    const val targetSdkVersion = 30

    const val testInstrumentationRunner="androidx.test.runner.AndroidJUnitRunner"


    const val gradleVersion = "7.1.1"
    const val kotlinVersion = "1.6.10"
    const val kt_stdlib = "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"

    object Kotlin {
        const val stdlib    ="org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"
        const val reflect   = "org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion"
        const val stdlibJdk7 = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion"
        const val stdlibJdk8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion"
        const val test = "org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion"
        const val plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
    }
}