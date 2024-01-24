package com.tomy.compose.components.monitor.base

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

/**@author Tomy
 * Created by Tomy on 2024/1/9.
 */
class AStateMonitor(val mIMonitor: IMonitor): IStateMonitor<ItemState> {

    override suspend fun startMonitor() = callbackFlow {
        mIMonitor.sendInitState(this)
        mIMonitor.startMonitor(this)
        awaitClose {
            mIMonitor.stopMonitor()
        }
    }

}