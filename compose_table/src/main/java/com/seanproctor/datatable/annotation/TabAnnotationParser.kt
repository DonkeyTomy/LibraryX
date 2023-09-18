package com.seanproctor.datatable.annotation

import androidx.compose.ui.unit.dp
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.TableColumnWidth
import timber.log.Timber

/**@author Tomy
 * Created by Tomy on 2023/9/18.
 */
object TabAnnotationParser {

    fun parseTabColumn(tabColumn: Any): List<DataColumn> {
        val list = ArrayList<DataColumn>()
        val clazz = tabColumn.javaClass
        Timber.v("clazz: ${clazz.name}")
        if (clazz.isAnnotationPresent(TabColumnClass::class.java)) {
            clazz.getDeclaredAnnotation(TabColumnClass::class.java)!!.apply {
                clazz.declaredFields.forEach {
                    Timber.v("field.name: ${it.name}")
                    it.isAccessible = true
                    if (it.isAnnotationPresent(TabColumn::class.java)) {
                        it.getAnnotation(TabColumn::class.java)!!.let { tab ->
                            Timber.v("${tab.index}")
                            val column = DataColumn(
                                index = tab.index,
                                text = it.getInt(tabColumn),
                                width = when (tab.widthType) {
                                    TabWidthType.FLEX   -> TableColumnWidth.Flex(tab.value)
                                    TabWidthType.FIXED   -> TableColumnWidth.Fixed(tab.value.dp)
                                    TabWidthType.FRACTION   -> TableColumnWidth.Fraction(tab.value)
                                    TabWidthType.MAX   -> TableColumnWidth.MaxIntrinsic
                                    TabWidthType.MIN   -> TableColumnWidth.MinIntrinsic
                                    else   -> TableColumnWidth.Wrap
                                }
                            )
                            list.add(column)
                        }
                    } else {
                        Timber.e("not annotation")
                    }
                }
                list.sortBy {
                    it.index
                }
                list.forEach {
                    Timber.v("${it.index}")
                }
            }
        }
        return list
    }

}