/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tomy.buildsrc

object Versions {
    const val gradlePlugin  = "7.1.0-rc01"

    const val anko  = "0.10.8"

    const val glide = "4.11.0"
    const val glide_transform   = "4.3.0"
    const val glide_gpu = "2.1.0"

    const val gson  = "2.8.6"
    const val junit = "4.13.2"
    const val ktLint = "0.43.0"
    const val litePal   = "3.2.2"
    const val permission= ""
    const val timber    = "4.7.1"
}

object Libs {


    const val AndroidGradlePlugin = "com.android.tools.build:gradle:${Versions.gradlePlugin}"
    const val JdkDesugar = "com.android.tools:desugar_jdk_libs:1.1.5"

    const val Gson      = "com.google.code.gson:gson:${Versions.gson}"
    const val LitePal   = "org.litepal.guolindev:core:${Versions.litePal}"
    const val Permission= "com.yanzhenjie:permission:${Versions.permission}"
    const val Timber    = "com.jakewharton.timber:timber:${Versions.timber}"

    const val Junit = "junit:junit:${Versions.junit}"
    const val Material3 = "com.google.android.material:material:1.5.0-alpha05"
    const val Material = "com.google.android.material:material:1.4.0"

    object Accompanist {
        const val version = "0.22.0-rc"
        const val insets = "com.google.accompanist:accompanist-insets:$version"
    }

    object Anko {
        const val sqlite    = "org.jetbrains.anko:anko-sqlite:${Versions.anko}"
    }

    object AutoDispose {
        const val version   = "2.0.0"
        const val core  = "com.uber.autodispose2:autodispose:$version"
        const val android   = "com.uber.autodispose2:autodispose-android:$version"
        const val lifecycle = "com.uber.autodispose2:autodispose-lifecycle:$version"
        const val android_lifecycle = "com.uber.autodispose2:autodispose-androidx-lifecycle:$version"
    }

    object Build {
        const val compile   = 31
        const val min       = 26
        const val target    = 31
    }

    object Bugly {
        const val version = "3.3.9"
        const val runtime = "com.tencent.bugly:crashreport:$version"
    }

    object Glide {
        const val version = Versions.glide
        const val runtime   = "com.github.bumptech.glide:glide:$version"
        const val compiler  = "com.github.bumptech.glide:compiler:$version"
        const val gransform = "jp.wasabeef:glide-transformations:${Versions.glide_transform}"
        const val gpu = "jp.co.cyberagent.android:gpuimage:${Versions.glide_gpu}"
    }

    object Kotlin {
        private const val version = "1.6.10"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        const val extensions = "org.jetbrains.kotlin:kotlin-android-extensions:$version"
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

    /**
     * 第三方库
     */
    object DateTimePiker {
        const val version = "0.3.1"
    }


    object LoadingView {
        const val version = "2.1.3"
    }

    object Okhttp {
        const val version = "4.3.0"
        const val core  = "com.squareup.okhttp3:okhttp:$version"
        const val log   = "com.squareup.okhttp3:logging-interceptor:$version"
    }

    object Retrofit {
        const val version = "2.9.0"
    }

    object SmartRefresh {
        const val version = "2.0.1"
        const val kernel    = ""
    }

    object SmartShow {
        const val version = "3.1.9"
    }

    object SwipeRecycler {
        const val version = "1.3.2"
    }
}

object AndroidX {

