package com.tomy.lib.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.tomy.lib.ui.R

/**@author Tomy
 * Created by Tomy on 19/11/2020.
 * layout must need id R.id.container
 */
open class BaseActivity: AppCompatActivity() {

    inline fun <reified T: Fragment> addFragment(bundle: Bundle? = null) {
        supportFragmentManager.beginTransaction().add(
            R.id.container,
            T::class.java, bundle).commit()
    }

    inline fun <reified T: Fragment> replaceFragment(needAddToBack: Boolean = true, bundle: Bundle? = null) {
        val beginTransaction = supportFragmentManager.beginTransaction()
        beginTransaction.replace(
            R.id.container,
            T::class.java, bundle)
        if (needAddToBack) {
            beginTransaction.addToBackStack(T::class.java.name)
        }
        beginTransaction.commitAllowingStateLoss()
    }

}