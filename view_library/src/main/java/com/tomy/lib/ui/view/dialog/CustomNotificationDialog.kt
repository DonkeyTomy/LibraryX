package com.tomy.lib.ui.view.dialog

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatDialog
import com.coder.zzq.smartshow.dialog.MessageDialog

/**@author Tomy
 * Created by Tomy on 17/6/2021.
 */
class CustomNotificationDialog: MessageDialog<CustomNotificationDialog>() {

    private var mOnKeyListener: DialogInterface.OnKeyListener? = null

    fun setOnKeyListener(onKeyListener: DialogInterface.OnKeyListener?) {
        mOnKeyListener = onKeyListener
        applyOnKeyListener(null)
    }

    override fun applyBody(dialog: AppCompatDialog?) {
        super.applyBody(dialog)
        applyOnKeyListener(dialog)
    }

    fun applyOnKeyListener(dialog: AppCompatDialog?) {
        dialog?.setOnKeyListener(mOnKeyListener)
        mNestedDialog?.setOnKeyListener(mOnKeyListener)
    }

}