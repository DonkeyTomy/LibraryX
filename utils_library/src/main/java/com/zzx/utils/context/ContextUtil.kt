package com.zzx.utils.context

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle

/**@author Tomy
 * Created by Tomy on 2018/7/10.
 */
object ContextUtil {

    inline fun <reified T: Activity>startActivityWithFragmentName(context: Context, fragmentName: String, bundle: Bundle? = null, needNewTask: Boolean = false) {
        try {
            Intent(context, T::class.java).apply {
                putExtra(FRAGMENT_NAME, fragmentName)
                bundle?.let {
                    putExtra(FRAGMENT_BUNDLE, it)
                }
                if (needNewTask) {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(this)
            }
        } catch (e: Exception) {

        }
    }

    fun <T: Activity>startActivity(context: Context, clazz: Class<T>, needNewTask: Boolean = false, needKillSelf: Boolean = false) {
        try {
            Intent(context, clazz).apply {
                if (needNewTask) {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
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