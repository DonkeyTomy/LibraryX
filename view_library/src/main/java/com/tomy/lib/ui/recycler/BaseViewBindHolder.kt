package com.tomy.lib.ui.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.tomy.lib.ui.utils.LayoutUtil

/**@author Tomy
 * Created by Tomy on 11/1/2021.
 */
abstract class BaseViewBindHolder<T, VB: ViewBinding>(var viewBinding: VB): RecyclerView.ViewHolder(viewBinding.root) {

    abstract fun setData(data: T)

    companion object {

        fun <T, VB: ViewBinding> instantiateViewBind(context: Context, viewGroup: ViewGroup, viewHolderClass: Class<*>, dbClazz: Class<*>): BaseViewBindHolder<T, VB> {
            return viewHolderClass.getConstructor(dbClazz).newInstance(LayoutUtil.createViewBinding(
                dbClazz as Class<out ViewBinding>, LayoutInflater.from(context), viewGroup)) as BaseViewBindHolder<T, VB>
        }

    }
}