package com.tomy.lib.ui.activity

import android.os.Bundle
import androidx.fragment.app.*
import com.tomy.lib.ui.R
import timber.log.Timber

/**@author Tomy
 * Created by Tomy on 22/2/2021.
 */

inline fun <reified T: Fragment> FragmentManager.addFragment(bundle: Bundle? = null, tag: String? = T::class.java.name,
                                                             allowStateLoss: Boolean = true, containerId :Int = R.id.container) {
    commit(allowStateLoss) {
        add<T>(containerId, tag ?: T::class.java.name, bundle)
    }
}

/**
 * @param needAddToBack Boolean 是否加入堆栈
 * @param bundle Bundle?
 * @param tag String? 用于[FragmentManager.findFragmentByTag].默认使用完整类名
 * @param allowStateLoss Boolean
 * @param containerId Int 指定所属容器的id.默认: R.id.container
 * @param replaceIfExist Boolean [FragmentManager.findFragmentByTag]查到已有是否选择强行替换.默认false.
 */
inline fun <reified T: Fragment> FragmentManager.replaceFragment(needAddToBack: Boolean = true,
                                                                 bundle: Bundle? = null, tag: String? = T::class.java.name,
                                                                 allowStateLoss: Boolean = true, containerId :Int = R.id.container, replaceIfExist: Boolean = false) {
    tag?.let {
        if (replaceIfExist && findFragmentByTag(it) != null) {
            return
        }
    }
    commit(allowStateLoss) {
        Timber.v("replaceFragment()")
        replace<T>(containerId, tag ?: T::class.java.name, bundle)
        if (needAddToBack) {
            addToBackStack(T::class.java.name)
        }
    }
}

inline fun <reified T: Fragment> FragmentManager.removeFragment(tag: String? = T::class.java.name, allowStateLoss: Boolean = true) {
    Timber.v("removeFragment: $tag")
    commit(allowStateLoss) {
        findFragmentByTag(tag)?.let { remove(it) }
    }
}



/**
 * @param bundle Bundle?
 */
inline fun <reified T: Fragment> Fragment.addFragment(bundle: Bundle? = null, tag: String? = T::class.java.name, allowStateLoss: Boolean = true) {
    parentFragmentManager.addFragment<T>(bundle, tag, allowStateLoss)
}

/**
 * @param needAddToBack Boolean 是否加入堆栈
 * @param bundle Bundle?
 * @param tag String? 用于[FragmentManager.findFragmentByTag].默认使用完整类名
 * @param allowStateLoss Boolean
 * @param containerId Int 指定所属容器的id.默认: R.id.container
 * @param replaceIfExist Boolean [FragmentManager.findFragmentByTag]查到已有是否选择强行替换.默认false.
 */
inline fun <reified T: Fragment> Fragment.replaceFragment(needAddToBack: Boolean = true,
                                                          bundle: Bundle? = null, tag: String? = T::class.java.name,
                                                          allowStateLoss: Boolean = true, containerId :Int = R.id.container,
                                                          replaceIfExist: Boolean = false) {
    parentFragmentManager.replaceFragment<T>(needAddToBack, bundle, tag, allowStateLoss, containerId, replaceIfExist)
}

inline fun <reified T: Fragment> Fragment.removeFragment(tag: String? = T::class.java.name, allowStateLoss: Boolean = true) {
    parentFragmentManager.removeFragment<T>(tag, allowStateLoss)
}



inline fun <reified T: Fragment> FragmentActivity.addFragment(bundle: Bundle? = null, tag: String? = T::class.java.name, allowStateLoss: Boolean = false, containerId :Int = R.id.container) {
    supportFragmentManager.addFragment<T>(bundle, tag, allowStateLoss, containerId)
}

/**
 * @param needAddToBack Boolean 是否加入堆栈
 * @param bundle Bundle?
 * @param tag String? 用于[FragmentManager.findFragmentByTag].默认使用完整类名
 * @param allowStateLoss Boolean
 * @param resId Int 指定所属容器的id.默认: R.id.container
 * @param replaceIfExist Boolean [FragmentManager.findFragmentByTag]查到已有是否选择强行替换.默认false.
 */
inline fun <reified T: Fragment> FragmentActivity.replaceFragment(needAddToBack: Boolean = true,
                                                                  bundle: Bundle? = null, tag: String? = T::class.java.name,
                                                                  allowStateLoss: Boolean = true, resId :Int = R.id.container,
                                                                  replaceIfExist: Boolean = false) {
    supportFragmentManager.replaceFragment<T>(needAddToBack, bundle, tag, allowStateLoss, resId, replaceIfExist)
}

inline fun <reified T: Fragment> FragmentActivity.removeFragment(tag: String? = T::class.java.name, allowStateLoss: Boolean = true) {
    supportFragmentManager.removeFragment<T>(tag, allowStateLoss)
}

class CustomFragmentManager