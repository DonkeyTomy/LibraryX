/*
 * Created by Engine100 on 2016-11-30 11:09:14.
 *
 *      https://github.com/engine100
 *
 */
package com.zzx.utils.excel.annotations

/**
 * content in excel
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class ExcelContent(
    /**
     * The name link to title in excel
     */
    val titleName: String,
    /**
     * titleIndex in excel
     */
    val index: Int = 0
)