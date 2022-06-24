package com.tomy.component.activity

import android.app.Application
import timber.log.Timber
import timber.log.Timber.DebugTree

/**@author Tomy
 * Created by Tomy on 2018/11/5.
 */
open class BaseApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        if (Timber.treeCount() <= 0) {
            Timber.plant(DebugTree())
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        Timber.uprootAll()
    }

}