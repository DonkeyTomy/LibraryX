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


    var gradleVersion = "7.1.1"
    var kotlinVersion = "1.6.10"
    val kt_stdlib get()= "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"

    object Kotlin {
        var stdlib ="org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"
        val stdlibJdk7 = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion"
        val stdlibJdk8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion"
        val test = "org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion"
        val plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
    }
}