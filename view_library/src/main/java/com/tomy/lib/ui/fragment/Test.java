package com.tomy.lib.ui.fragment;

import androidx.viewbinding.ViewBinding;

import com.tomy.lib.ui.databinding.FragmentBaseRecyclerViewBinding;

/**
 * @author Tomy
 * Created by Tomy on 3/12/2020.
 */
class Test {

    Class<? extends ViewBinding> getBinding() {
        getClass().getGenericSuperclass();
        Class clazz = FragmentBaseRecyclerViewBinding.class;
        clazz.getGenericSuperclass();
        return FragmentBaseRecyclerViewBinding.class;
    }

}
