package com.tomy.lib.ui.fragment

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.zzx.utils.config.ScreenUtil
import com.zzx.utils.rxjava.RxJava
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
    var dimEnabled: Boolean = true
    var canceledOnTouchOutside: Boolean = false
    var onKeyListener: DialogInterface.OnKeyListener? = null
    @Volatile
    var showed = false

    var autoDismissDelay = 2000L

    protected var mBinding: VB? = null

    protected var mRootView: View? = null

    var dialogWidthPercent: Float?  = 0.8f

    var dialogHeightPercent: Float? = 0.7f

    fun sizePercent(widthPercent: Float?, heightPercent: Float?): BaseDialogFragment<VB> {
        dialogWidthPercent = widthPercent
        dialogHeightPercent = heightPercent
        return this
    }

    fun canceledOnOutside(canceledOnOutSide: Boolean): BaseDialogFragment<VB> {
        canceledOnTouchOutside = canceledOnOutSide
        return this
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.apply {
            window?.let {
                it.requestFeature(Window.FEATURE_NO_TITLE)
                it.setBackgroundDrawableResource(backgroundResource)
                val params = it.attributes
                params.dimAmount = dim
                it.attributes = params
                if (!dimEnabled) {
                    it.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                } else {
                    it.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                }
            }
            setOnKeyListener(onKeyListener)
            setCanceledOnTouchOutside(canceledOnTouchOutside)
        }
        if (mRootView == null) {
            mBinding = getViewBinding(LayoutInflater.from(requireActivity()), container)
            mRootView = mBinding!!.root
        }
        modifyView(mRootView!!)
        return mRootView!!
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mRootView?.apply {
            parent?.let {
                (it as ViewGroup).removeView(this)
            }
            /**
             * 加了会导致[BaseFragmentDataBind]/[BaseFragmentViewBind]里的HeadBind或者BottomBind在onDestroyView()后再重新执行onCreateView()两者不绑定而添加失败
             */
//            mRootView = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mRootView = null
        mBinding = null
    }

    open fun modifyView(view: View) {}

    /**
     * 若是已经定义了[VB]的子类,则需要写明哪个类定义了,这样就会去查询该类定义的[VB]
     * @return Class<out Any>?
     */
    open fun getFatherClass(): Class<out Any>? = null

    /**
     * 若是已经定义了[VB]的子类,则需要实现[getFatherClass]
     * @see getFatherClass
     */
    abstract fun getViewBindingClass(): Class<out VB>

    /***
     * class.java.genericSuperClass只会获取该类的泛型类而不会获取其父类所拥有的泛型类.
     * 因此在多层子类中出现某个子类已实现了泛型而再往下的子类不再实现时如[BaseAdapterFragment]就需要单独寻找该子类的泛型参数
     * @param inflater LayoutInflater
     * @param container ViewGroup?
     * @return VB?
     */
    @Suppress("UNCHECKED_CAST")
    protected fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB? {
        getFatherClass()?.let { c ->
            if (c.isAssignableFrom(javaClass)) {
                (c.genericSuperclass as ParameterizedType).actualTypeArguments.iterator().forEach {
                    val clazz = it as Class<*>
                    Timber.v("clazz = ${clazz.simpleName}")
                    if (clazz == getViewBindingClass()) {
                        val method = clazz.getDeclaredMethod(
                            "inflate",
                            LayoutInflater::class.java,
                            ViewGroup::class.java,
                            Boolean::class.java
                        )
                        return method.invoke(null, inflater, container, false) as VB
                    }
                }
            } else {
                Timber.w("$javaClass is not son of $c")
            }
        }

        val type    = javaClass.genericSuperclass as ParameterizedType

        type.actualTypeArguments.iterator().forEach {
            val aClass  = it as Class<*>
            if (aClass == getViewBindingClass()) {
                val method = aClass.getDeclaredMethod(
                    "inflate",
                    LayoutInflater::class.java,
                    ViewGroup::class.java,
                    Boolean::class.java
                )
                return method.invoke(null, inflater, container, false) as VB
            }
        }
        return null
    }

    override fun onStart() {
        super.onStart()
        resizeDialog()
    }

    private fun resizeDialog() {
        dialog?.window?.let { window ->
            window.attributes.apply {
                val screenSize = ScreenUtil.getScreenSize(requireContext())
                dialogHeightPercent?.let {
                    height  = (screenSize.height * it).toInt()
                }
                dialogWidthPercent?.let {
                    width   = (screenSize.width * it).toInt()
                }
                window.setLayout(width, height)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        bindView()
    }

    open fun bindView() {}

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
        RxJava.sendMainSingle ({
            synchronized(this) {
                Timber.v("${this.javaClass.simpleName}.dismissDialog(). isShowing = ${isShowing()}")
                if (isShowing()) {
                    showed = false
                    dismiss()
                }
            }
        })
    }

    fun isShowing(): Boolean = showed || isAdded && dialog?.isShowing == true

    fun showInFragment(fragment: Fragment, autoDismiss: Boolean = false) {
        showDialog(fragment.parentFragmentManager, autoDismiss)
    }

    fun showInActivity(activity: FragmentActivity, autoDismiss: Boolean = false) {
        showDialog(activity.supportFragmentManager, autoDismiss)
    }

    @SuppressLint("AutoDispose")
    fun showDialog(fm: FragmentManager, autoDismiss: Boolean = false) {
        RxJava.sendMainSingle ({
            Timber.d("showDialog isShowing = ${isShowing()}")
            synchronized(this) {
                if (!isShowing()) {
                    show(fm, this.javaClass.simpleName)
                }
            }
        })
        Observable.just(Unit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( {

                }, {
                    it.printStackTrace()
                })
        if (autoDismiss) {
            Observable.just(Unit)
                    .delay(autoDismissDelay, TimeUnit.MILLISECONDS)
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
                ft.remove(this@BaseDialogFragment)
            }
            ft.commit()
            showed = true
            super.show(manager, tag)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}