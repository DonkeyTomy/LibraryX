package com.tomy.compose.components.custom

import android.content.res.Resources
import android.content.res.TypedArray
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tomy.compose.data.IDataItem

/**@author Tomy
 * Created by Tomy on 2022/1/21.
 */
@Composable
fun <T> VerticalGridContent(
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

@Composable
fun <T: IDataItem> VerticalFixResContent(
    modifier: Modifier = Modifier,
    vararg resList: Int,
    converters: List<(TypedArray, Int)-> Any>,
    columnCount: Int,
    onItemClick: (Int) -> Unit,
    resources: Resources,
    createData: () -> T,
    content: @Composable (T, Modifier) -> Unit
) {
    val dataList = ArrayList<T>()
    resList.forEachIndexed { index, i ->
        val converter = converters[index]
        resources.obtainTypedArray(i).apply {
            if (dataList.isEmpty()) {
                for (j in 0 until length()) {
                    dataList.add(createData())
                }
            }
            for (j in 0 until length()) {
                val data = converter(this, j)
                dataList[j].addItemData(data, index)
            }
            recycle()
        }
    }
    VerticalGridContent(
        modifier = modifier,
        dataList = dataList,
        columnCount = columnCount,
        onItemClick = onItemClick,
        content = content
    )
}

@Composable
fun <T> HorizontalGridContent(
    modifier: Modifier = Modifier,
    dataList: List<T>,
    columnCount: Int,
    onItemClick: (Int) -> Unit,
    content: @Composable (T, Modifier) -> Unit
) {
    LazyHorizontalGrid(
        modifier = modifier,
        rows = GridCells.Fixed(columnCount),
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

@Composable
fun <T: IDataItem> HorizontalFixResContent(
    modifier: Modifier = Modifier.fillMaxSize(),
    vararg resList: Int,
    converters: List<(TypedArray, Int)-> Any>,
    columnCount: Int,
    onItemClick: (Int) -> Unit,
    resources: Resources,
    createData: () -> T,
    content: @Composable (T, Modifier) -> Unit
) {
    val dataList = ArrayList<T>()
    resList.forEachIndexed { index, i ->
        val converter = converters[index]
        resources.obtainTypedArray(i).apply {
            if (dataList.isEmpty()) {
                for (j in 0 until length()) {
                    dataList.add(createData())
                }
            }
            for (j in 0 until length()) {
                val data = converter(this, j)
                dataList[j].addItemData(data, index)
            }
            recycle()
        }
    }
    HorizontalGridContent(
        modifier = modifier,
        dataList = dataList,
        columnCount = columnCount,
        onItemClick = onItemClick,
        content = content
    )
}