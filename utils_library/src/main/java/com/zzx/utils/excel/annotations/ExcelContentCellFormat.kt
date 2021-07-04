/*
 * Created by Engine100 on 2016-11-30 11:10:00.
 *
 *      https://github.com/engine100
 *
 */
package com.zzx.utils.excel.annotations

/**
 * format the content.
 * usual,you can add it on method which return WritableCellFormat,
 * most times ,it doesn't fit the big picture
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
annotation class ExcelContentCellFormat(val titleName: String)