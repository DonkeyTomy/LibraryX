/*
 * Created by Engine100 on 2016-11-30 11:09:14.
 *
 *      https://github.com/engine100
 *
 */
package com.zzx.utils.excel.annotations

/**
 * format the title content,
 * like ExcelContentCellFormat,it is used by method which return WritableCellFormat
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
annotation class ExcelTitleCellFormat(val titleName: String)