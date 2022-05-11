package com.tomy.compose.components.custom

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**@author Tomy
 * Created by Tomy on 2022/1/21.
 */
@Composable
fun <T> GridContent(
    modifier: Modifier = Modifier,
    dataList: List<T>,
    columnCount: Int,
    onItemClick: (Int) -> Unit,
    content: @Composable (T, Modifier) -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(columnCount),
        verticalArrangement = Arrangement.Center,
        horizontalArrangement = Arrangement.Center
    ) {
        itemsIndexed(dataList) { index, item ->
            content(
                item,
                Modifier.clickable { onItemClick(index) }
            )
        }
    }
}