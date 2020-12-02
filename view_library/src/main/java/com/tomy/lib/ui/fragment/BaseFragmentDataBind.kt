package com.tomy.lib.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import timber.log.Timber

/**@author Tomy
 * Created by Tomy on 2017/10/11.
 */
abstract class BaseFragmentDataBind<T : ViewDataBinding> : BaseFragment() {

    protected lateinit var mDataBinding: T

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Timber.d("${this.javaClass.simpleName} onCreateView")
        mDataBinding = DataBindingUtil.inflate(inflater, bindLayout(), container, false)
        mRootView = mDataBinding.root
        modifyView(mRootView!!)
        return mRootView!!
    }

}