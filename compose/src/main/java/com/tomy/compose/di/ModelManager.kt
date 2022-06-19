package com.tomy.compose.di

import com.tomy.compose.activity.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val mainModel = module {
    viewModelOf(::MainViewModel)
}