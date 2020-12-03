package com.tomy.lib.ui.fragment

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import timber.log.Timber
import java.lang.reflect.ParameterizedType
import java.util.concurrent.TimeUnit

/**@author Tomy
 * Created by Tomy on 12/9/2020.
 */
abstract class BaseDialogFragment<VB: ViewBinding>: DialogFragment() {

    var backgroundResource: Int = android.R.color.transparent
    var cancelCallback: ((dialog: DialogInterface?) -> Unit)? = null
    var dismissCallback: ((dialog: DialogInterface?) -> Unit)? = null
    var positiveCallback: (() -> Unit)? = null
    var negativeCallback: (() -> Unit)? = null
    var dim: Float = 0.3F
    var canceledOnTouchOutside: Boolean = false
    var onKeyListener: DialogInterface.OnKeyListener? = null
    @Volatile
    var showed = false

    protected var mBinding: VB? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.apply {
            window?.let {
                it.requestFeature(Window.FEATURE_NO_TITLE)
                it.setBackgroundDrawableResource(backgroundResource)
                val params = it.attributes
                params.dimAmount = dim
                it.attributes = params
            }
            setOnKeyListener(onKeyListener)
            setCanceledOnTouchOutside(canceledOnTouchOutside)
        }
        mBinding = getViewBinding(inflater, container)
        return mBinding!!.root
    }

    @Suppress("UNCHECKED_CAST")
    private fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB {
        val type    = javaClass.genericSuperclass as ParameterizedType
        val aClass  = type.actualTypeArguments[0] as Class<*>
        val method = aClass.getDeclaredMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
        return method.invoke(null, inflater, container, false) as VB
    }

    override fun onResume() {
        super.onResume()
        bindView()
    }

    abstract fun bindView()

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        Timber.v("onCancel()")
        cancelCallback?.invoke(dialog)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissCallback?.invoke(dialog)
        showed = false
        Timber.v("onDismiss()")
    }

    @SuppressLint("AutoDispose")
    fun dismissDialog() {
        Observable.just(Unit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( {
                    synchronized(this) {
                        Timber.v("dismissDialog. isShowing = ${isShowing()}")
                        if (isShowing()) {
                            showed = false
                            dismiss()
                        }
                    }

                }, {
                    it.printStackTrace()
                })
    }

    fun isShowing(): Boolean = showed || isAdded && dialog?.isShowing == true


    @SuppressLint("AutoDispose")
    fun showDialog(fm: FragmentManager, autoDismiss: Boolean = false) {
        Observable.just(Unit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( {
                    Timber.d("showDialog isShowing = ${isShowing()}")
                    synchronized(this) {
                        if (!isShowing()) {
                            show(fm, this.javaClass.simpleName)
                        }
                    }
                }, {
                    it.printStackTrace()
                })
        if (autoDismiss) {
            Observable.just(Unit)
                    .delay(2, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe ({
                        dismissDialog()
                    }, {
                        it.printStackTrace()
                    })
        }
    }


    override fun show(manager: FragmentManager, tag: String?) {
        try {
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