package com.tomy.lib.ui.activity

import android.os.Bundle
import androidx.fragment.app.*
import androidx.lifecycle.lifecycleScope
import com.tomy.lib.ui.R
import timber.log.Timber

/**@author Tomy
 * Created by Tomy on 22/2/2021.
 */

/**
 * @param bundle Bundle?
 */
inline fun <reified T: Fragment> Fragment.addFragment(bundle: Bundle? = null, tag: String? = T::class.java.name, allowStateLoss: Boolean = true, containerId :Int = R.id.container) {
    parentFragmentManager.addFragment<T>(bundle, tag, allowStateLoss, containerId)
}

inline fun <reified T: Fragment> FragmentActivity.addFragment(bundle: Bundle? = null, tag: String? = T::class.java.name, allowStateLoss: Boolean = false, containerId :Int = R.id.container) {
    supportFragmentManager.addFragment<T>(bundle, tag, allowStateLoss, containerId)
}

inline fun <reified T: Fragment> FragmentManager.addFragment(bundle: Bundle? = null, tag: String? = T::class.java.name,
                                                             allowStateLoss: Boolean = true, containerId :Int = R.id.container) {
    commit(allowStateLoss) {
        add<T>(containerId, tag ?: T::class.java.name, bundle)
    }
}

/**
 * @see H 需要hide的Fragment.
 * @see T 需要add的Fragment.
 * @param bundle Bundle?
 * @param tag String?
 * @param hideTag String? 通过[FragmentManager.findFragmentByTag]获取需要hide的Fragment.默认是[H]类名
 * @param allowStateLoss Boolean
 * @param containerId Int
 * @param needAddToBack Boolean
 */
inline fun <reified H: Fragment, reified T: Fragment> Fragment.coverFragment(
    bundle: Bundle? = null, tag: String? = T::class.java.name,
    hideTag: String? = H::class.java.name, allowStateLoss: Boolean = true,
    containerId :Int = R.id.container, needAddToBack: Boolean = true
) {
    parentFragmentManager.coverFragment<H, T>(hideTag, tag, bundle, allowStateLoss, containerId, needAddToBack)
}

inline fun <reified H: Fragment, reified T: Fragment> FragmentActivity.coverFragment(
    bundle: Bundle? = null, tag: String? = T::class.java.name,
    hideTag: String? = H::class.java.name, allowStateLoss: Boolean = false,
    containerId :Int = R.id.container, needAddToBack: Boolean = true
) {
    supportFragmentManager.coverFragment<H, T>(hideTag, tag, bundle, allowStateLoss, containerId,  needAddToBack)
}

inline fun <reified H: Fragment, reified T: Fragment> FragmentManager.coverFragment(
    tag: String? = T::class.java.name, hideTag: String? = H::class.java.name,
    bundle: Bundle? = null, allowStateLoss: Boolean = true,
    containerId :Int = R.id.container, needAddToBack: Boolean = true
) {
    commit(allowStateLoss) {
        hideTag?.let {
            findFragmentByTag(it)?.let { hideFragment ->
                hide(hideFragment)
            }
        }
        add<T>(containerId, tag ?: T::class.java.name, bundle)
        if (needAddToBack) {
            addToBackStack(tag)
        }
    }
}


/**
 * @see H 需要hide的Fragment.
 * @see T 需要add的Fragment.
 * @param bundle Bundle?
 * @param tag String?
 * @param hideTag String? 通过[FragmentManager.findFragmentByTag]获取需要hide的Fragment.默认是[H]类名
 * @param allowStateLoss Boolean
 * @param containerId Int
 * @param needAddToBack Boolean
 */
inline fun <reified H: Fragment, reified T: Fragment> Fragment.coverFragment(
    fragment: T,
    bundle: Bundle? = null, tag: String? = T::class.java.name,
    hideTag: String? = H::class.java.name, allowStateLoss: Boolean = true,
    containerId :Int = R.id.container, needAddToBack: Boolean = true
) {
    parentFragmentManager.coverFragment<H, T>(fragment, hideTag, tag, bundle, allowStateLoss, containerId, needAddToBack)
}

inline fun <reified H: Fragment, reified T: Fragment> FragmentActivity.coverFragment(
    fragment: T,
    bundle: Bundle? = null, tag: String? = T::class.java.name,
    hideTag: String? = H::class.java.name, allowStateLoss: Boolean = false,
    containerId :Int = R.id.container, needAddToBack: Boolean = true
) {
    supportFragmentManager.coverFragment<H, T>(fragment, hideTag, tag, bundle, allowStateLoss, containerId,  needAddToBack)
}

