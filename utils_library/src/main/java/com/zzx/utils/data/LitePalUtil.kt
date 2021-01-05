package com.zzx.utils.data

import com.zzx.utils.rxjava.ObservableUtil
import io.reactivex.rxjava3.core.Observable
import org.litepal.crud.LitePalSupport
import org.litepal.extension.saveAll

/**@author Tomy
 * Created by Tomy on 1/12/2020.
 */
fun <T: LitePalSupport> Collection<T>.saveAllBack(): Observable<Boolean> {
    return ObservableUtil.changeIoToMainThread {
        saveAll()
    }
}