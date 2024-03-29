package com.tomy.lib.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import timber.log.Timber
import java.lang.reflect.ParameterizedType

/**@author Tomy
 * Created by Tomy on 3/12/2020.
 */
abstract class BaseFragmentViewBind<VB: ViewBinding>: BaseFragment(), CoroutineScope by MainScope() {

    protected var _binding: VB? = null
    protected val mBinding: VB get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (_binding == null) {
//            mRootView = inflater.inflate(bindLayout(), container, false)
            _binding = getViewBinding(LayoutInflater.from(requireActivity()), container)!!
        }
        mRootView = _binding?.root?.apply { (parent as? ViewGroup)?.removeView(this) }
        Timber.v("${javaClass.simpleName} onCreateView(): mRootView = $mRootView; mBinding = $mBinding")
        modifyView(mRootView!!)
        return mRootView!!
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
        _binding = null
    }


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
    private fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB? {
        getFatherClass()?.let { c ->
//            Timber.v("fatherClass = $c; javaClass = $javaClass")
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
//                        Timber.v("load ViewBinding: ${clazz.simpleName}")
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
//            Timber.v("${this.javaClass.simpleName} aClass = ${aClass.simpleName}; ${getViewBindingClass().simpleName} ${ViewBinding::class.java.isAssignableFrom(aClass)}, ${ViewDataBinding::class.java.isAssignableFrom(aClass)}")
//            Timber.v("${this.javaClass.simpleName} equals = ${aClass == getViewBindingClass()}")
            if (aClass == getViewBindingClass()) {
                val method = aClass.getDeclaredMethod(
                    "inflate",
                    LayoutInflater::class.java,
                    ViewGroup::class.java,
                    Boolean::class.java
                )
//                Timber.v("load ViewBinding: ${aClass.simpleName}")
                return method.invoke(null, inflater, container, false) as VB
            }
        }
        return null
    }

}