package com.tomy.compose.components.monitor

import kotlinx.coroutines.flow.Flow

/**@author Tomy
 * Created by Tomy on 2023/2/24.
 */

interface State

interface StateMachine<out S: ItemState> {

    val state:  S

    suspend fun startTest(): Flow<S>

}