package com.tomy.compose.components.custom

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.statusBarsPadding
import com.tomy.compose.theme.LocalIconColor
import timber.log.Timber

@Composable
fun CustomTopBar(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    @StringRes titleRes: Int,
    navigationVisible: Boolean = true,
    @DrawableRes navigationIcon: Int? = null,
    navigationOnClick: () -> Unit,
    @DrawableRes actionIcon: Int? = null,
    @StringRes actionMsg: Int? = null,
    actionOnClick: () -> Unit = {},
) {
    if (isVisible) {
        CenterAlignedTopAppBar(
            modifier = modifier.statusBarsPadding(),
            title = {
                Text(
                    text = stringResource(
                        id = titleRes
                    )
                )
            },
            navigationIcon = {
                if (navigationVisible && navigationIcon != null) {
                    Icon(
                        modifier = Modifier.clickable {
                            navigationOnClick()
                        },
                        painter = painterResource(id = navigationIcon),
                        contentDescription = ""
                    )
                }
            },
            actions = {
                IconOrText(
                    modifier = Modifier.padding(end = 5.dp),
                    msg = actionMsg,
                    icon = actionIcon,
                    tint = LocalIconColor.current,
                    onClick = actionOnClick
                )
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
fun TabTopBar(
    modifier: Modifier = Modifier,
    list: Array<String>,
    isShow: Boolean = true,
    onTabSelect: (Int) -> Unit,
    selectIndex: Int
) {
    Timber.d("isShow = $isShow")
    if (isShow) {
//        var selectIndex by remember { mutableStateOf(0) }
        TabRow(
            selectedTabIndex = selectIndex,
            modifier = modifier,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectIndex]),
                    height = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        ) {
            list.forEachIndexed { index, s ->
                Tab(
                    selected = selectIndex == index,
                    onClick = {
                        if (selectIndex != index) {
//                            selectIndex = index
                            onTabSelect(index)
                        }
                    },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = Color.Black
                ) {
                    Text(
                        text = s,
                        modifier = Modifier.padding(
                            top = 8.dp,
                            bottom = 8.dp
                        ),
                        style = MaterialTheme.typography.titleLarge,
                        color = LocalContentColor.current
                    )

                }
            }
        }
    }
}