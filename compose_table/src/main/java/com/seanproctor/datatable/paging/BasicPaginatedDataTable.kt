package com.seanproctor.datatable.paging

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.seanproctor.datatable.BasicDataTable
import com.seanproctor.datatable.CellContentProvider
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.DataTableScope
import com.seanproctor.datatable.DefaultCellContentProvider

@Composable
fun BasicPaginatedDataTable(
    columns: List<DataColumn>,
    modifier: Modifier = Modifier,
    separator: @Composable (rowIndex: Int) -> Unit = { },
    headerHeight: Dp = 56.dp,
    rowHeight: Dp = 52.dp,
    horizontalPadding: Dp = 16.dp,
    state: PaginatedDataTableState = rememberPaginatedDataTableState(10),
    footer: @Composable () -> Unit = { },
    isFooterAboveTab: Boolean = false,
    cellContentProvider: CellContentProvider = DefaultCellContentProvider,
    sortColumnIndex: Int? = null,
    sortAscending: Boolean = true,
    headerColor: Color = Color.Unspecified,
    oddColor: Color = Color.Unspecified,
    evenColor: Color = Color.Unspecified,
    content: DataTableScope.() -> Unit
) {
    BasicDataTable(
        columns = columns,
        modifier = modifier,
        separator = separator,
        headerHeight = headerHeight,
        rowHeight = rowHeight,
        horizontalPadding = horizontalPadding,
        footer = footer,
        isFooterAboveTab = isFooterAboveTab,
        cellContentProvider = cellContentProvider,
        sortColumnIndex = sortColumnIndex,
        sortAscending = sortAscending,
        headerColor = headerColor,
        oddColor    = oddColor,
        evenColor   = evenColor,
    ) {
        val start = state.pageIndex * state.pageSize
        val scope = PaginatedRowScope(start, start + state.pageSize, this)
        with(scope) {
            content()
        }
        if (state.count != scope.index) {
            state.count = scope.index
        }
    }
}
