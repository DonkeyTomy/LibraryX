package com.tomy.compose.components.custom

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.BottomAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**@author Tomy
 * Created by Tomy on 2022/8/30.
 */
@Composable
fun CustomBottomBar(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    floatingActionButton: @Composable (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit)? = null
) {
    if (isVisible) {
        BottomAppBar(
            actions = actions!!,
            modifier = modifier,
            floatingActionButton = floatingActionButton,
        )
    }
}

@Composable
fun CustomBottomBar(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    content: @Composable (RowScope.() -> Unit)? = null
) {
    if (isVisible) {
        BottomAppBar(
            content = content!!,
            modifier = modifier
        )
    }
}

/**
 * 底部简单的排列按钮选择
 */
@Composable
fun ItemClickBottomBar(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    floatingActionButton: @Composable (() -> Unit)? = null,
    onItemClick: ((Int) -> Unit)? = null,
    titleList: List<String>,
    iconList: List<Int>? = null,
    contentModifier: Modifier,
) {
    CustomBottomBar(
        modifier = modifier,
        isVisible = isVisible,
        floatingActionButton = floatingActionButton
    ) {

    }
}