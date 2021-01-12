package com.tomy.lib.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding
import timber.log.Timber
import java.lang.reflect.ParameterizedType

/**@author Tomy
 * Created by Tomy on 3/12/2020.
 */
abstract class BaseFragmentViewBind<VB: ViewBinding>: BaseFragment() {

    var mBinding: VB? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (mRootView == null) {
//            mRootView = inflater.inflate(bindLayout(), container, false)
            mBinding = getViewBinding(inflater, container)!!
            mRootView = mBinding!!.root
        }
        Timber.d("${this.javaClass.simpleName} onCreateView")
        modifyView(mRootView!!)
        return mRootView!!
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }

    abstract fun getViewBindingClass(): Class<out ViewBinding>

    /***
     * class.java.genericSuperClass只会获取该类的泛型类而不会获取其父类所拥有的泛型类.
     * 因此在多层子类中出现某个子类已实现了泛型而再往下的子类不再实现时如[BaseAdapterFragment]就需要单独寻找该子类的泛型参数
     * @param inflater LayoutInflater
     * @param container ViewGroup?
     * @return VB?
     */
    @Suppress("UNCHECKED_CAST")
    private fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB? {
        if (BaseAdapterFragment::class.java.isAssignableFrom(javaClass)) {
            (BaseAdapterFragment::class.java.genericSuperclass as ParameterizedType).actualTypeArguments.iterator().forEach {
                Timber.v("type = ${it::class.java.simpleName}")
                val clazz = it as Class<*>
                Timber.v("clazz = ${clazz.simpleName}")
                if (clazz == getViewBindingClass()) {
                    val method = clazz.getDeclaredMethod(
                        "inflate",
                        LayoutInflater::class.java,
                        ViewGroup::class.java,
                        Boolean::class.java
                    )
                    Timber.v("load Base ViewBinding")
                    return method.invoke(null, inflater, container, false) as VB
                }
            }
        }
        val type    = javaClass.genericSuperclass as ParameterizedType
        /*type.actualTypeArguments.iterator().forEach {
            Timber.d("${this.javaClass.simpleName} aClass = ${(it as Class<*>).simpleName}")
        }*/
        type.actualTypeArguments.iterator().forEach {
            val aClass  = it as Class<*>
            Timber.v("${this.javaClass.simpleName} aClass = ${aClass.simpleName}; ${getViewBindingClass().simpleName} ${ViewBinding::class.java.isAssignableFrom(aClass)}, ${ViewDataBinding::class.java.isAssignableFrom(aClass)}")
            Timber.v("${this.javaClass.simpleName} equals = ${aClass == getViewBindingClass()}")
//            if (!ViewDataBinding::class.java.isAssignableFrom(aClass) && ViewBinding::class.java.isAssignableFrom(aClass)) {
            if (aClass == getViewBindingClass()) {
                val method = aClass.getDeclaredMethod(
                    "inflate",
                    LayoutInflater::class.java,
                    ViewGroup::class.java,
                    Boolean::class.java
                )
                Timber.v("load ViewBinding")
                return method.invoke(null, inflater, container, false) as VB
            }
        }
        return null
    }

}