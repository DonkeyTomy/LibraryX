package com.tomy.lib.ui.view.dialog

import android.graphics.drawable.ColorDrawable
import androidx.fragment.app.FragmentManager
import com.tomy.lib.ui.R
import com.tomy.lib.ui.databinding.MessageNotificationBinding
import com.tomy.lib.ui.fragment.BaseDialogFragment

/**@author Tomy
 * Created by Tomy on 3/1/2021.
 */
class NotificationDialog: BaseDialogFragment<MessageNotificationBinding>() {

    var backgroundColor = R.color.black

    var backgroundId: Int? = null

    var msgColor    = R.color.white

    private var mMsg: String = ""

    fun showMsg(msgId: Int, fragmentManager: FragmentManager, autoDismiss: Boolean = true) {
        showMsg(getString(msgId), fragmentManager, autoDismiss)
    }

    fun showMsg(msg: String, fragmentManager: FragmentManager, autoDismiss: Boolean = true) {
        mMsg = msg
        showDialog(fragmentManager, autoDismiss)
    }

    override fun bindView() {
        mBinding?.tvMessage?.apply {
            setBackgroundResource(backgroundId ?:backgroundColor)
            text = mMsg
        }
    }

}