inline fun <reified H:Fragment, reified T: Fragment> FragmentManager.coverFragment(
    fragment: T,
    tag: String? = T::class.java.name, hideTag: String? = H::class.java.name,
    bundle: Bundle? = null, allowStateLoss: Boolean = true,
    containerId :Int = R.id.container, needAddToBack: Boolean = true
) {
    commit(allowStateLoss) {
        hideTag?.let {
            findFragmentByTag(it)?.let { hideFragment ->
                hide(hideFragment)
            }
        }
        bundle?.let {
            fragment.arguments = it
        }
        add(containerId, fragment, tag ?: T::class.java.name)
        if (needAddToBack) {
            addToBackStack(tag)
        }
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
inline fun <reified T: Fragment> Fragment.replaceFragment(needAddToBack: Boolean = true,
                                                          bundle: Bundle? = null, tag: String? = T::class.java.name,
                                                          allowStateLoss: Boolean = true, containerId :Int = R.id.container,
                                                          replaceIfExist: Boolean = false) {
    parentFragmentManager.replaceFragment<T>(needAddToBack, bundle, tag, allowStateLoss, containerId, replaceIfExist)
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
        if (!replaceIfExist && findFragmentByTag(it) != null) {
//            return
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
        findFragmentByTag(tag)?.let {
            remove(it)
        }
    }
}

inline fun <reified T: Fragment> Fragment.removeFragment(tag: String? = T::class.java.name, allowStateLoss: Boolean = true) {
    parentFragmentManager.removeFragment<T>(tag, allowStateLoss)
}

inline fun <reified T: Fragment> FragmentActivity.removeFragment(tag: String? = T::class.java.name, allowStateLoss: Boolean = true) {
    supportFragmentManager.removeFragment<T>(tag, allowStateLoss)
}


fun Fragment.backToLauncherFragment() {
    if (context is FragmentActivity) {
        (context as FragmentActivity).backToLauncherFragment()
    }
}

fun FragmentActivity.backToLauncherFragment() {
    lifecycleScope.launchWhenResumed {
        runOnUiThread {
            supportFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }
}

/**
 * @param needFinishIfIsTop Boolean 是否在当前Fragment处于顶部时退出当前Activity
 */
fun Fragment.popToBack(needFinishIfIsTop: Boolean = true) {
    if (context is FragmentActivity) {
        (context as FragmentActivity).popToBack(needFinishIfIsTop)
    }
}

/**
 * @param needFinishIfIsTop Boolean 是否在当前Fragment处于顶部时退出当前Activity
 */
fun FragmentActivity.popToBack(needFinishIfIsTop: Boolean = true) {
    lifecycleScope.launchWhenResumed {
        runOnUiThread {
            if (!supportFragmentManager.popBackStackImmediate() && needFinishIfIsTop) {
                finish()
            }
        }
    }
}


/**
 * 返回到指定TAG的Fragment
 * @param fragmentTag String
 * @param needFinishIfIsTop Boolean
 */
inline fun <reified T: Fragment> Fragment.popToFragmentPoint(fragmentTag: String = T::class.java.name, needFinishIfIsTop: Boolean = true) {
    if (context is FragmentActivity) {
        (context as FragmentActivity).popToFragmentPoint<T>(fragmentTag, needFinishIfIsTop)
    }
}

inline fun <reified T: Fragment> FragmentActivity.popToFragmentPoint(
    fragmentTag: String = T::class.java.name, needFinishIfIsTop: Boolean = true) {
    lifecycleScope.launchWhenResumed {
        runOnUiThread {
            if (!supportFragmentManager.popBackStackImmediate(fragmentTag, 0) && needFinishIfIsTop) {
                finish()
            }
        }
    }
}


/**
 * 弹出包括指定TAG的Fragment及以上所有Fragment
 * @param fragmentTag String
 * @param needFinishIfIsTop Boolean
 */
inline fun <reified T: Fragment> Fragment.popIncludeFragmentPoint(
    fragmentTag: String = T::class.java.name, needFinishIfIsTop: Boolean = true) {
    if (context is FragmentActivity) {
        (context as FragmentActivity).popIncludeFragmentPoint<T>(fragmentTag, needFinishIfIsTop)
    }
}

/**
 * 弹出包括指定TAG的Fragment及以上所有Fragment
 * @param fragmentTag String
 * @param needFinishIfIsTop Boolean
 */
inline fun <reified T: Fragment> FragmentActivity.popIncludeFragmentPoint(
    fragmentTag: String = T::class.java.name, needFinishIfIsTop: Boolean = true) {
    lifecycleScope.launchWhenResumed {
        runOnUiThread {
            if (!supportFragmentManager.popBackStackImmediate(fragmentTag, FragmentManager.POP_BACK_STACK_INCLUSIVE) && needFinishIfIsTop) {
                finish()
            }
        }
    }
}
