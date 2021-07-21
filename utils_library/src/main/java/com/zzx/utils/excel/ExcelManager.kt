/*
 * Created by Engine100 on 2016-11-30 11:12:25.
 *
 *      https://github.com/engine100
 *
 */
package com.zzx.utils.excel

import com.zzx.utils.excel.annotations.ExcelContentCellFormat
import com.zzx.utils.excel.annotations.ExcelTitleCellFormat
import jxl.Sheet
import jxl.Workbook
import jxl.write.Label
import jxl.write.WritableCellFormat
import jxl.write.WritableWorkbook
import timber.log.Timber
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.lang.reflect.Method
import java.util.*

/**
 * import from excel to class or export beans to excel
 */
class ExcelManager: AExcelManger() {

    /**
     * write excel to only one sheet, no format
     */
    @Throws(Exception::class)
    override fun toExcel(excelStream: OutputStream, dataList: List<*>?): Boolean {
        if (dataList.isNullOrEmpty()) {
            try {
                excelStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }
        val dataType: Class<*> = dataList[0]!!.javaClass
        val sheetName = getSheetName(dataType)
        val keys = getKeys(dataType)
        Timber.d("sheetName = $sheetName")
        Timber.d("keys = $keys")
        val workbook: WritableWorkbook
        try {

            // create one book
            workbook = Workbook.createWorkbook(excelStream)
            // create sheet
            Timber.d("index = ${workbook.numberOfSheets}")
            val sheet = workbook.createSheet(sheetName, workbook.numberOfSheets)
            workbook.sheetNames.forEach {
                Timber.d("sheetName = $it")
            }

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
//                    Timber.d("$fieldName: content = $content")
                    // below the title ,the data begin from y+1
                    sheet.addCell(Label(x, y + 1, content))
                }
            }
            workbook.write()
            workbook.close()
//            excelStream.close();
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                excelStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return true
    }


    @Throws(Exception::class)
    override fun toExcel(file: File, dataList: List<*>?, needReplaceFile: Boolean): Boolean {
        if (dataList.isNullOrEmpty()) {
            return false
        }
        if (file.exists()) {
            if (file.isDirectory) {
                return false
            } else if (needReplaceFile) {
                file.delete()
            }
        } else {
            val folder = file.parentFile
            if (!folder.exists()) {
                folder.mkdirs()
            }
        }
        val dataType: Class<*> = dataList[0]!!.javaClass
        val sheetName = getSheetName(dataType)
        val keys = getKeys(dataType)
        Timber.d("sheetName = $sheetName")
        Timber.d("keys = $keys")
        val workbook: WritableWorkbook
        try {

            // create one book
            workbook = if (file.exists()) Workbook.createWorkbook(file, Workbook.getWorkbook(file)) else Workbook.createWorkbook(file)
            // create sheet
            Timber.d("index = ${workbook.numberOfSheets}")
            val sheet = workbook.createSheet(sheetName, workbook.numberOfSheets)
            workbook.sheetNames.forEach {
                Timber.d("sheetName = $it")
            }

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
                    //                    Timber.d("$fieldName: content = $content")
                    // below the title ,the data begin from y+1
                    sheet.addCell(Label(x, y + 1, content))
                }
            }
            workbook.write()
            workbook.close()
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
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


    /**
     * read excel by map
     */
    @Throws(Exception::class)
    override fun getMapFromExcel(
        excelStream: InputStream,
        sheetName: String,
        file: File?
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
        val values = LinkedList<Map<String, String>>()
        titleCache.clear()

        // yNum-1 is the data size , but not title
        for (y in 0 until yNum - 1) {
            val value = LinkedHashMap<String, String>()
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

    fun getExcelTitle(sheet: Sheet, x: Int): String? {
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

    fun getContent(sheet: Sheet, x: Int, y: Int): String {
        val contentCell = sheet.getCell(x, y)
        val content = contentCell.contents
        return content ?: ""
    }
}