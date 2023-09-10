package com.seanproctor.datatable

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import com.tomy.compose.theme.LocalTitleColor
import com.tomy.compose.theme.LocalTitleTextStyle

data class DataColumn(
    val text: String,
    val width: TableColumnWidth = TableColumnWidth.Flex(1f),
    val alignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    val onSort: ((columnIndex: Int, ascending: Boolean) -> Unit)? = null,
    val header: @Composable TableCellScope.() -> Unit = {
        Text(
            text = text,
            style = LocalTitleTextStyle.current,
            color = LocalTitleColor.current,
            textAlign = TextAlign.Center
        )
    }
)
