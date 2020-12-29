package com.tomy.lib.ui.activity

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.tomy.lib.ui.R

/**@author Tomy
 * Created by Tomy on 19/11/2020.
 * layout must need id R.id.container
 */
open class BaseActivity: AppCompatActivity() {

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

}