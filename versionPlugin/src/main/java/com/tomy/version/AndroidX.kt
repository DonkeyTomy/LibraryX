package com.tomy.version

/**@author Tomy
 * Created by Tomy on 2022/2/21.
 */
object AndroidX {
    const val Annotation    = "androidx.annotation:annotation:1.3.0"
    const val AppCompat     = "androidx.appcompat:appcompat:1.4.0"
    const val CardView      = "androidx.cardview:cardview:1.0.0"
    const val ConstraintLayout  = "androidx.constraintlayout:constraintlayout:2.1.2"
    const val CoreKtx       = "androidx.core:core-ktx:1.7.0"
    const val Espresso      = "androidx.test.espresso:espresso-core:3.4.0"
    const val Gridlayout    = "androidx.gridlayout:gridlayout:1.0.0"
    const val Junit         = "androidx.test.ext:junit-ktx:1.1.3"
    const val LocalBroadcast    = "androidx.localbroadcastmanager:localbroadcastmanager:1.1.0"
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
        const val materialIcons = "androidx.compose.material:material-icons-core:$version"
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
            const val version = "1.0.0-alpha03"

            const val material3 = "androidx.compose.material3:material3:$version"
        }
    }


    object Emoji2 {
        const val version = "1.0.1"

        const val bundle    = "androidx.emoji2:emoji2-bundled:$version"
        const val core      = "androidx.emoji2:emoji2:$version"
        const val view      = "androidx.emoji2:emoji2-views:$version"
        const val viewHelper = "androidx.emoji2:emoji2-views-helper:$version"
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