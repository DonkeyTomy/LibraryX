package com.zzx.utils.context

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import timber.log.Timber

/**@author Tomy
 * Created by Tomy on 2018/7/10.
 */
object ContextUtil {

    inline fun <reified T: Activity, reified F: Fragment>startActivityWithFragmentName(context: Context, bundle: Bundle? = null, needNewTask: Boolean = false) {
        try {
            Intent(context, T::class.java).apply {
                putExtra(FRAGMENT_NAME, F::class.java.name)
                bundle?.let {
                    putExtra(FRAGMENT_BUNDLE, it)
                }
                Timber.d("bundle = $bundle")
                if (needNewTask) {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(this)
            }
        } catch (e: Exception) {

        }
    }

    inline fun <reified T: Activity>startActivity(context: Context, bundle: Bundle? = null, needNewTask: Boolean = false, needKillSelf: Boolean = false) {
        try {
            Intent(context, T::class.java).apply {
                if (needNewTask) {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                bundle?.let {
                    putExtra(FRAGMENT_BUNDLE, it)
                }
                context.startActivity(this)
            }
            if (needKillSelf) {
                if (context is Activity) {
                    context.finish()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    const val FRAGMENT_NAME = "fragmentName"
    const val FRAGMENT_BUNDLE = "fragmentBundle"

    fun startOtherActivity(context: Context, pkgName: String, clsName: String, bundle: Bundle? = null): Boolean {
        return try {
            val intent = Intent().apply {
                setClassName(pkgName, clsName)
                bundle?.let {
                    putExtras(it)
                }
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

}