    const val Annotation    = "androidx.annotation:annotation:1.3.0"
    const val AppCompat     = "androidx.appcompat:appcompat:1.4.0"
    const val CardView      = "androidx.cardview:cardview:1.0.0"
    const val ConstraintLayout  = "androidx.constraintlayout:constraintlayout:2.1.2"
    const val CoreKtx       = "androidx.core:core-ktx:1.7.0"
    const val Espresso      = "androidx.test.espresso:espresso-core:3.4.0"
    const val Gridlayout    = "androidx.gridlayout:gridlayout:1.0.0"
    const val Junit         = "androidx.test.ext:junit-ktx:1.1.3"
    const val LocalBroadcast    = "androidx.localbroadcastmanager:localbroadcastmanager:1.0.0"
    const val Multidex      = "androidx.multidex:multidex:2.0.1"
    const val Preference    = "androidx.preference:preference:1.1.1"
    const val PreferenceKtx    = "androidx.preference:preference-ktx:1.1.1"
    const val Recyclerview    = "androidx.recyclerview:recyclerview:1.2.1"
    const val RecyclerviewSelection = "androidx.recyclerview:recyclerview-selection:1.1.0"
    const val SplashScreen  = "androidx.core:core-splashscreen:1.0.0-alpha02"
    const val Startup       = "androidx.startup:startup-runtime:1.1.0"
    const val SwipeRefresh = "androidx.swiperefreshlayout:swiperefreshlayout:1.1.0"
    const val ViewPager2    = "androidx.viewpager2:viewpager2:1.0.0"

    object Activity {
        const val version = "1.4.0"
        const val core = "androidx.activity:activity:$version"
        const val ktx = "androidx.activity:activity-ktx:$version"
        const val compose = "androidx.activity:activity-compose:$version"
    }

    object Camera {
        const val version = "1.0.2"
        const val core = "androidx.camera:camera-core:$version"
        const val camera2 = "androidx.camera:camera-camera2:$version"
        const val extension = "androidx.camera:camera-extensions:1.0.0-alpha32"
        const val lifecycle = "androidx.camera:camera-lifecycle:$version"
        const val video     = "androidx.camera:camera-video:1.1.0-alpha12"
        const val view      = "androidx.camera:camera-view:1.0.0-alpha32"
    }

    object Compose {
        const val snapshot = ""
        const val version = "1.1.0-rc01"
        const val compilerVersion = "1.1.0-rc02"

        const val animation     = "androidx.compose.animation:animation:$version"
        const val animationCore = "androidx.compose.animation:animation-core:$version"
        const val constraintLayout  = "androidx.constraintlayout:constraintlayout-compose:1.0.0-rc02"
        const val ui            = "androidx.compose.ui:ui:$version"
        const val foundation    = "androidx.compose.foundation:foundation:$version"
        const val layout        = "androidx.compose.foundation:foundation-layout:$version"
        const val material      = "androidx.compose.material:material:$version"
        const val materialIconsExtended = "androidx.compose.material:material-icons-extended:$version"
        const val runtime       = "androidx.compose.runtime:runtime:$version"
        const val runtimeLivedata = "androidx.compose.runtime:runtime-livedata:$version"
        const val tooling       = "androidx.compose.ui:ui-tooling:$version"
        const val test          = "androidx.compose.ui:ui-test:$version"
        const val uiTest        = "androidx.compose.ui:ui-test-junit4:$version"
        const val uiTestManifest = "androidx.compose.ui:ui-test-manifest:$version"
        const val uiUtil        = "androidx.compose.ui:ui-util:${version}"
        const val viewBinding   = "androidx.compose.ui:ui-viewbinding:$version"

        object Material3 {
            const val snapshot = ""
            const val version = "1.0.0-alpha02"

            const val material3 = "androidx.compose.material3:material3:$version"
        }
    }


    object Emoji2 {
        const val version = "1.0.1"

        const val bundle    = "androidx.emoji2:emoji2-bundled:$version"
        const val core      = "androidx.emoji2:emoji2:$version"
        const val view      = "androidx.emoji2:emoji2-views:$version"
        const val viewHelper = "androidx.emoji2:emoji2-views-helper:1.0.1"
    }

    object Fragment {
        const val version = "1.4.0"
        const val core  = "androidx.fragment:fragment:$version"
        const val ktx   = "androidx.fragment:fragment-ktx:$version"
    }

