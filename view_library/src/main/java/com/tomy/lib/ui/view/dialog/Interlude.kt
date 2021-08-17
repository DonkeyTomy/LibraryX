package com.tomy.lib.ui.view.dialog

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.SystemClock
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.tomy.lib.ui.R
import com.tomy.lib.ui.databinding.LoadingIndicatorBinding
import com.zzx.utils.config.ScreenUtil
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import timber.log.Timber
import java.util.concurrent.TimeUnit
import kotlin.math.abs


/**
 * Project : InterludeApp<br>
 * Created by twisty on 2017/6/12.<br>
 *
 */
class Interlude : DialogFragment() {


    private var customIndicator: String? = null
    var indicatorType: IndicatorType = IndicatorType.BallClipRotatePulseIndicator
    var backgroundResource: Int = R.drawable.bg_interlude
    var indicatorColorResource: Int = R.color.defaultIndicatorColor
    var cancelCallback: ((dialog: DialogInterface?) -> Unit)? = null
    var dismissCallback: ((dialog: DialogInterface?) -> Unit)? = null
    var dim: Float = 0.3F
    var canceledOnTouchOutside: Boolean = true
    var message: String? = null
    var onKeyListener: DialogInterface.OnKeyListener? = null

    private var mDelayDisposable: Disposable? = null
    var mNeedFocus = true

    @Volatile
    private var showed = false

    private var mBinding: LoadingIndicatorBinding? = null

    var dialogWidthPercent: Float?  = 0.5f

    var dialogHeightPercent: Float? = 0.3f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.apply {
            window?.let {
                if (!mNeedFocus) {
                    it.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
                }
                it.requestFeature(Window.FEATURE_NO_TITLE)
                it.setBackgroundDrawableResource(backgroundResource)
            }
            setCanceledOnTouchOutside(canceledOnTouchOutside)
            setOnKeyListener(onKeyListener)
        }

//        val view = inflater.inflate(R.layout.loading_indicator, container, false)
        mBinding = LoadingIndicatorBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    fun resizeDialog() {
        dialog?.window?.let { window ->
            window.attributes.apply {
                val screenSize = ScreenUtil.getScreenSize(requireContext())

                height  = if (dialogHeightPercent == null || dialogHeightPercent == 0f) {
                    ViewGroup.LayoutParams.WRAP_CONTENT
                } else {
                    (screenSize.height * dialogHeightPercent!!).toInt()
                }

                width   = if (dialogWidthPercent == null || dialogWidthPercent == 0f) {
                    ViewGroup.LayoutParams.MATCH_PARENT
                } else {
                    (screenSize.height * dialogWidthPercent!!).toInt()
                }

                window.setLayout(width, height)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding!!.progressBar.apply {
            if (VERSION.SDK_INT >= VERSION_CODES.M) {
                setIndicatorColor(resources.getColor(indicatorColorResource, null))
            } else {
                setIndicatorColor(resources.getColor(indicatorColorResource))
            }
            setIndicator(customIndicator ?: indicatorType.name)
        }
        message?.apply {
            mBinding!!.tvMessage.text = message
            mBinding!!.tvMessage.visibility = View.VISIBLE
        }
    }

    /*override fun onResume() {
        super.onResume()
        progressBar.show()
    }

    override fun onPause() {
        super.onPause()
        progressBar.hide()
    }*/

    private fun setMsg(message: String?) {
        this.message = message
        mBinding?.apply {
            if (message != null) {
                tvMessage.text = message
                tvMessage.visibility = View.VISIBLE
            } else {
                tvMessage.visibility = View.GONE
            }
        }
    }


    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        showed = false
        cancelCallback?.invoke(dialog)
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        showed = false
        dismissCallback?.invoke(dialog)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            val windowParams = attributes
            windowParams.dimAmount = dim
            attributes = windowParams
        }
        resizeDialog()
    }

    fun isShowing(): Boolean = showed || isAdded && dialog?.isShowing == true

    @Volatile
    private var mShowTime = 0L

    @SuppressLint("AutoDispose")
    fun dismissDialog() {
        mDelayDisposable?.dispose()
        mDelayDisposable = null
        Observable.just(Unit)
                .delay(if (abs(SystemClock.elapsedRealtime() - mShowTime) >= 400) 0 else 400, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( {
                    synchronized(this) {
                        Timber.v("dismissDialog. isShowing = ${isShowing()} msg = $message")
                        if (isShowing()) {
                            dismissAllowingStateLoss()
                        }
                    }

                }, {
                    it.printStackTrace()
                })
    }

    @SuppressLint("AutoDispose")
    fun showMsg(fm: FragmentManager, msg: String? = null, delayAutoDismiss: Long = 0, needFocus: Boolean = true) {
        mShowTime = SystemClock.elapsedRealtime()
        mNeedFocus = needFocus
        Observable.just(Unit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( {
                    Timber.v("showDialog isShowing = ${isShowing()}; msg = $msg")
                    synchronized(this) {
                        if (isShowing()) {
                            setMsg(msg)
                        } else {
                            message = msg
                            show(fm, this.javaClass.simpleName)
                        }
                    }
                }, {
                    it.printStackTrace()
                })
        if (delayAutoDismiss > 0) {
            mDelayDisposable?.dispose()
            mDelayDisposable = Observable.just(Unit)
                    .delay(delayAutoDismiss, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe( {
                        dismissDialog()
                    }, {
                        it.printStackTrace()
                    })
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            mShowTime = SystemClock.elapsedRealtime()
            val ft = manager.beginTransaction()
            val fragment = manager.findFragmentByTag(tag)
            if (fragment != null) {
                ft.remove(fragment)
            } else if (isAdded) {
                ft.remove(this)
            }
            ft.commitAllowingStateLoss()
            showed = true
            showAllowingStateLoss(manager, tag)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showAllowingStateLoss(manager: FragmentManager, tag: String?) {
        try {
            val dismissed = DialogFragment::class.java.getDeclaredField("mDismissed")
            dismissed.isAccessible = true
            dismissed.set(this, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            val shown = DialogFragment::class.java.getDeclaredField("mShownByMe")
            shown.isAccessible = true
            shown.set(this, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val ft = manager.beginTransaction()
        ft.add(this, tag)
        ft.commitAllowingStateLoss()
    }
}