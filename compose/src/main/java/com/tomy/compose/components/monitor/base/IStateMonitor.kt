package com.tomy.compose.components.monitor.base

import kotlinx.coroutines.flow.Flow

/**@author Tomy
 * Created by Tomy on 2023/2/24.
 */

interface State

interface IStateMonitor<out S: State> {

    suspend fun startMonitor(): Flow<S>

}