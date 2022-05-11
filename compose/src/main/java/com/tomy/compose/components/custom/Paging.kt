package com.tomy.compose.components.custom

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.tomy.compose.R

@Composable
fun <T: Any> SwipeRefreshList(
    collectAsLazyItems: LazyPagingItems<T>,
    content: LazyListScope.() -> Unit
) {

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = false)
    SwipeRefresh(state = swipeRefreshState,
        onRefresh = {
            collectAsLazyItems.refresh()
        }) {
        swipeRefreshState.isRefreshing = collectAsLazyItems.loadState.refresh is LoadState.Loading

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                }
            }
            content()

            collectAsLazyItems.apply {
                when {
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
    modifier: Modifier = Modifier.padding(10.dp)
) {
    CircularProgressIndicator(modifier)
}