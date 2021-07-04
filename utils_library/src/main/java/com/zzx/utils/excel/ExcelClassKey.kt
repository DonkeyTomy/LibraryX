package com.zzx.utils.excel

/**
 * orm in excel and java bean fields ,
 * if the field in bean has ExcelContent annotation ,it can be export to excel
 * 2017-12-12 add field index by engine100
 */
data class ExcelClassKey(
    /**
     * title in excel
     */
    var title: String,
    /**
     * field Name in java bean
     */
    var fieldName: String,
    /**
     * sort title in excel
     */
    val index: Int
)