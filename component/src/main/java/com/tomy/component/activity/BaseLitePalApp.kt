package com.tomy.component.activity

import org.litepal.LitePal

open class BaseLitePalApp: BaseApplication() {

    override fun onCreate() {
        super.onCreate()
        LitePal.initialize(this)
    }
}