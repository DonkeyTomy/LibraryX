package com.seanproctor.datatable.annotation

import androidx.annotation.IntDef

/**@author Tomy
 * Created by Tomy on 2023/9/18.
 */

@Target(AnnotationTarget.CLASS)
annotation class TabColumnClass

@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.FIELD
)
annotation class TabColumn(
    val widthType: @TabType Int = TabWidthType.WRAP,
    val value: Float = 0f,
    val index: Int = 0,
)

@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.TYPE)
@IntDef(
    TabWidthType.WRAP,
    TabWidthType.FLEX,
    TabWidthType.FIXED,
    TabWidthType.FRACTION,
    TabWidthType.MIN,
    TabWidthType.MAX,
)
annotation class TabType

object TabWidthType {
    const val WRAP  = 0
    const val FLEX  = 1
    const val FIXED = 2
    const val FRACTION  = 3
    const val MIN   = 4
    const val MAX   = 5
}
