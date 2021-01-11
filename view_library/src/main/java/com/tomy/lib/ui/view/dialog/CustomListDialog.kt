package com.tomy.lib.ui.view.dialog

import androidx.databinding.ViewDataBinding
import com.tomy.lib.ui.adapter.RecyclerViewBindAdapter
import com.tomy.lib.ui.databinding.CustomListDialogBinding
import com.tomy.lib.ui.fragment.BaseDialogFragment
import com.tomy.lib.ui.recycler.BaseViewBindHolder

/**@author Tomy
 * Created by Tomy on 11/1/2021.
 */
abstract class CustomListDialog<T, VB: ViewDataBinding>: BaseDialogFragment<CustomListDialogBinding>() {

    private val mAdapter by lazy { RecyclerViewBindAdapter<T, VB>(getViewHolderClass(), getViewBindingClass()) }

    abstract fun getViewHolderClass(): Class<*>

    abstract fun getViewBindingClass(): Class<*>


}