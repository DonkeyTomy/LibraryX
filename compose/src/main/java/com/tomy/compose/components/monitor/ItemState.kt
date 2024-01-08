package com.tomy.compose.components.monitor

/**@author Tomy
 * Created by Tomy on 2023/2/27.
 */
sealed class ItemState: State {
    open class Start(val actionMsg: ActionMsgResId): ItemState()
    open class Process(val actionMsg: ActionMsg) : ItemState()
    class ProcessResId(resId: Int): Start(ActionMsgResId(resId))
    class Failed(actionMsg: ActionMsg): Process(actionMsg)
    class Finish(resId: Int): Start(ActionMsgResId(resId))
    class Status(statue: Int): ItemState()
}