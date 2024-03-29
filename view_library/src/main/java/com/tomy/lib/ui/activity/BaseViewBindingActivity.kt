package com.tomy.lib.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
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
abstract class BaseViewBindingActivity<VB: ViewBinding>: BaseActivity() {

    protected val mBinding: VB by lazy {
        getBinding()
    }

    /**
     * 若某个子类已实现了[VB]后,再往下创建子类则需要覆写此方法.
     * @return VB
     */
    open fun getBinding(): VB {
        val type = javaClass.genericSuperclass as ParameterizedType
        val aClass = type.actualTypeArguments[0] as Class<*>
        val method = aClass.getDeclaredMethod("inflate", LayoutInflater::class.java)
        return method.invoke(null, layoutInflater) as VB
    }

    override fun init(savedInstanceState: Bundle?) {
        setContentView(mBinding.root)
        val name = intent.getStringExtra(FRAGMENT_NAME)
        Timber.d("name = $name. state = $savedInstanceState")
        if (savedInstanceState == null) {
            name?.apply {
                supportFragmentManager.beginTransaction().add(
                    R.id.container,
                    Fragment.instantiate(this@BaseViewBindingActivity, this, intent.getBundleExtra(FRAGMENT_BUNDLE)), this).commit()
            }
        }
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Timber.d("onNewIntent = $intent")
        intent?.getStringExtra(FRAGMENT_NAME)?.apply {
            supportFragmentManager.beginTransaction().replace(
                R.id.container,
                Fragment.instantiate(this@BaseViewBindingActivity, this, intent.getBundleExtra(FRAGMENT_BUNDLE))).addToBackStack(this).commit()
        }
    }

    companion object {
        const val FRAGMENT_NAME = "fragmentName"
        const val FRAGMENT_BUNDLE = "fragmentBundle"
    }

}