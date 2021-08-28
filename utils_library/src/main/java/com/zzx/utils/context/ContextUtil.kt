package com.zzx.utils.context

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import timber.log.Timber
import java.lang.reflect.ParameterizedType

/**@author Tomy
 * Created by Tomy on 2018/7/10.
 */
object ContextUtil {

    private var mApplicationContext: Context? = null

    fun initGlobalApplicationContext(context: Context) {
        mApplicationContext = context
    }

    fun releaseGlobalApplicationContext() {
        mApplicationContext = null
    }

    fun getGlobalContext(): Context? {
        return mApplicationContext
    }

    inline fun <reified T: Activity, reified F: Fragment> startActivityWithFragmentName(context: Context, bundle: Bundle? = null, needNewTask: Boolean = false, needTransition: Boolean = true) {
        try {
            var compat: ActivityOptionsCompat? = null
            if (Thread.currentThread() == Looper.getMainLooper().thread && context is Activity && needTransition) {
                compat = ActivityOptionsCompat.makeSceneTransitionAnimation(context)
            }
            Intent(context, T::class.java).apply {
                putExtra(FRAGMENT_NAME, F::class.java.name)
                bundle?.let {
                    putExtra(FRAGMENT_BUNDLE, it)
                }
                Timber.d("bundle = $bundle")
                if (needNewTask) {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(this, compat?.toBundle())
            }
        } catch (e: Exception) {

        }
    }

    inline fun <reified T: Activity> startActivity(context: Context, bundle: Bundle? = null, needNewTask: Boolean = false, needKillSelf: Boolean = false, needTransition: Boolean = true) {
        try {
            var compat: ActivityOptionsCompat? = null
            if (Thread.currentThread() == Looper.getMainLooper().thread && context is Activity && needTransition) {
                compat = ActivityOptionsCompat.makeSceneTransitionAnimation(context)
            }
            Intent(context, T::class.java).apply {
                if (needNewTask) {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                bundle?.let {
                    putExtras(it)
                }
                context.startActivity(this, compat?.toBundle())
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


    inline fun <reified T: Service> startService(context: Context, bundle: Bundle? = null) {
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

    inline fun <reified T: Service> stopService(context: Context) {
        try {
            Intent(context, T::class.java).apply {
                context.stopService(this)
            }
        } catch (e: Exception) {

        }
    }


    inline fun <reified T: Service> bindService(context: Context, connection: ServiceConnection, flag: Int = Context.BIND_AUTO_CREATE, bundle: Bundle? = null) {
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

/**
 * class.java.genericSuperClass只会获取该类的泛型类而不会获取其父类所拥有的泛型类.
 * 因此在多层子类中出现某个子类已实现了泛型而再往下的子类不再实现时就需要单独寻找该子类的泛型参数
 * @receiver Son
 * @param Father 判断是否是父类
 * @param Son 判断是否是[Father]的子类.调用此方法的对象本身的类或者父类即可
 * @param inflater LayoutInflater
 * @param container ViewGroup?
 * @return VB?
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified Father, Son: Any, reified VB> Son.getViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB? {
    Timber.d("Father = ${Father::class.java.simpleName}; Son = ${javaClass.simpleName}; VB = ${VB::class.java.simpleName}")
    if (Father::class.java.isAssignableFrom(javaClass)) {
        Timber.d("isAssignableFrom()")
        (Father::class.java.genericSuperclass as ParameterizedType).actualTypeArguments.iterator().forEach {
            Timber.d("type = ${it::class.java.simpleName}")
            val clazz = it as Class<*>
            Timber.v("clazz = ${clazz.simpleName}")
            if (clazz == VB::class.java) {
                val method = clazz.getDeclaredMethod(
                        "inflate",
                        LayoutInflater::class.java,
                        ViewGroup::class.java,
                        Boolean::class.java
                )
                Timber.v("load Base ViewBinding")
                return method.invoke(null, inflater, container, false) as VB
            }
        }
    }
    Timber.d("is not AssignableFrom()")
    val type    = this.javaClass.genericSuperclass as ParameterizedType
    type.actualTypeArguments.iterator().forEach {
        val aClass  = it as Class<*>
//            if (!ViewDataBinding::class.java.isAssignableFrom(aClass) && ViewBinding::class.java.isAssignableFrom(aClass)) {
        if (aClass == VB::class.java) {
            val method = aClass.getDeclaredMethod(
                    "inflate",
                    LayoutInflater::class.java,
                    ViewGroup::class.java,
                    Boolean::class.java
            )
            Timber.v("load ViewBinding")
            return method.invoke(null, inflater, container, false) as VB
        }
    }
    return null
}

inline fun <reified T: Activity, reified F: Fragment> Context.startActivityWithFragmentName(bundle: Bundle? = null, needNewTask: Boolean = false, needTransition: Boolean = true) {
    try {
        var compat: ActivityOptionsCompat? = null
        if (Thread.currentThread() == Looper.getMainLooper().thread && this is Activity && needTransition) {
            compat = ActivityOptionsCompat.makeSceneTransitionAnimation(this)
        }
        Intent(this, T::class.java).apply {
            putExtra(ContextUtil.FRAGMENT_NAME, F::class.java.name)
            bundle?.let {
                putExtra(ContextUtil.FRAGMENT_BUNDLE, it)
            }
            Timber.d("bundle = $bundle")
            if (needNewTask) {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(this, compat?.toBundle())
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

inline fun <reified T: Activity> Context.startActivity(bundle: Bundle? = null, needNewTask: Boolean = false, needKillSelf: Boolean = false, needTransition: Boolean = true) {
    try {
        var compat: ActivityOptionsCompat? = null
        if (Thread.currentThread() == Looper.getMainLooper().thread && this is Activity && needTransition) {
            compat = ActivityOptionsCompat.makeSceneTransitionAnimation(this)
        }
        Intent(this, T::class.java).apply {
            if (needNewTask) {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            bundle?.let {
                putExtras(it)
            }
            this@startActivity.startActivity(this, compat?.toBundle())
        }
        if (needKillSelf) {
            if (this is Activity) {
                this.finish()
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}


inline fun <reified T: Service> Context.startService(bundle: Bundle? = null) {
    try {
        Intent(this, T::class.java).apply {
            bundle?.let {
                putExtras(it)
            }
            Timber.d("startService = $bundle")
            this@startService.startService(this)
        }
    } catch (e: Exception) {

    }
}

inline fun <reified T: Service> Context.stopService() {
    try {
        Intent(this, T::class.java).apply {
            this@stopService.stopService(this)
        }
    } catch (e: Exception) {

    }
}


inline fun <reified T: Service> Context.bindService(connection: ServiceConnection, flag: Int = Context.BIND_AUTO_CREATE, bundle: Bundle? = null) {
    try {
        Intent(this, T::class.java).apply {
            bundle?.let {
                putExtras(it)
            }
            Timber.d("bindService = $bundle")
            this@bindService.bindService(this, connection, flag)
        }
    } catch (e: Exception) {

    }
}
