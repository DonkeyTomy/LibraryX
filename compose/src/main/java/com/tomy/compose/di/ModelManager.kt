package com.tomy.compose.di

import com.tomy.compose.activity.MainViewModel
import com.tomy.compose.fragment.BaseContainerViewModel
import com.tomy.compose.adapter.BaseAdapterViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val mainModel = module {
    viewModelOf(::BaseContainerViewModel)
    viewModelOf(::MainViewModel)
    viewModelOf(::BaseAdapterViewModel)
}