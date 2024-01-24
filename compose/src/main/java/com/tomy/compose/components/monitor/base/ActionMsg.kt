package com.tomy.compose.components.monitor.base

/**@author Tomy
 * Created by Tomy on 2023/2/27.
 */
data class ActionMsg(
    val msg: String = "",
    val needSpeak: Boolean = false,
    val needShow: Boolean = true
)

data class ActionMsgResId(
    val msgResId: Int = -1,
    val needSpeak: Boolean = false,
    val needShow: Boolean = true
)
