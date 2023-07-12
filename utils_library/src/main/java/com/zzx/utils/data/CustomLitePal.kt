package com.zzx.utils.data

import com.zzx.utils.rxjava.fixedThread
import org.litepal.crud.LitePalSupport
import timber.log.Timber


/**@author Tomy
 * Created by Tomy on 4/9/2020.
 */
open class CustomLitePal: LitePalSupport() {

    open fun saveData(key: String, value: String) = fixedThread {
        Timber.v("saveSuccess: ${saveOrUpdate("$key = ?", value)}")
    }

    open fun saveUniqueData() {
        saveData("id", "0")
    }

    open fun saveData() = fixedThread {
        save()
    }
}