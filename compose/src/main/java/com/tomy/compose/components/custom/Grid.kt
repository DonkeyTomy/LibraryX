package com.tomy.compose.components.custom

import android.content.res.Resources
import android.content.res.TypedArray
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tomy.compose.data.IDataItem

/**@author Tomy
 * Created by Tomy on 2022/1/21.
 */
@Composable
fun <T> VerticalGridContent(
    modifier: Modifier = Modifier,
    dataList: List<T>,
    columnCount: Int,
    onItemClick: ((Int, T) -> Unit?)? = null,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
    content: @Composable (T, Modifier) -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(columnCount),
        contentPadding = contentPadding,
        reverseLayout = reverseLayout,
        flingBehavior = flingBehavior,
        userScrollEnabled = userScrollEnabled,
        verticalArrangement = verticalArrangement,
        horizontalArrangement = horizontalArrangement
    ) {
        itemsIndexed(dataList) { index, item ->
            content(
                item,
                Modifier.clickable {
                    onItemClick?.invoke(index, item)
                }
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
    onItemClick: (Int, T) -> Unit,
    resources: Resources,
    createData: () -> T,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
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
        contentPadding = contentPadding,
        reverseLayout = reverseLayout,
        flingBehavior = flingBehavior,
        userScrollEnabled = userScrollEnabled,
        verticalArrangement = verticalArrangement,
        horizontalArrangement = horizontalArrangement,
        content = content
    )
}

@Composable
fun VerticalFixResIntContent(
    modifier: Modifier = Modifier,
    resArrayId: Int,
    columnCount: Int,
    onItemClick: (Int, Int) -> Unit,
    resources: Resources,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
    content: @Composable (Int, Modifier) -> Unit
) {
    val dataList = ArrayList<Int>()
    resources.obtainTypedArray(resArrayId).apply {
        for (i in 0 until length()) {
            dataList.add(getResourceId(i, 0))
        }
        recycle()
    }
    VerticalGridContent(
        modifier = modifier,
        dataList = dataList,
        columnCount = columnCount,
        onItemClick = onItemClick,
        contentPadding = contentPadding,
        reverseLayout = reverseLayout,
        flingBehavior = flingBehavior,
        userScrollEnabled = userScrollEnabled,
        verticalArrangement = verticalArrangement,
        horizontalArrangement = horizontalArrangement,
        content = content
    )
}

@Composable
fun <T> HorizontalGridContent(
    modifier: Modifier = Modifier,
    dataList: List<T>,
    columnCount: Int,
    onItemClick: (Int) -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
    content: @Composable (T, Modifier) -> Unit
) {
    LazyHorizontalGrid(
        modifier = modifier,
        rows = GridCells.Fixed(columnCount),
        reverseLayout = reverseLayout,
        flingBehavior = flingBehavior,
        contentPadding = contentPadding,
        userScrollEnabled = userScrollEnabled,
        verticalArrangement = verticalArrangement,
        horizontalArrangement = horizontalArrangement
    ) {
        itemsIndexed(dataList) { index, item ->
            content(
                item,
                Modifier.clickable { onItemClick(index) }
            )
        }
    }
}

/**
 *
 * @param modifier Modifier
 * @param resList IntArray
 * @param converters List<Function2<TypedArray, Int, Any>>
 * @param columnCount Int
 * @param onItemClick Function1<Int, Unit>
 * @param resources Resources
 * @param createData Function0<T>
 * @param content [@androidx.compose.runtime.Composable] Function2<T, Modifier, Unit>
 */
@Composable
fun <T: IDataItem> HorizontalFixResContent(
    modifier: Modifier = Modifier.fillMaxSize(),
    vararg resList: Int,
    converters: List<(TypedArray, Int)-> Any>,
    columnCount: Int,
    onItemClick: (Int) -> Unit,
    resources: Resources,
    createData: () -> T,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
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
        contentPadding = contentPadding,
        reverseLayout = reverseLayout,
        flingBehavior = flingBehavior,
        userScrollEnabled = userScrollEnabled,
        verticalArrangement = verticalArrangement,
        horizontalArrangement = horizontalArrangement,
        content = content
    )
}