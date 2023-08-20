package com.tomy.compose.components.custom

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.pullRefreshIndicatorTransform
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.tomy.compose.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T: Any> SwipeRefreshList(
    collectAsLazyItems: LazyPagingItems<T>,
    content: LazyListScope.() -> Unit
) {

    var refreshing by remember {
        mutableStateOf(false)
    }

    val refreshCope = rememberCoroutineScope()

    fun refresh() = refreshCope.launch {
        refreshing = true
        delay(5000)
        refreshing = false
    }

    val state = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = ::refresh
    )
    val rotation = animateFloatAsState(targetValue = state.progress * 120, label = "refresh")
    Box(
        Modifier.pullRefresh(state)
    ) {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (refreshing.not()) {
                item {
                    ListItem(
                        painter = rememberVectorPainter(image = Icons.Default.ArrowDropDown),
                        text = "Pull down"
                    )
                }
            }
            collectAsLazyItems.apply {
                when (loadState.prepend) {
                    is LoadState.Loading -> {
                        item {
                            LoadingItem()
                        }
                    }
                    is LoadState.Error   -> {
                        item {
                            ErrorItem {
                                retry()
                            }
                        }
                    }
                    else                 -> {}
                }
            }
            content()

            collectAsLazyItems.apply {
                when {
                    /**
                     * 加载更多时,底部显示加载项
                     */
                    /**
                     * 加载更多时,底部显示加载项
                     */
                    loadState.append is LoadState.Loading   -> {
                        item {
                            LoadingItem()
                        }
                    }
                    loadState.append is LoadState.Error     -> {
                        item {
                            ErrorItem {
                                retry()
                            }
                        }
                    }

                    loadState.refresh is LoadState.Error    -> {

                    }
                }
            }
        }

        Surface(
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.TopCenter)
                .pullRefreshIndicatorTransform(state = state)
                .rotate(rotation.value),
            shape = RoundedCornerShape(10.dp),
            color = Color.DarkGray,
            elevation = if (state.progress > 0 || refreshing) 20.dp else 0.dp
        ) {
            Box {
                if (refreshing) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(25.dp),
                        color = Color.White,
                        strokeWidth = 3.dp
                    )
                }
            }
        }
    }

}

@Composable
fun ErrorItem(
    modifier: Modifier = Modifier,
    retry: () -> Unit
) {
    Button(
        modifier = modifier,
        onClick = retry
    ) {
        Text(text = stringResource(id = R.string.retry))
    }
}

@Composable
fun ErrorContent(
    modifier: Modifier = Modifier,
    retry: () -> Unit
) {
    Text(text = stringResource(id = R.string.request_data_error))
    ErrorItem(modifier, retry)
}

@Composable
fun LoadingItem(
    modifier: Modifier = Modifier
) {
    CircularProgressIndicator(modifier.padding(10.dp))
}