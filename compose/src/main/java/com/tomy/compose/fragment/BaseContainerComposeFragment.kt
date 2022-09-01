package com.tomy.compose.fragment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import org.koin.androidx.viewmodel.ext.android.viewModel

/**@author Tomy
 * Created by Tomy on 2022/9/1.
 */
abstract class BaseContainerComposeFragment: BaseComposeFragment() {

    protected val mBaseContainerViewModel by viewModel<BaseContainerViewModel>()

    @Composable
    override fun CreateContent() {
        if (mBaseContainerViewModel.topBarVisibility.collectAsState().value) {
            CreateTopContainer()
        }
        if (mBaseContainerViewModel.bottomBarVisibility.collectAsState().value) {
            CreateBottomContainer()
        }
    }

    @Composable
    fun CreateTopContainer() {}

    @Composable
    fun CreateBottomContainer() {}
}