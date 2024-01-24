package com.tomy.compose.components.monitor

import android.content.Context
import com.tomy.compose.components.monitor.base.ABroadcastStateMonitor
import timber.log.Timber

/**@author Tomy
 * Created by Tomy on 2024/1/9.
 */
abstract class StateOnlyBroadcastMonitor(context: Context, iconArrayResId: Int = -1): ABroadcastStateMonitor(context) {

    init {
        if (iconArrayResId > 0) {
            setIconList(context.resources.getIntArray(iconArrayResId).toList())
            mIconList.forEach {
                Timber.d("icon: $it")
            }
        }
    }

    lateinit var mIconList: List<Int>

    fun isIconListInit() = ::mIconList.isInitialized

    fun setIconList(iconList: List<Int>) {
        mIconList = iconList
    }

    open fun convertState(originState: Int) = 0

}