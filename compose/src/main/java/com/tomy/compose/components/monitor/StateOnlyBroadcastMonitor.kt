package com.tomy.compose.components.monitor

import android.content.Context

/**@author Tomy
 * Created by Tomy on 2024/1/9.
 */
abstract class StateOnlyBroadcastMonitor(context: Context): ABroadcastStateMonitor(context) {

    lateinit var mIconList: List<Int>

    fun isIconListInit() = ::mIconList.isInitialized

}