package com.zzx.utils.excel

import java.io.File
import java.io.InputStream
import java.io.OutputStream

/**@author Tomy
 * Created by Tomy on 19/7/2021.
 */
interface IExcelManager {

    fun toExcel(excelStream: OutputStream, dataList: List<*>?): Boolean

    fun toExcel(file: File, dataList: List<*>?, needReplaceFile: Boolean = false): Boolean

    fun toExcel(filePath: String, dataList: List<*>?, needReplaceFile: Boolean = false): Boolean

    fun <T> fromExcel(excelStream: InputStream, dataType: Class<T>): ArrayList<T>?

    fun <T> fromExcel(filePath: String, dataType: Class<T>): ArrayList<T>?

    fun <T> fromExcel(file: File, dataType: Class<T>): ArrayList<T>?

    fun getSheetName(clazz: Class<*>): String

    fun getMapFromExcel(excelStream: InputStream, sheetName: String, file: File? = null): List<Map<String, String>>?

    fun <T> getDataFromMap(contentList: List<Map<String, String>>?, dataType: Class<T>): ArrayList<T>?

    fun getKeys(clazz: Class<*>): List<ExcelClassKey>

}