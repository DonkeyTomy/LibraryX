package com.tomy.lib.ui.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

/**@author Tomy
 * Created by Tomy on 11/1/2021.
 */
object LayoutUtil {
    fun createViewBinding(aClass: Class<out ViewBinding>, inflater: LayoutInflater, container: ViewGroup): ViewBinding {
        val method = aClass.getDeclaredMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
        return method.invoke(null, inflater, container, false) as ViewBinding
    }
}