package com.zzx.utils.excel

import com.zzx.utils.excel.annotations.ExcelContent
import com.zzx.utils.excel.annotations.ExcelSheet
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set

/**@author Tomy
 * Created by Tomy on 19/7/2021.
 */
abstract class AExcelManger: IExcelManager {

    var fieldCache = HashMap<String, Field>()
    protected var contentMethodsCache: Map<String, Method>? = null
    protected val titleCache: MutableMap<Int, String?> = HashMap()

    override fun getKeys(clazz: Class<*>): List<ExcelClassKey> {
        val fields = clazz.declaredFields
        val keys: MutableList<ExcelClassKey> = ArrayList()
        for (field in fields) {
            val content = field.getAnnotation(ExcelContent::class.java)
            if (content != null) {
                keys.add(ExcelClassKey(content.titleName, field.name, content.index))
            }
        }
        //sort to control the title index in excel
        keys.sortWith { (_, _, index_1: Int), (_, _, index_2: Int) -> index_1 - index_2 }
        return keys
    }

    @Throws(Exception::class)
    fun getField(type: Class<*>, fieldName: String): Field {
        val f: Field?
        if (fieldCache.containsKey(fieldName)) {
            f = fieldCache[fieldName]
        } else {
            f = type.getDeclaredField(fieldName)
            fieldCache[fieldName] = f
        }
        f!!.isAccessible = true
        return f
    }

    @Throws(Exception::class)
    override fun toExcel(filePath: String, dataList: List<*>?, needReplaceFile: Boolean): Boolean {
        if (dataList.isNullOrEmpty()) {
            return false
        }
        return toExcel(File(filePath), dataList, needReplaceFile)
    }

    @Throws(Exception::class)
    override fun <T> fromExcel(file: File, dataType: Class<T>): ArrayList<T>? {
        return fromExcel(FileInputStream(file), dataType)
    }

    @Throws(Exception::class)
    override fun <T> fromExcel(filePath: String, dataType: Class<T>): ArrayList<T>? {
        return fromExcel(File(filePath), dataType)
    }

    override fun getSheetName(clazz: Class<*>): String {
        val sheet = clazz.getAnnotation(ExcelSheet::class.java)
                ?: throw RuntimeException(clazz.simpleName + " : lost sheet name!")
        return sheet.sheetName
    }

    /**
     * read excel ,it is usual read by sheet name
     * the sheet name must as same as the ExcelSheet annotation's sheetName on dataType
     */
    @Throws(Exception::class)
    override fun <T> fromExcel(excelStream: InputStream, dataType: Class<T>): ArrayList<T>? {
        val sheetName = getSheetName(dataType)
        Timber.v("sheetName = $sheetName")
        // read map in excel
        val contentList = getMapFromExcel(excelStream, sheetName)
        return getDataFromMap(contentList, dataType)
    }

    override fun <T> getDataFromMap(contentList: List<Map<String, String>>?, dataType: Class<T>): ArrayList<T>? {
        if (contentList.isNullOrEmpty()) {
            return null
        }
        val value0 = contentList[0]
        Timber.v("titleContent.size() = ${contentList.size}: $value0")
        val keys = getKeys(dataType)

        //if there is no ExcelContent annotation in class ,return null
        var isExist = false
        for (kIndex in keys.indices) {
            val title = keys[kIndex].title
            if (value0.containsKey(title)) {
                isExist = true
                break
            }
        }
        if (!isExist) {
            return null
        }
        val list = ArrayList<T>()
        fieldCache.clear()

        // parse data from content
        for (titleContent in contentList) {
            val data = dataType.newInstance()
            for (k in keys.indices) {
                val title = keys[k].title
                val fieldName = keys[k].fieldName
                val field = getField(dataType, fieldName)
                if (field[data] is Int) {
                    field[data] = titleContent[title]!!.toDouble().toInt()
                } else {
                    field[data] = titleContent[title]
                }
            }
            list.add(data)
        }
        return list
    }


}