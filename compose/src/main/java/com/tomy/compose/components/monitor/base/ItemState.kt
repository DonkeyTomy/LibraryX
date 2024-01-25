package com.tomy.compose.components.monitor.base

/**@author Tomy
 * Created by Tomy on 2023/2/27.
 */
sealed class ItemState: State {
    open class Start(val actionMsg: ActionMsgResId = ActionMsgResId()): ItemState()

    open class Process(val actionMsg: ActionMsg = ActionMsg()) : ItemState()
    class ProcessResId(val actionMsgResId: ActionMsgResId = ActionMsgResId()): ItemState()

    class Failed(actionMsg: ActionMsg): ItemState()
    class FailedResId(val actionMsgResId: ActionMsgResId = ActionMsgResId()): ItemState()

    class Finish(val actionMsgResId: ActionMsgResId = ActionMsgResId()): ItemState()

    class Status(val state: ActionMsgResId = ActionMsgResId()): ItemState()

    class StatusOnly(val state: Int): ItemState()
    class StatusLong(val state: Long): ItemState()
}