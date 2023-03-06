package com.zzx.utils.network

import kotlinx.coroutines.flow.Flow

/**@author Tomy
 * Created by Tomy on 2023/3/6.
 */
interface IConnectivityObserver {

    fun observer(): Flow<Status>

    enum class Status {
        Available,
        Unavailable,
        Connecting,
        Connected,
        Losing,
        Lost
    }

}