package com.zzx.utils.rxjava

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableSource
import io.reactivex.rxjava3.functions.Function
import java.util.concurrent.TimeUnit

/**@author Tomy
 * Created by Tomy on 23/8/2021.
 */
class ObservableRetry(val maxRetries: Int, val retryDelayMills: Int):
    Function<Observable<Throwable>, ObservableSource<*>> {

    private var mRetryCount = 0

    override fun apply(t: Observable<Throwable>): ObservableSource<*> {
        return t.flatMap {
            return@flatMap if (++ mRetryCount <= maxRetries) {
                Observable.timer(retryDelayMills.toLong(), TimeUnit.MILLISECONDS)
            } else {
                Observable.error(it)
            }
        }
    }
}