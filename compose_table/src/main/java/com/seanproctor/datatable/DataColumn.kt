package com.seanproctor.datatable

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign

data class DataColumn(
    val text: String,
    val width: TableColumnWidth = TableColumnWidth.Flex(1f),
    val alignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    val onSort: ((columnIndex: Int, ascending: Boolean) -> Unit)? = null,
    val header: @Composable TableCellScope.() -> Unit = {
        Text(
            text = text,
            style = LocalTextStyle.current,
            color = LocalContentColor.current,
            textAlign = TextAlign.Center
        )
    }
)