    object Hilt {
        const val version = "1.0.0"
        const val core = "androidx.hilt:hilt-common:$version"
        const val compiler  = "androidx.hilt:hilt-compiler:$version"
        const val lifecycle = "androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03"
        const val navigation= "androidx.hilt:hilt-navigation:$version"
        const val navigationCompose   = "androidx.hilt:hilt-navigation-compose:1.0.0-rc01"
        const val fragment  = "androidx.hilt:hilt-navigation-fragment:$version"
        const val work  = "androidx.hilt:hilt-work:$version"
    }

    object Lifecycle {
        private const val version = "2.4.0"
        const val compiler      = "androidx.lifecycle:lifecycle-compiler:$version"
        const val extensions    = "androidx.lifecycle:lifecycle-extensions:$version"
        const val livedata      = "androidx.lifecycle:lifecycle-livedata-ktx:$version"
        const val viewModel     = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
        const val viewModelCompose = "androidx.lifecycle:lifecycle-viewmodel-compose:$version"
    }

    object Navigation {
        private const val version = "2.3.5"
        const val compose   = "androidx.navigation:navigation-compose:2.4.0-rc01"
        const val fragment  = "androidx.navigation:navigation-fragment-ktx:$version"
        const val uiKtx     = "androidx.navigation:navigation-ui-ktx:$version"
    }

    object Paging {
        const val version = "3.1.0"
        const val compose   = "androidx.paging:paging-compose:1.0.0-alpha14"
        const val guava     = "androidx.paging:paging-guava:$version"
        const val runtime   = "androidx.paging:paging-runtime:$version"
        const val runtimeKtx   = "androidx.paging:paging-runtime-ktx:$version"
        const val rxjava3   = "androidx.paging:paging-rxjava3:$version"
        //testImplementation
        const val test      = "androidx.paging:paging-common:$version"
    }

    object Room {
        const val version   = "2.4.0"
        const val compiler  = "androidx.room:room-compiler:$version"
        const val coroutines = "androidx.room:room-coroutines:2.1.0-alpha04"
        const val guava     = "androidx.room:room-guava:$version"
        const val ktx       = "androidx.room:room-ktx:$version"
        const val paging    = "androidx.room:room-paging:$version"
        const val runtime   = "androidx.room:room-runtime:$version"
        const val test      = "androidx.room:room-testing:$version"
    }

    object SavedState {
        const val version = "1.1.0"
        const val core  = "androidx.savedstate:savedstate:$version"
        const val ktx   = "androidx.savedstate:savedstate-ktx:$version"
    }

    object Transition {
        const val version = "1.4.1"
        const val core  = "androidx.transition:transition:$version"
        const val ktx   = "androidx.transition:transition-ktx:$version"
    }

    object Work {
        const val version   = "2.7.1"
        const val gcm   = "androidx.work:work-gcm:$version"
        const val multiProcess = "androidx.work:work-multiprocess:2.7.1"
        const val ktx   = "androidx.work:work-runtime-ktx:$version"
        const val rxjava3   = "androidx.work:work-rxjava3:$version"
    }

    object Test {
        private const val version = "1.4.0"
        const val core = "androidx.test:core:$version"
        const val rules = "androidx.test:rules:$version"

        object Ext {
            private const val version = "1.1.3"
            const val junit = "androidx.test.ext:junit-ktx:$version"
        }

        const val espressoCore = "androidx.test.espresso:espresso-core:3.4.0"
    }

}

object Urls {
    const val composeSnapshotRepo = "https://androidx.dev/snapshots/builds/" +
        "${AndroidX.Compose.snapshot}/artifacts/repository/"
    const val composeMaterial3SnapshotRepo = "https://androidx.dev/snapshots/builds/" +
            "${AndroidX.Compose.Material3.snapshot}/artifacts/repository/"
    const val accompanistSnapshotRepo = "https://oss.sonatype.org/content/repositories/snapshots"
}
