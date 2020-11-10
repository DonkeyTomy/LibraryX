package com.tomy.lib.ui.utils

import androidx.lifecycle.LifecycleOwner
import autodispose2.AutoDispose
import autodispose2.AutoDisposeConverter
import autodispose2.ScopeProvider
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider

/**@author Tomy
 * Created by Tomy on 10/9/2020.
 */
object RxLifecycleUtils {

    fun bindLifecycle(lifecycleOwner: LifecycleOwner): AutoDisposeConverter<ScopeProvider> {
        return AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(lifecycleOwner))
    }

}