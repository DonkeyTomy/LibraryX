package com.tomy.compose.components.monitor

/**@author Tomy
 * Created by Tomy on 2023/2/27.
 */
data class ActionMsg(
    val msg: String,
    val needSpeak: Boolean = true,
    val needShow: Boolean = true
)

data class ActionMsgResId(
    val msgResId: Int,
    val needSpeak: Boolean = true,
    val needShow: Boolean = true
)
