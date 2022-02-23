package com.tomy.version

/**@author Tomy
 * Created by Tomy on 2022/2/23.
 */
object PrimaryLib {

    object AutoDispose {
        const val version   = "2.0.0"
        const val core  = "com.uber.autodispose2:autodispose:$version"
        const val android   = "com.uber.autodispose2:autodispose-android:$version"
        const val lifecycle = "com.uber.autodispose2:autodispose-lifecycle:$version"
        const val android_lifecycle = "com.uber.autodispose2:autodispose-androidx-lifecycle:$version"
    }

    object Coroutines {
        private const val version = "1.5.2"
        /**
         * 提供了协程中使用的UI线程调度提[Dispatchers.Main]
         */
        const val android   = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
        const val core      = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        /**
         * 实现了RxJava的Flowable跟协程中Flow之间的互转
         * */
        const val reactive  = "org.jetbrains.kotlinx:kotlinx-coroutines-reactive:$version"
        const val rxjava3   = "org.jetbrains.kotlinx:kotlinx-coroutines-rx3:$version"
        const val test      = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
    }

    object Koin {
        const val koin_version = "3.1.5"
        // Koin main features for Android.contains (viewModel, scope, fragment)
        const val android   = "io.insert-koin:koin-android:$koin_version"
        // Jetpack Compose
        const val compose   = "io.insert-koin:koin-androidx-compose:$koin_version"
        // Java Compatibility
        const val compat    = "io.insert-koin:koin-android-compat:$koin_version"
        const val core      = "io.insert-koin:koin-core:$koin_version"
        const val plugin    = ""
        // Navigation Graph
        const val navigation    = "io.insert-koin:koin-androidx-navigation:$koin_version"
        // Jetpack WorkManager
        const val worker    = "io.insert-koin:koin-androidx-workmanager:$koin_version"
    }

    object Kotlin {
        private const val version = "1.6.10"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        const val extensions = "org.jetbrains.kotlin:kotlin-android-extensions:$version"
    }

}