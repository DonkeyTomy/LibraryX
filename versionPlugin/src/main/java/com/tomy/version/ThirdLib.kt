package com.tomy.version

/**@author Tomy
 * Created by Tomy on 2022/2/22.
 * 第三方库
 */

object Versions {
    const val gradlePlugin  = "7.1.1"

    const val anko  = "0.10.8"
    const val eventbus = "3.1.1"
    const val excel = "2.6.12"

    const val glide = "4.11.0"
    const val glide_transform   = "4.3.0"
    const val glide_gpu = "2.1.0"

    const val gson          = "2.8.6"
    const val ktLint        = "0.43.0"
    const val loadView      = "2.1.3"
    const val litePal       = "3.2.2"
    const val permission    = "2.0.3"
    const val swipeRecycler = "1.3.2"
    const val timber        = "4.7.1"
}

object ThirdLib {
    const val eventBus  = "org.greenrobot:eventbus:${Versions.eventbus}"
    const val excel     = "net.sourceforge.jexcelapi:jxl:${Versions.excel}"
    const val gson      = "com.google.code.gson:gson:${Versions.gson}"
    const val litePal   = "org.litepal.guolindev:core:${Versions.litePal}"
    const val loadView   = "com.wang.avi:library:${Versions.loadView}"
    const val permission= "com.yanzhenjie:permission:${Versions.permission}"
    const val swipeRecycler = "com.yanzhenjie.recyclerview:x:${Versions.swipeRecycler}"
    const val timber    = "com.jakewharton.timber:timber:${Versions.timber}"

    object Anko {
        const val sqlite    = "org.jetbrains.anko:anko-sqlite:${Versions.anko}"
    }
    
    object ButterKnife {
        private const val version = "10.2.3"
        const val runtime   = "com.jakewharton:butterknife:$version"
        const val compiler  = "com.jakewharton:butterknife-compiler:$version"
    }

    object Bugly {
        private const val runtimeVersion = "3.3.9"
        private const val nativeVersion = "3.9.0"
        const val runtime = "com.tencent.bugly:crashreport:$runtimeVersion"
        const val native = "com.tencent.bugly:nativecrashreport:$nativeVersion"
    }

    object DateTimePiker {
        private const val version = "0.3.1"
    }

    object Glide {
        private const val version = Versions.glide
        const val runtime   = "com.github.bumptech.glide:glide:$version"
        const val compiler  = "com.github.bumptech.glide:compiler:$version"
        const val transform = "jp.wasabeef:glide-transformations:${Versions.glide_transform}"
        const val gpu = "jp.co.cyberagent.android:gpuimage:${Versions.glide_gpu}"
    }

    object LoadingView {
        private const val version = "2.1.3"
    }

    object Okhttp {
        private const val version = "4.3.0"
        const val core  = "com.squareup.okhttp3:okhttp:$version"
        const val log   = "com.squareup.okhttp3:logging-interceptor:$version"
    }

    object Retrofit {
        private const val version = "2.9.0"
    }

    object RxJava {
        private const val java = "3.0.6"
        private const val android   = "3.0.0"
        const val java3 = "io.reactivex.rxjava3:rxjava:$java"
        const val android3 = "io.reactivex.rxjava3:rxandroid:$android"
    }

    object SmartRefresh {
        private const val version = "2.0.3"
        //核心库.必须依赖
        const val kernel    = "com.scwang.smart:refresh-layout-kernel:$version"
        //可选 - 经典刷新头
        const val headerClassics    = "com.scwang.smart:refresh-header-classics:$version"
        //可选 - 雷达刷新头
        const val headerRadar    = "com.scwang.smart:refresh-header-radar:$version"
        //可选 - 虚拟刷新头
        const val headerFalsify    = "com.scwang.smart:refresh-header-falsify:$version"
        //可选 - 谷歌刷新头
        const val headerMaterial    = "com.scwang.smart:refresh-header-material:$version"
        //可选 - 耳机刷新头
        const val headerTwoLevel    = "com.scwang.smart:refresh-header-two-level:$version"
        //可选 - 球脉冲加载
        const val footerBall    = "com.scwang.smart:refresh-footer-ball:$version"
        //可选 - 经典加载
        const val footerClassics    = "com.scwang.smart:refresh-footer-classics:$version"
    }

    object SmartShow {
        //https://github.com/zhiqiang-series/smart-show
        private const val version = "3.1.9"
        const val all   = "com.github.the-pig-of-jungle.smart-show:all:$version"
        const val toast = "com.github.the-pig-of-jungle.smart-show:toast:$version"
        const val dialog    = "com.github.the-pig-of-jungle.smart-show:dialog:$version"
        const val topbar    = "com.github.the-pig-of-jungle.smart-show:topbar:$version"
        //snackBar需要material库
        const val snackbar  = "com.github.the-pig-of-jungle.smart-show:snackbar:$version"
    }

}