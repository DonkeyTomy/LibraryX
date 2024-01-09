package com.tomy.compose.components.monitor

import kotlinx.coroutines.flow.callbackFlow

/**@author Tomy
 * Created by Tomy on 2024/1/9.
 */
abstract class AStateMonitor: IStateMonitor<ItemState> {

    override suspend fun startMonitor() = callbackFlow {
        send(ItemState.StatusOnly(0))
    }

}