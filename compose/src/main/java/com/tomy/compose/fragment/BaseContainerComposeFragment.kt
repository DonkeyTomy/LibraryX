package com.tomy.compose.fragment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tomy.compose.theme.wrapHeight
import org.koin.androidx.viewmodel.ext.android.viewModel

/**@author Tomy
 * Created by Tomy on 2022/9/1.
 * 包含[TopBar],[Body],[BottomBar]三部分的[Fragment]
 * @see CreateTopContainer() 创建顶部TopBar部分
 * @see CreateBodyContainer() 创建主内容部分
 * @see CreateBottomContainer() 创建底部BottomBar部分
 */
abstract class BaseContainerComposeFragment: BaseComposeFragment() {

    protected val mBaseContainerViewModel by viewModel<BaseContainerViewModel>()

    @Composable
    override fun CreateContent() {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (mBaseContainerViewModel.topBarVisibility.collectAsState().value) {
                CreateTopContainer(modifier = Modifier.wrapHeight())
            }
            CreateBodyContainer(modifier = Modifier
                .fillMaxWidth()
                .weight(1f))
            if (mBaseContainerViewModel.bottomBarVisibility.collectAsState().value) {
                CreateBottomContainer(modifier = Modifier.wrapHeight())
            }
        }
    }

    @Composable
    open fun CreateTopContainer(modifier: Modifier = Modifier) {}

    @Composable
    open fun CreateBottomContainer(modifier: Modifier = Modifier) {}

    @Composable
    open fun CreateBodyContainer(modifier: Modifier = Modifier) {}
}