package com.tomy.component.activity

import android.view.KeyEvent
import timber.log.Timber


/**@author Tomy
 * Created by Tomy on 11/11/2020.
 */
abstract class BaseKeyListenerActivity: BasePermissionActivity() {

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.repeatCount == 0) {
            Timber.v("onKeyDown() keyCode = $keyCode")
        }
        supportFragmentManager.fragments.forEach {
            if (it is KeyEvent.Callback) {
                if (it.onKeyDown(keyCode, event)) {
                    return true
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
        Timber.v("onBackPressed()")
        super.onBackPressed()
    }
}