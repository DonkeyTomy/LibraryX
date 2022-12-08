package com.zzx.camera.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.zzx.utils.event.EventBusUtils
import timber.log.Timber

/**@author Tomy
 * Created by Tomy on 2018/7/10.
 */
class MessageReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.e(intent!!.action)
        when (intent.action) {
            ACTION_CAMERA -> processCamera(intent)
//            ACTION_WECHAT_RECV -> processWechat(mContext, intent)
        }
    }

    private fun processPowerDown(accOn: Boolean) {

    }

    private fun processWechat(context: Context?, intent: Intent) {
        val cmd = intent.getStringExtra(EXTRA_WECHAT_CMD)
        Timber.e("WeChat processWechat.cmd = $cmd")
        when (cmd) {
            WECHAT_CMD_RECV_TAKE_PIC -> {
                EventBusUtils.postEvent(ACTION_CAMERA, PICTURE_NEED_RESULT)
                sendAction(context, WECHAT_CMD_SEND_TAKE_PIC)
            }
            WECHAT_CMD_RECV_TAKE_VID -> {
                EventBusUtils.postEvent(ACTION_CAMERA, VIDEO_NEED_RESULT)
                sendAction(context, WECHAT_CMD_SEND_TAKE_VID)
            }
        }
    }

    private fun sendAction(context: Context?, extra: String) {
        val intent = Intent().apply {
            action = ACTION_WECHAT_SEND
            putExtra(EXTRA_WECHAT_CMD, extra)
        }
        context!!.sendBroadcast(intent)
    }

    private fun processCamera(intent: Intent) {
        val intExtra = intent.getIntExtra(ACTION_EXTRA_INT, -1)
        EventBusUtils.postEvent(ACTION_CAMERA, intExtra.toString())
    }

    companion object {
        /**
         * Intent的Action
         * */
        const val ACTION_CAMERA = "ActionCamera"

        const val ACTION_WECHAT_RECV    = "action.com.zdjw.rv138.cn.recv"
        const val ACTION_WECHAT_SEND    = "action.com.zdjw.rv138.cn.send"

        const val EXTRA_WECHAT_CMD  = "extra.com.zdjw.rv138.cn.cmd"

        const val WECHAT_CMD_RECV_TAKE_PIC  = "RECV_TAKE_PHOTO"
        const val WECHAT_CMD_RECV_TAKE_VID  = "RECV_TAKE_VIDEO"

        const val WECHAT_CMD_SEND_TAKE_PIC  = "SEND_TAKE_PHOTO"
        const val WECHAT_CMD_SEND_TAKE_VID  = "SEND_TAKE_VIDEO"

        const val WECHAT_RESULT_TAKE_VID    = "RECV_TAKE_VIDEO_RESULT"
        const val WECHAT_RESULT_TAKE_PIC    = "RECV_TAKE_PHOTO_RESULT"

        const val WECHAT_PIC_PATH  = "TAKE_PHOTO_PATH"
        const val WECHAT_VID_PATH  = "TAKE_VIDEO_PATH"

        /**
         * [Intent.putExtra] ([ACTION_EXTRA_INT], [EXTRA_DISMISS_WIN])
         * */
        const val ACTION_EXTRA_INT      = "zzxExtraInt"
        /**
         * [Intent.putExtra] ([ACTION_EXTRA_BOOLEAN], true/false)
         * */
        const val ACTION_EXTRA_BOOLEAN  = "zzxExtraBool"


        const val EXTRA_DISMISS_WIN     = "0"
        const val EXTRA_SHOW_WIN        = "1"
        const val EXTRA_START_RECORD    = "2"
        const val EXTRA_ONE_SHOT_FINISH = "3"

        /**以下三个可以添加BooleanExtra
         * @see ACTION_EXTRA_BOOLEAN true代表需要广播返回文件路径.微信调用使用.
         * */
        const val EXTRA_STOP_RECORD     = "3"
        const val EXTRA_LOCK_VIDEO      = "4"
        const val EXTRA_TAKE_PICTURE    = "5"

        const val PICTURE_NEED_RESULT   = "6"
        const val VIDEO_NEED_RESULT     = "7"

        const val ACTION_ACC_CHANGED    = "ActionAcc"

        const val ACTION_POWER_DOWN     = "ActionPwrStats"

        const val ACTION_POWER_UP = "ActionAccStats"
        const val STATUS = "status"
    }
}