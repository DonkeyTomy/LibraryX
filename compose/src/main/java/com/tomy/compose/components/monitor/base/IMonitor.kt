package com.tomy.compose.components.monitor.base

import kotlinx.coroutines.channels.ProducerScope

/**@author Tomy
 * Created by Tomy on 2024/1/9.
 */
interface IMonitor {

    suspend fun startMonitor(producerScope: ProducerScope<ItemState>)

    suspend fun sendInitState(producerScope: ProducerScope<ItemState>)

    fun stopMonitor()

}