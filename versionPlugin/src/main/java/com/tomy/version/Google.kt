package com.tomy.version

/**@author Tomy
 * Created by Tomy on 2022/2/23.
 */
object Google {
    const val ComposeTheme3  = "com.google.android.material:compose-theme-adapter-3:1.0.3"
    const val ComposeTheme  = "com.google.android.material:compose-theme-adapter:1.1.3"
    const val Material3 = "com.google.android.material:material:1.5.0-alpha05"
    const val Material = "com.google.android.material:material:1.4.0"

    object Accompanist {
        /**
         * [Insets],[SystemUiController],[AppCompatThemeAdapter],
         * [ViewPager],[NavigationAnimation],[NavigationMaterial],
         * [SwipeToRefresh]
         */
        const val version = "0.22.0-rc"
        const val version_alphaCompose = "0.24.0-alpha"
        const val insets        = "com.google.accompanist:accompanist-insets:$version"
        const val flowlayout    = "com.google.accompanist:accompanist-flowlayout:$version"
        const val pager         = "com.google.accompanist:accompanist-pager:$version"
        const val permission    = "com.google.accompanist:accompanist-permissions:$version"
        const val ui            = "com.google.accompanist:accompanist-insets-ui:$version"
        const val systemUiController = "com.google.accompanist:accompanist-systemuicontroller:$version"
        const val swipeRefresh  = "com.google.accompanist:accompanist-swiperefresh:$version"
        const val themeAdapter  = "com.google.accompanist:accompanist-appcompat-theme:$version"
    }

    object Hilt {
        private const val version = "2.41"
        const val core   = "com.google.dagger:hilt-android:$version"
        const val compiler   = "com.google.dagger:hilt-compiler:$version"
    }
}