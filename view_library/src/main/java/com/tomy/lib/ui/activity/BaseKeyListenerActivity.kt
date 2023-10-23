package com.tomy.lib.ui.activity

import android.view.KeyEvent
import com.tomy.lib.ui.fragment.BaseFragment
import timber.log.Timber


/**@author Tomy
 * Created by Tomy on 11/11/2020.
 */
abstract class BaseKeyListenerActivity: BaseActivity() {

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.repeatCount == 0) {
            Timber.v("onKeyDown() keyCode = $keyCode")
        }
        supportFragmentManager.fragments.forEach {
//            Timber.v("childFragment: $it; ${it.childFragmentManager.fragments.size}")
            if (it is KeyEvent.Callback) {
                if (it.onKeyDown(keyCode, event)) {
                    return true
                }
            }
            it.childFragmentManager.fragments.forEach {child ->
//                Timber.v("childFragment: $child")
                if (child is KeyEvent.Callback) {
                    if (child.onKeyDown(keyCode, event)) {
                        return true
                    }
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        supportFragmentManager.fragments.forEach {
            if (it is KeyEvent.Callback) {
                if (it.onKeyUp(keyCode, event)) {
                    return true
                }
            }
        }
        return super.onKeyUp(keyCode, event)
    }

    override fun onBackPressed() {
        supportFragmentManager.fragments.forEach {
            if (it is BaseFragment) {
                if (it.onBackPressed()) {
                    return
                }
            }
        }
        Timber.v("onBackPressed()")
        super.onBackPressed()
    }
}