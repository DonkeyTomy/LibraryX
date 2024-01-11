package com.tomy.compose.components.monitor

import kotlinx.coroutines.channels.ProducerScope

/**@author Tomy
 * Created by Tomy on 2024/1/9.
 */
interface IMonitor {

    suspend fun startMonitor(producerScope: ProducerScope<ItemState.StatusOnly>)

    fun stopMonitor()

}