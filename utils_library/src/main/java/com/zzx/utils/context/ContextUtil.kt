package com.zzx.utils.context

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
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
                    putExtras(it)
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


    inline fun <reified T: Service>startService(context: Context, bundle: Bundle? = null) {
        try {
            Intent(context, T::class.java).apply {
                bundle?.let {
                    putExtras(it)
                }
                Timber.d("startService = $bundle")
                context.startService(this)
            }
        } catch (e: Exception) {

        }
    }

    inline fun <reified T: Service>stopService(context: Context) {
        try {
            Intent(context, T::class.java).apply {
                context.stopService(this)
            }
        } catch (e: Exception) {

        }
    }


    inline fun <reified T: Service>bindService(context: Context, connection: ServiceConnection, flag: Int = Context.BIND_AUTO_CREATE, bundle: Bundle? = null) {
        try {
            Intent(context, T::class.java).apply {
                bundle?.let {
                    putExtras(it)
                }
                Timber.d("bindService = $bundle")
                context.bindService(this, connection, flag)
            }
        } catch (e: Exception) {

        }
    }

    fun bindServiceWithName(context: Context, pkgName: String, clsName: String, connection: ServiceConnection, flag: Int = Context.BIND_AUTO_CREATE, bundle: Bundle? = null) {
        try {
            Intent().apply {
                setClassName(pkgName, clsName)
                bundle?.let {
                    putExtras(it)
                }
                Timber.d("bindServiceWithName = $bundle")
                context.bindService(this, connection, flag)
            }
            context.unbindService(connection)
        } catch (e: Exception) {

        }
    }

}