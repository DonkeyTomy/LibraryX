/*
 * Created by Engine100 on 2016-11-30 11:12:25.
 *
 *      https://github.com/engine100
 *
 */
package com.zzx.utils.excel

import com.zzx.utils.excel.annotations.ExcelContent
import com.zzx.utils.excel.annotations.ExcelContentCellFormat
import com.zzx.utils.excel.annotations.ExcelSheet
import com.zzx.utils.excel.annotations.ExcelTitleCellFormat
import jxl.Sheet
import jxl.Workbook
import jxl.write.Label
import jxl.write.WritableCellFormat
import jxl.write.WritableWorkbook
import timber.log.Timber
import java.io.*
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.*
import kotlin.collections.ArrayList

/**
 * import from excel to class or export beans to excel
 */
class ExcelManager {
    var fieldCache = HashMap<String, Field>()
    private var contentMethodsCache: Map<String, Method>? = null
    private val titleCache: MutableMap<Int, String?> = HashMap()

    /**
     * write excel to only one sheet, no format
     */
    @Throws(Exception::class)
    fun toExcel(excelStream: OutputStream, dataList: List<*>?): Boolean {
        if (dataList.isNullOrEmpty()) {
            return false
        }
        val dataType: Class<*> = dataList[0]!!.javaClass
        val sheetName = getSheetName(dataType)
        val keys = getKeys(dataType)
        var workbook: WritableWorkbook? = null
        try {

            // create one book
            workbook = Workbook.createWorkbook(excelStream)
            // create sheet
            val sheet = workbook.createSheet(sheetName, 0)

            // add titles
            for (x in keys.indices) {
                sheet.addCell(Label(x, 0, keys[x].title))
            }
            fieldCache.clear()
            // add data
            for (y in dataList.indices) {
                for (x in keys.indices) {
                    val fieldName = keys[x].fieldName
                    val field = getField(dataType, fieldName)
                    val value = field[dataList[y]]
                    val content = value?.toString() ?: ""

                    // below the title ,the data begin from y+1
                    sheet.addCell(Label(x, y + 1, content))
                }
            }
//            workbook.write();
//            workbook.close();
//            excelStream.close();
        } catch (e: Exception) {
            throw e
        } finally {
            if (workbook != null) {
                try {
                    workbook.write()
                    workbook.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            try {
                excelStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return true
    }

    @Throws(Exception::class)
    fun toExcel(fileAbsoluteName: String, dataList: List<*>?): Boolean {
        return toExcel(File(fileAbsoluteName), dataList)
    }

    @Throws(Exception::class)
    fun toExcel(file: File, dataList: List<*>?): Boolean {
        if (file.exists()) {
            if (file.isDirectory) {
                return false
            }
        }
        val folder = file.parentFile
        if (!folder.exists()) {
            folder.mkdirs()
        }
        val stream: OutputStream = FileOutputStream(file, false)
        return toExcel(stream, dataList)
    }

    /**
     * write excel ,only one sheet ,with format
     */
    @Throws(Exception::class)
    fun toExcelWithFormat(excelStream: OutputStream, dataList: List<*>?): Boolean {
        if (dataList.isNullOrEmpty()) {
            return false
        }
        val dataType: Class<*> = dataList[0]!!.javaClass
        val sheetName = getSheetName(dataType)
        val keys = getKeys(dataType)

        // create one book
        val workbook = Workbook.createWorkbook(excelStream)
        // create sheet
        val sheet = workbook.createSheet(sheetName, 0)

        // add titles
        // find title format
        val titleFormats = getTitleFormat(dataType)
        for (x in keys.indices) {
            val titleName = keys[x].title
            val f = titleFormats[titleName]
            if (f != null) {
                sheet.addCell(Label(x, 0, titleName, f))
            } else {
                sheet.addCell(Label(x, 0, titleName))
            }
        }
        fieldCache.clear()
        // add data
        for (y in dataList.indices) {
            for (x in keys.indices) {
                // current data
                val data = dataList[y]!!
                val (title, fieldName) = keys[x]

                // add content
                val field = getField(dataType, fieldName)
                val value = field[data]
                val content = value?.toString() ?: ""

                // add format
                val contentFormat = getContentFormat(title, data)

                // below the title ,the data begin from y+1
                if (contentFormat != null) {
                    sheet.addCell(Label(x, y + 1, content, contentFormat))
                } else {
                    sheet.addCell(Label(x, y + 1, content))
                }
            }
        }
        workbook.write()
        workbook.close()
        excelStream.close()
        return true
    }

    /**
     * find all titles' WritableCellFormat
     */
    @Throws(Exception::class)
    private fun getTitleFormat(clazz: Class<*>): Map<String, WritableCellFormat> {
        val titleFormat: MutableMap<String, WritableCellFormat> = HashMap()
        val methods = clazz.declaredMethods
        for (m in methods.indices) {
            val method = methods[m]
            val formatAnno = method.getAnnotation(ExcelTitleCellFormat::class.java) ?: continue
            method.isAccessible = true
            var format: WritableCellFormat? = null
            format = try {
                method.invoke(null) as WritableCellFormat
            } catch (e: Exception) {
                throw Exception("The method added ExcelTitleCellFormat must be the static method")
            }
            if (format != null) {
                val title = formatAnno.titleName
                titleFormat[title] = format
            }
        }
        return titleFormat
    }

    /**
     * find all methods with ExcelContentCellFormat
     */
    private fun getContentFormatMethods(clazz: Class<*>): Map<String, Method> {
        val contentMethods: MutableMap<String, Method> = HashMap()
        val methods = clazz.declaredMethods
        for (m in methods.indices) {
            val method = methods[m]
            val formatAnno = method.getAnnotation(ExcelContentCellFormat::class.java) ?: continue
            contentMethods[formatAnno.titleName] = method
        }
        return contentMethods
    }

    private fun <T> getContentFormat(title: String, data: T): WritableCellFormat? {
        if (contentMethodsCache == null) {
            contentMethodsCache = getContentFormatMethods(data!!::class.java)
        }
        val method = contentMethodsCache!![title] ?: return null
        method.isAccessible = true
        var format: WritableCellFormat? = null
        try {
            format = method.invoke(data) as WritableCellFormat
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return format
    }

    private fun getKeys(clazz: Class<*>): List<ExcelClassKey> {
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
    private fun getField(type: Class<*>, fieldName: String): Field {
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

    private fun getSheetName(clazz: Class<*>): String {
        val sheet = clazz.getAnnotation(ExcelSheet::class.java)
            ?: throw RuntimeException(clazz.simpleName + " : lost sheet name!")
        return sheet.sheetName
    }

    /**
     * read excel ,it is usual read by sheet name
     * the sheet name must as same as the ExcelSheet annotation's sheetName on dataType
     */
    @Throws(Exception::class)
    fun <T> fromExcel(excelStream: InputStream, dataType: Class<T>): ArrayList<T>? {
        val sheetName = getSheetName(dataType)
        Timber.v("sheetName = $sheetName")
        // read map in excel
        val titleContentValues = getMapFromExcel(excelStream, sheetName)
        if (titleContentValues.isNullOrEmpty()) {
            return null
        }
        val value0 = titleContentValues[0]
        Timber.v("titleContent.size() = ${titleContentValues.size}: $value0")
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
        for (titleContent in titleContentValues) {
            val data = dataType.newInstance()
            for (k in keys.indices) {
                val title = keys[k].title
                val fieldName = keys[k].fieldName
                val field = getField(dataType, fieldName)
                if (field[data] is Int) {
                    field[data] = titleContent[title]!!.toInt()
                } else {
                    field[data] = titleContent[title]
                }
            }
            list.add(data)
        }
        return list
    }

    @Throws(Exception::class)
    fun <T> fromExcel(file: File, dataType: Class<T>): ArrayList<T>? {
        return fromExcel(FileInputStream(file), dataType)
    }

    @Throws(Exception::class)
    fun <T> fromExcel(filePath: String, dataType: Class<T>): ArrayList<T>? {
        return fromExcel(FileInputStream(filePath), dataType)
    }

    /**
     * read excel by map
     */
    @Throws(Exception::class)
    fun getMapFromExcel(
        excelStream: InputStream,
        sheetName: String
    ): List<Map<String, String>>? {
        val workBook = Workbook.getWorkbook(excelStream)
        val sheet = workBook.getSheet(sheetName)

        // row num
        val yNum = sheet.rows
        // there is only tile or nothing
        if (yNum <= 1) {
            return null
        }
        // column num
        val xNum = sheet.columns

        // none column
        if (xNum <= 0) {
            return null
        }
        val values: MutableList<Map<String, String>> = LinkedList()
        titleCache.clear()

        // yNum-1 is the data size , but not title
        for (y in 0 until yNum - 1) {
            val value: MutableMap<String, String> = LinkedHashMap()
            for (x in 0 until xNum) {
                //read title name
                val title = getExcelTitle(sheet, x)

                //read data,from second row
                val content = getContent(sheet, x, y + 1)
                title?.let {
                    value[it] = content
                }
            }
            values.add(value)
        }
        workBook.close()
        return values
    }

    private fun getExcelTitle(sheet: Sheet, x: Int): String? {
        val title: String?
        if (titleCache.containsKey(x)) {
            title = titleCache[x]
        } else {
            title = getContent(sheet, x, 0)
            titleCache[x] = title
        }
        return title
        // return getContent(sheet, x, 0);
    }

    private fun getContent(sheet: Sheet, x: Int, y: Int): String {
        val contentCell = sheet.getCell(x, y)
        val content = contentCell.contents
        return content ?: ""
    }
}