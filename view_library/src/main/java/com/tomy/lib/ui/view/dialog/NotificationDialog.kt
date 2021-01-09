package com.tomy.lib.ui.view.dialog

import androidx.fragment.app.FragmentManager
import com.tomy.lib.ui.R
import com.tomy.lib.ui.databinding.MessageNotificationBinding
import com.tomy.lib.ui.fragment.BaseDialogFragment
import com.zzx.utils.TTSToast

/**@author Tomy
 * Created by Tomy on 3/1/2021.
 */
class NotificationDialog: BaseDialogFragment<MessageNotificationBinding>() {

    var backgroundColor = R.color.black

    var backgroundId: Int? = null

    var msgColor    = R.color.white

    private var mMsg: String = ""

    @Deprecated("调用此方法的时候当前Dialog未创建,还未获得Context,因此调用getString()会报错崩溃",
        ReplaceWith("showMsg(msg, fragmentManager, autoDismiss, needTTS)")
    )
    fun showMsg(msgId: Int, fragmentManager: FragmentManager, autoDismiss: Boolean = true, needTTS: Boolean = false) {
//        showMsg(getString(msgId), fragmentManager, autoDismiss, needTTS)
    }

    fun showMsg(msg: String, fragmentManager: FragmentManager, autoDismiss: Boolean = true, needTTS: Boolean = false) {
        mMsg = msg
        showDialog(fragmentManager, autoDismiss)
        if (needTTS) {
            TTSToast.showToast(msg, needTTS, show = false)
        }
    }

    override fun bindView() {
        mBinding?.tvMessage?.apply {
            setBackgroundResource(backgroundId ?:backgroundColor)
            text = mMsg
        }
    }

}