package com.tomy.lib.ui.fragment

import androidx.viewbinding.ViewBinding
import com.tomy.lib.ui.view.dialog.ConfirmDialog
import com.tomy.lib.ui.view.dialog.Interlude
import com.tomy.lib.ui.view.dialog.NotificationDialog
import com.zzx.utils.ExceptionHandler
import io.reactivex.rxjava3.functions.Consumer

/**@author Tomy
 * Created by Tomy on 11/9/2020.
 */
abstract class BaseMsgFragment<VB: ViewBinding>: BaseFragmentViewBind<VB>(), Consumer<Throwable> {
    protected val mProgressDialog by lazy {
        Interlude().apply {
            dim = 0.1F
            isCancelable = false
            canceledOnTouchOutside = false
        }
    }

    protected val mMsgDialog by lazy {
        NotificationDialog().apply {
            dim = 0f
            dimEnabled = false
            autoDismissDelay = 1000L
        }
    }

    protected val mConfirmDialog by lazy {
        ConfirmDialog()
    }

    fun showProgressDialog(msg: String?, delayAutoDismiss: Long = 0) {
        mProgressDialog.showMsg(mContext!!.supportFragmentManager, msg, delayAutoDismiss)
    }

    fun showProgressDialog(msg: Int, value: Any, delayAutoDismiss: Long = 0) {
        showProgressDialog(getString(msg, value), delayAutoDismiss)
    }

    fun showProgressDialog(msg: Int, delayAutoDismiss: Long = 0) {
        showProgressDialog(getString(msg), delayAutoDismiss)
    }

    fun dismissProgressDialog() {
        mProgressDialog.dismissDialog()
    }

    fun showConfirmDialog() {
        mConfirmDialog.showDialog(mContext!!.supportFragmentManager)
    }

    fun dismissConfirmDialog() {
        mConfirmDialog.dismissDialog()
    }

    override fun accept(t: Throwable?) {
        t?.apply {
            ExceptionHandler.getInstance().saveException2File(this)
        }
        dismissProgressDialog()
        t?.printStackTrace()
    }

}