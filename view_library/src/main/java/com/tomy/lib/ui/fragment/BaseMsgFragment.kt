package com.tomy.lib.ui.fragment

import androidx.viewbinding.ViewBinding
import com.tomy.lib.ui.R
import com.tomy.lib.ui.view.dialog.CustomMsgDialog
import com.tomy.lib.ui.view.dialog.IndicatorType
import com.tomy.lib.ui.view.dialog.Interlude
import com.tomy.lib.ui.view.dialog.NotificationDialog
import com.zzx.utils.ExceptionHandler
import com.zzx.utils.TTSToast
import com.zzx.utils.network.NetworkUtil
import io.reactivex.rxjava3.functions.Consumer

/**@author Tomy
 * Created by Tomy on 11/9/2020.
 */
abstract class BaseMsgFragment<VB: ViewBinding>: BaseFragmentViewBind<VB>(), Consumer<Throwable> {
    protected val mProgressDialog by lazy {
        Interlude().apply {
            dim = 0.0F
            isCancelable = false
            canceledOnTouchOutside = false
            indicatorType = IndicatorType.BallSpinFadeLoaderIndicator
            indicatorColorResource = R.color.green
        }
    }

    protected val mMsgDialog by lazy {
        NotificationDialog(mContext!!).apply {
            dim = 0f
            dimEnabled = false
            dialogHeightPercent = 0.8f
            dialogWidthPercent =0.8f
            autoDismissDelay = 1000L
        }
    }

    protected val mConfirmDialog by lazy {
        CustomMsgDialog()
    }

    fun showProgressDialog(msg: String?, delayAutoDismiss: Long = 0, needFocus: Boolean = true) {
        mProgressDialog.showMsg(parentFragmentManager, msg, delayAutoDismiss, needFocus)
    }

    fun showProgressDialog(msg: Int, value: Any, delayAutoDismiss: Long = 0, needFocus: Boolean = true) {
        showProgressDialog(getString(msg, value), delayAutoDismiss, needFocus)
    }

    fun showProgressDialog(msg: Int, delayAutoDismiss: Long = 0, needFocus: Boolean = true) {
        showProgressDialog(getString(msg), delayAutoDismiss, needFocus)
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

    fun showMsg(msg: Int, needTTS: Boolean = false) {
        showMsg(getString(msg), needTTS)
    }

    fun showMsg(msg: String, needTTS: Boolean = false) {
        mMsgDialog.showMsg(msg, mContext!!.supportFragmentManager)
        if (needTTS) {
            speakTTS(msg)
        }
    }

    fun dismissMsg() {
        mMsgDialog.dismissDialog()
    }

    /**
     *
     * @return Boolean true代表当前没有连接WiFi
     */
    fun checkWifiNotConnect(): Boolean {
        return if (!NetworkUtil.isWifiConnected(mContext!!)) {
            showToast(R.string.no_wifi_connect, type = TTSToast.Type.ERROR)
            true
        } else {
            false
        }
    }

    /**
     * @return Boolean true代表没有网络连接,包括数据及WiFi.
     */
    fun checkNetworkNotConnect(): Boolean {
        return if (!NetworkUtil.isNetworkConnected(mContext!!)) {
            showToast(R.string.no_network_connect, type = TTSToast.Type.ERROR)
            true
        } else {
            false
        }
    }

    override fun accept(t: Throwable?) {
        t?.apply {
            ExceptionHandler.getInstance().saveException2File(this)
        }
        dismissProgressDialog()
        t?.printStackTrace()
    }

}