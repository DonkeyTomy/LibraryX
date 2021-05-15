package com.tomy.lib.ui.activity

import android.os.Bundle
import androidx.fragment.app.*
import com.tomy.lib.ui.R
import timber.log.Timber

/**@author Tomy
 * Created by Tomy on 22/2/2021.
 */

inline fun <reified T: Fragment> FragmentManager.addFragment(bundle: Bundle? = null, tag: String? = null,
                                                             allowStateLoss: Boolean = true, containerId :Int = R.id.container) {
    commit(allowStateLoss) {
        add<T>(containerId, tag ?: T::class.java.name, bundle)
    }
}

inline fun <reified T: Fragment> FragmentManager.replaceFragment(needAddToBack: Boolean = true,
                                                                 bundle: Bundle? = null, tag: String? = null,
                                                                 allowStateLoss: Boolean = true, containerId :Int = R.id.container) {
    commit(allowStateLoss) {
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
inline fun <reified T: Fragment> Fragment.addFragment(bundle: Bundle? = null, tag: String? = null, allowStateLoss: Boolean = true) {
    parentFragmentManager.addFragment<T>(bundle, tag, allowStateLoss)
}

/**
 * @param needAddToBack Boolean
 * @param bundle Bundle?
 */
inline fun <reified T: Fragment> Fragment.replaceFragment(needAddToBack: Boolean = true, bundle: Bundle? = null, tag: String? = null, allowStateLoss: Boolean = true) {
    parentFragmentManager.replaceFragment<T>(needAddToBack, bundle, tag, allowStateLoss)
}

inline fun <reified T: Fragment> Fragment.removeFragment(tag: String? = T::class.java.name, allowStateLoss: Boolean = true) {
    parentFragmentManager.removeFragment<T>(tag, allowStateLoss)
}



inline fun <reified T: Fragment> FragmentActivity.addFragment(bundle: Bundle? = null, tag: String? = null, allowStateLoss: Boolean = false, containerId :Int = R.id.container) {
    supportFragmentManager.commit(allowStateLoss) {
        add<T>(containerId, tag ?: T::class.java.name, bundle)
    }
}

inline fun <reified T: Fragment> FragmentActivity.replaceFragment(needAddToBack: Boolean = true, bundle: Bundle? = null, tag: String? = null, allowStateLoss: Boolean = true, resId :Int = R.id.container) {
    supportFragmentManager.commit(allowStateLoss) {
        replace<T>(resId, tag ?: T::class.java.name, bundle)
        if (needAddToBack) {
            addToBackStack(T::class.java.name)
        }
    }
}

inline fun <reified T: Fragment> FragmentActivity.removeFragment(tag: String? = T::class.java.name, allowStateLoss: Boolean = true) {
    supportFragmentManager.commit(allowStateLoss) {
        supportFragmentManager.findFragmentByTag(tag)?.let { remove(it) }
    }
}

class CustomFragmentManager