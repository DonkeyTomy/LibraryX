package com.zzx.utils.data

import com.zzx.utils.rxjava.fixedThread
import org.litepal.crud.LitePalSupport


/**@author Tomy
 * Created by Tomy on 4/9/2020.
 */
open class CustomLitePal: LitePalSupport() {

    fun saveData(key: String, value: String) = fixedThread {
        saveOrUpdate("$key like ?", value)
    }

    fun saveUniqueData() {
        saveData("id", "0")
    }

    fun saveData() = fixedThread {
        save()
    }
}