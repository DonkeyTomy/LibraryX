package com.tomy.lib.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding

/**@author Tomy
 * Created by Tomy on 2017/10/11.
 */
abstract class BaseFragmentDataBind<DB : ViewDataBinding> : BaseFragmentViewBind<DB>() {

    /**
     * 若是已经定义了[DB]的子类,则需要实现[getFatherClass]
     * @see getFatherClass
     */
    abstract fun getDataBindingClass(): Class<out DB>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.lifecycleOwner = this
    }

    override fun onDestroy() {
        _binding?.unbind()
        super.onDestroy()
    }

}