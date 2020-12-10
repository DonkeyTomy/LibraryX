package com.tomy.lib.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.tomy.lib.ui.R
import timber.log.Timber
import java.lang.reflect.ParameterizedType

/**@author Tomy
 * Created by Tomy on 11/11/2020.
 * 显示单个Fragment的容器Activity
 *
 */
open class FragmentContainerBaseActivity<VB: ViewBinding>: BaseKeyListenerActivity() {

    protected val mBinding: VB by lazy {
        val type = javaClass.genericSuperclass as ParameterizedType
        val aClass = type.actualTypeArguments[0] as Class<*>
        val method = aClass.getDeclaredMethod("inflate", LayoutInflater::class.java)
        method.invoke(null, layoutInflater) as VB
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        val name = intent.getStringExtra(FRAGMENT_NAME)
        Timber.d("name = $name. state = $savedInstanceState")
        if (savedInstanceState == null) {
            name?.apply {
                supportFragmentManager.beginTransaction().add(
                    R.id.container,
                    Fragment.instantiate(this@FragmentContainerBaseActivity, this, intent.getBundleExtra(FRAGMENT_BUNDLE))).commit()
            }
        }
    }

    inline fun <reified T: Fragment>addFragment() {
        supportFragmentManager.beginTransaction().add(
                R.id.container,
                Fragment.instantiate(this, T::class.java.name)).commit()
    }

    inline fun <reified T: Fragment>replaceFragment(needAddToBack: Boolean = true) {
        val beginTransaction = supportFragmentManager.beginTransaction()
        beginTransaction.replace(
                R.id.container,
                Fragment.instantiate(this, T::class.java.name))
        if (needAddToBack) {
            beginTransaction.addToBackStack(T::class.java.name)
        }
        beginTransaction.commitAllowingStateLoss()
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

//    open fun getLayoutId(): Int? = null

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Timber.d("onNewIntent = $intent")
        intent?.getStringExtra(FRAGMENT_NAME)?.apply {
            supportFragmentManager.beginTransaction().replace(
                R.id.container,
                Fragment.instantiate(this@FragmentContainerBaseActivity, this, intent.getBundleExtra(FRAGMENT_BUNDLE))).addToBackStack(this).commit()
        }
    }

    companion object {
        const val FRAGMENT_NAME = "fragmentName"
        const val FRAGMENT_BUNDLE = "fragmentBundle"
    }

}