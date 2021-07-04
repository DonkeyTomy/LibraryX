/*
 * Created by Engine100 on 2016-11-30 11:09:14.
 *
 *      https://github.com/engine100
 *
 */
package com.zzx.utils.excel.annotations

/**
 * map to sheet name in excel
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class ExcelSheet(val sheetName: String)