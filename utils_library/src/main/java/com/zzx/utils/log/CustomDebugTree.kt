package com.zzx.utils.log

import android.util.Log
import timber.log.Timber

/**@author Tomy
 * Created by Tomy on 21/1/2021.
 */
class CustomDebugTree: Timber.DebugTree() {

    private var mMinLogLevel = Log.VERBOSE

    fun setMinLogLevel(logLevel: Int) {
        mMinLogLevel = logLevel
    }

    override fun isLoggable(tag: String?, priority: Int): Boolean {
        return priority >= mMinLogLevel
    }

}