package com.tomy.compose.components.monitor

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

/**@author Tomy
 * Created by Tomy on 2024/1/9.
 */
abstract class AStateMonitor(val mIMonitor: IMonitor): IStateMonitor<ItemState> {

    override suspend fun startMonitor() = callbackFlow {
        mIMonitor.startMonitor(this)
        awaitClose {
            mIMonitor.stopMonitor()
        }
    }

}