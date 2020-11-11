package com.tomy.lib.ui.view.dialog

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.tomy.lib.ui.R
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.android.synthetic.main.loading_indicator.*
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
    var backgroundResource: Int = android.R.color.transparent
    var indicatorColorResource: Int = R.color.defaultIndicatorColor
    var cancelCallback: ((dialog: DialogInterface?) -> Unit)? = null
    var dismissCallback: ((dialog: DialogInterface?) -> Unit)? = null
    var dim: Float = 0.3F
    var canceledOnTouchOutside: Boolean = true
    var message: String? = null
    var onKeyListener: DialogInterface.OnKeyListener? = null

    private var mDelayDisposable: Disposable? = null

    @Volatile
    private var showed = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.apply {
            window?.requestFeature(Window.FEATURE_NO_TITLE)
            window?.setBackgroundDrawableResource(backgroundResource)
            setCanceledOnTouchOutside(canceledOnTouchOutside)
            setOnKeyListener(onKeyListener)
        }

//        val view = inflater.inflate(R.layout.loading_indicator, container, false)

        return inflater.inflate(R.layout.loading_indicator, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar.apply {
            setIndicatorColor(resources.getColor(indicatorColorResource, null))
            setIndicator(customIndicator ?: indicatorType.name)
        }
        message?.apply {
            tv_message.text = message
            tv_message.visibility = View.VISIBLE
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
        if (message != null) {
            tv_message?.text = message
            tv_message?.visibility = View.VISIBLE
        } else {
            tv_message?.visibility = View.GONE
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
                            dismiss()
                        }
                    }

                }, {
                    it.printStackTrace()
                })
    }

    @SuppressLint("AutoDispose")
    fun showMsg(fm: FragmentManager, msg: String?, delayAutoDismiss: Long = 0) {
        mShowTime = SystemClock.elapsedRealtime()
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
                    .delay(delayAutoDismiss, TimeUnit.SECONDS)
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
            ft.commit()
            showed = true
            super.show(manager, tag)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}