package com.tomy.lib.ui.view.dialog

import android.text.method.ScrollingMovementMethod
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.tomy.lib.ui.R
import com.tomy.lib.ui.databinding.MessageNotificationBinding
import com.tomy.lib.ui.fragment.BaseDialogFragment
import com.zzx.utils.TTSToast
import timber.log.Timber

/**@author Tomy
 * Created by Tomy on 3/1/2021.
 */
class NotificationDialog(var mContext: FragmentActivity? = null): BaseDialogFragment<MessageNotificationBinding>() {

    var backgroundColor = R.color.black

    var backgroundId: Int? = null

    var msgColor    = R.color.white

    init {
        dialogHeightPercent = 0.8f
        dialogWidthPercent =0.9f
    }

    private var mMsg: String = ""

    private val mStringBuilder = StringBuffer()

    @Deprecated("调用此方法的时候当前Dialog未创建,还未获得Context,因此调用getString()会报错崩溃",
        ReplaceWith("showMsg(msg, fragmentManager, autoDismiss, needTTS)")
    )
    fun showMsg(msgId: Int, fragmentManager: FragmentManager, autoDismiss: Boolean = true, needTTS: Boolean = false) {
//        showMsg(getString(msgId), fragmentManager, autoDismiss, needTTS)
    }

    fun showMsg(msg: String, fragmentManager: FragmentManager, autoDismiss: Boolean = true, needTTS: Boolean = false) {
        mMsg = msg
        if (isShowing()) {
            mContext?.runOnUiThread {
                mBinding?.tvMessage?.apply {
                    text = mMsg
                }
            }

        } else {
            showDialog(fragmentManager, autoDismiss)
        }
        if (needTTS) {
            TTSToast.speakTTS(msg)
        }
    }

    fun appendMsg(msg: String, fragmentManager: FragmentManager, autoDismiss: Boolean = false) {
        Timber.v("appmsg: $msg")
        if (isShowing()) {
            Timber.v(msg)
            mContext?.runOnUiThread {
                mBinding?.tvMessage?.apply {
                    Timber.v("append: $msg")
                    append("$msg\n")
                }
            }
        } else {
            mStringBuilder.append("$msg\n")
            showDialog(fragmentManager, autoDismiss)
        }
    }

    fun showMsg(msg: String, autoDismiss: Boolean = true, needTTS: Boolean = false) {
        mContext?.apply {
            showMsg(msg, supportFragmentManager, autoDismiss, needTTS)
        }
    }

    override fun bindView() {
        mBinding?.tvMessage?.apply {
            maxLines = Int.MAX_VALUE
            movementMethod = ScrollingMovementMethod.getInstance()
            setBackgroundResource(backgroundId ?:backgroundColor)
            text = if (mMsg.isNotEmpty()) mMsg else mStringBuilder.toString()
        }
    }

    override fun getViewBindingClass(): Class<out MessageNotificationBinding> {
        return MessageNotificationBinding::class.java
    }

}