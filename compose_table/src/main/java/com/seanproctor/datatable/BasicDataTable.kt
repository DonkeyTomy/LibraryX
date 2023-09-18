/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.seanproctor.datatable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.constrain
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Layout model that arranges its children into rows and columns.
 */
@Composable
fun BasicDataTable(
    columns: List<DataColumn>,
    modifier: Modifier = Modifier,
    separator: @Composable (rowIndex: Int) -> Unit = { },
    headerHeight: Dp = 56.dp,
    rowHeight: Dp = 52.dp,
    horizontalPadding: Dp = 16.dp,
    footer: @Composable () -> Unit = { },
    isFooterAboveTab: Boolean = false,
    cellContentProvider: CellContentProvider = DefaultCellContentProvider,
    sortColumnIndex: Int? = null,
    sortAscending: Boolean = true,
    headerColor: Color  = Color.Unspecified,
    oddColor: Color     = Color.Unspecified,
    evenColor: Color    = Color.Unspecified,
    content: DataTableScope.() -> Unit
) {
    val tableScope = DataTableScopeImpl()
    val tableContent: @Composable () -> Unit = with(tableScope) {
        apply(content); @Composable {

        columns.forEachIndexed { columnIndex, columnDefinition ->
            with(TableCellScopeImpl(0, columnIndex)) {
                val cellScope = this
                Row(
                    Modifier.tableCell()
                        .padding(horizontal = horizontalPadding)
                        .height(headerHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val sorted = columnIndex == sortColumnIndex
                    cellContentProvider.HeaderCellContent(
                        sorted = sorted,
                        sortAscending = sortAscending,
                        onClick = columnDefinition.onSort?.let {
                            { it(columnIndex, if (sorted) !sortAscending else sortAscending) }
                        }
                    ) {
                        cellScope.(columnDefinition.header)()
                    }
                }
            }
        }

        tableRows.forEachIndexed { rowIndex, rowData ->
            with(TableRowScopeImpl(rowIndex + 1)) {
                rowData.content(this)
                /*if (cells.size > columns.size) {
                    throw RuntimeException("Row ${this.rowIndex} has too many cells.")
                }*/
                /*if (cells.size < columns.size) {
                    throw RuntimeException("Row ${this.rowIndex} doesn't have enough cells.")
                }*/
                cells.forEachIndexed { columnIndex, cellData ->
                    with(TableCellScopeImpl(rowIndex + 1, columnIndex)) {
                        val cellScope = this
                        Row(
                            Modifier.tableCell()
                                .padding(horizontal = horizontalPadding)
                                .height(rowHeight),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            cellContentProvider.RowCellContent {
                                cellData.content(cellScope)
                            }
                        }
                    }
                }
            }
        }
    }
    }

    val separators = @Composable {
        val rows = tableScope.tableRows.size + 1 // table rows + header
        for (row in 0 until rows) {
            separator(row)
        }
    }

    val rowBackgrounds = @Composable {
        tableScope.tableRows.forEachIndexed { index, tableRowData ->
            Box(Modifier
                .fillMaxSize()
                .background(if (index % 2 == 0) evenColor else oddColor)
                .then(if (tableRowData.onClick != null) Modifier.clickable { tableRowData.onClick.invoke() } else Modifier)
            )
        }
    }

    val headerBackground = @Composable {
        Box(Modifier
            .fillMaxSize()
            .background(headerColor)
        )
    }

    val layoutDirection = LocalLayoutDirection.current
    val columnCount = columns.size
    Layout(
        listOf(tableContent, separators, rowBackgrounds, footer, headerBackground),
        modifier
    ) { (contentMeasurables, separatorMeasurables, rowBackgroundMeasurables, footerMeasurable, headerBackgroundMeasurable), constraints ->
        val addIndex = if (isFooterAboveTab) 1 else 0
        val rowMeasurables = contentMeasurables.groupBy { it.rowIndex }
        val rowCount = rowMeasurables.size
        fun measurableAt(row: Int, column: Int) = rowMeasurables[row]?.getOrNull(column)
        val placeables = Array(rowCount) { arrayOfNulls<Placeable>(columnCount) }

        // Compute column widths and collect flex information.
        var totalFlex = 0f
        val columnWidths = Array(columnCount) { 0 }
        var minTableWidth = 0
        var neededColumnWidth = 0
        for (column in 0 until columnCount) {
            val spec = columns[column].width
            val cells = List(rowCount) { row ->
                TableMeasurable(
                    preferredWidth = {
                        placeables[row][column]?.width
                            ?: measurableAt(row, column)?.measure(Constraints())
                                ?.also { placeables[row][column] = it }?.width ?: 0
                    },
                    minIntrinsicWidth = {
                        val height = if (row == 0) {
                            headerHeight.roundToPx()
                        } else {
                            rowHeight.roundToPx()
                        }
                        measurableAt(row, column)?.minIntrinsicWidth(height) ?: 0
                    },
                    maxIntrinsicWidth = {
                        val height = if (row == 0) {
                            headerHeight.roundToPx()
                        } else {
                            rowHeight.roundToPx()
                        }
                        measurableAt(row, column)?.maxIntrinsicWidth(height) ?: 0
                    }
                )
            }
            minTableWidth += spec.minIntrinsicWidth(
                cells,
                constraints.maxWidth,
                this,
                constraints.maxHeight
            )
            columnWidths[column] = spec.preferredWidth(cells, constraints.maxWidth, this)
            neededColumnWidth += columnWidths[column]
            totalFlex += spec.flexValue
        }
        val availableSpace =
            if (constraints.maxWidth == Constraints.Infinity) constraints.maxWidth else max(
                constraints.minWidth,
                minTableWidth
            )
        val remainingSpace = availableSpace - neededColumnWidth

        // Grow flexible columns to fill available horizontal space.
        if (totalFlex > 0 && remainingSpace > 0) {
            for (column in 0 until columnCount) {
                val spec = columns[column].width
                if (spec.flexValue > 0) {
                    columnWidths[column] += (remainingSpace * (spec.flexValue / totalFlex)).roundToInt()
                }
            }
        }

        // Measure the remaining children and calculate row heights.
        val rowHeights = Array(rowCount) { 0 }
        for (row in 0 until rowCount) {
            for (column in 0 until columnCount) {
                if (placeables[row][column] == null) {
                    placeables[row][column] = measurableAt(row, column)?.measure(
                        Constraints(minWidth = 0, maxWidth = columnWidths[column])
                    )
                }
                val cellHeight = placeables[row][column]?.height ?: 0
                rowHeights[row] = max(rowHeights[row], cellHeight)
            }
        }

        val columnOffsets = Array(columnCount + 1) { 0 }
        for (column in 0 until columnCount) {
            columnOffsets[column + 1] = columnOffsets[column] + columnWidths[column]
        }

        val footerPlaceable = footerMeasurable.map {
            it.measure(
                Constraints(
                    minWidth = max(
                        constraints.minWidth,
                        columnOffsets[columnCount]
                    )
                )
            )
        }.firstOrNull()

        val tableWidth = listOf(constraints.minWidth, columnOffsets[columnCount], footerPlaceable?.width ?: 0).max()

        val separatorPlaceables = separatorMeasurables.mapIndexed { index, measurable ->
            val separatorPlaceable = measurable.measure(Constraints(minWidth = 0, maxWidth = tableWidth))
            rowHeights[index] += separatorPlaceable.height
            separatorPlaceable
        }

        // Compute row/column offsets.
        val rowOffsets = Array(rowCount + 1) { 0 }
        for (row in 0 until rowCount) {
            rowOffsets[row + 1] = rowOffsets[row] + rowHeights[row]
        }

        val tableHeight = max(constraints.minHeight, rowOffsets[rowCount] + (footerPlaceable?.height ?: 0))

        // TODO(calintat): Do something when these do not satisfy constraints.
        val tableSize = constraints.constrain(IntSize(tableWidth, tableHeight))

        val rowBackgroundPlaceables = rowBackgroundMeasurables.mapIndexed { index, measurable ->
            measurable.measure(
                Constraints(
                    minWidth = tableSize.width,
                    maxWidth = tableSize.width,
                    minHeight = rowHeights[index + 1],
                    maxHeight = rowHeights[index + 1]
                )
            )
        }

        val headerBackgroundPlaceable = headerBackgroundMeasurable.mapIndexed { _, measurable ->
            measurable.measure(
                Constraints(
                    minWidth =  tableSize.width,
                    maxWidth = tableSize.width,
                    minHeight = rowHeights[0],
                    maxHeight = rowHeights[0]
                )
            )
        }
        layout(tableSize.width, tableSize.height) {
            headerBackgroundPlaceable.forEachIndexed { index, placeable ->
                placeable.place(x = 0, y = rowOffsets[index + addIndex])
            }
            // Place backgrounds
            rowBackgroundPlaceables.forEachIndexed { index, placeable ->
                val rowIndex = index + 1 + addIndex // header doesn't have a background
                placeable.place(x = 0, y = rowOffsets[rowIndex])
            }

            for (row in 0 until rowCount) {
                // Place cells
                for (column in 0 until columnCount) {
                    placeables[row][column]?.let {
                        val position = columns[column].alignment.align(
                            it.width, columnWidths[column], layoutDirection
                        )
                        it.place(
                            x = columnOffsets[column] + position,
                            y = rowOffsets[row + addIndex]
                        )
                    }
                }

                // Place separators
                if (separatorPlaceables.size > row) {
                    separatorPlaceables[row].let {
                        it.place(
                            x = 0,
                            y = rowOffsets[row + addIndex] + rowHeights[row] - it.height
                        )
                    }
                }
            }
            footerPlaceable?.place(x = 0, y = rowOffsets[if (isFooterAboveTab) 0 else rowCount])
        }
    }
}