package com.tomy.lib.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import timber.log.Timber
import java.lang.reflect.ParameterizedType

/**@author Tomy
 * Created by Tomy on 2017/10/11.
 */
abstract class BaseFragmentDataBind<DB : ViewBinding> : BaseFragment() {

    protected var mBinding: DB? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Timber.v("${javaClass.simpleName} onCreateView(): mRootView = $mRootView; mBinding = $mBinding")
        if (mRootView == null) {
//            mRootView = inflater.inflate(bindLayout(), container, false)
            mBinding = getViewBinding(LayoutInflater.from(requireActivity()), container)!!
            mRootView = mBinding!!.root
        }
        modifyView(mRootView!!)
        return mRootView!!
    }

    /**
     * 若是已经定义了[DB]的子类,则需要实现[getFatherClass]
     * @see getFatherClass
     */
    abstract fun getDataBindingClass(): Class<out DB>

    /**
     * 若是已经定义了[DB]的子类,则需要写明哪个类定义了,这样就会去查询该类定义的[DB]
     * @return Class<out Any>?
     */
    open fun getFatherClass(): Class<out Any>? = null

    /***
     * class.java.genericSuperClass只会获取该类的泛型类而不会获取其父类所拥有的泛型类.
     * 因此在多层子类中出现某个子类已实现了泛型而再往下的子类不再实现时如[BaseAdapterFragment]就需要单独寻找该子类的泛型参数
     * @param inflater LayoutInflater
     * @param container ViewGroup?
     * @return VB?
     */
    @Suppress("UNCHECKED_CAST")
    private fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): DB? {
        getFatherClass()?.let { c ->
            Timber.v("fatherClass = $c; javaClass = $javaClass")
            if (c.isAssignableFrom(javaClass)) {
                (c.genericSuperclass as ParameterizedType).actualTypeArguments.iterator().forEach {
                    val clazz = it as Class<*>
                    Timber.v("clazz = ${clazz.simpleName}")
                    if (clazz == getDataBindingClass()) {
                        val method = clazz.getDeclaredMethod(
                            "inflate",
                            LayoutInflater::class.java,
                            ViewGroup::class.java,
                            Boolean::class.java
                        )
                        Timber.v("load ViewBinding: ${clazz.simpleName}")
                        return method.invoke(null, inflater, container, false) as DB
                    }
                }
            } else {
                Timber.w("$javaClass is not son of $c")
            }
        }

        val type    = javaClass.genericSuperclass as ParameterizedType
        type.actualTypeArguments.iterator().forEach {
            val aClass  = it as Class<*>
//            Timber.v("${this.javaClass.simpleName} aClass = ${aClass.simpleName}; ${getViewBindingClass().simpleName} ${ViewBinding::class.java.isAssignableFrom(aClass)}, ${ViewDataBinding::class.java.isAssignableFrom(aClass)}")
//            Timber.v("${this.javaClass.simpleName} equals = ${aClass == getViewBindingClass()}")
            if (aClass == getDataBindingClass()) {
                val method = aClass.getDeclaredMethod(
                    "inflate",
                    LayoutInflater::class.java,
                    ViewGroup::class.java,
                    Boolean::class.java
                )
                Timber.v("load ViewBinding: ${aClass.simpleName}")
                return method.invoke(null, inflater, container, false) as DB
            }
        }
        return null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        /**
         * 加了这个,DialogFragment在消除再显示后会出现mBinding为null的问题.待解决
         */
//        mBinding = null
        Timber.v("${this.javaClass.simpleName} onDestroyView(): $mBinding")
    }
    override fun onDestroy() {
        super.onDestroy()
//        mBinding = null
    }

}