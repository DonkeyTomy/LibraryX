package com.zzx.utils.rxjava

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.functions.Function
import org.reactivestreams.Publisher
import java.util.concurrent.TimeUnit

/**@author Tomy
 * Created by Tomy on 19/8/2021.
 */
class RetryWithDelay(val maxRetries: Int, val retryDelayMills: Int): Function<Flowable<Throwable>, Publisher<*>> {

    private var mRetryCount = 0

    override fun apply(t: Flowable<Throwable>): Publisher<*> {
        return t.flatMap {
            return@flatMap if (++ mRetryCount <= maxRetries) {
                Flowable.timer(retryDelayMills.toLong(), TimeUnit.MILLISECONDS)
            } else {
                Flowable.error(it)
            }
        }
    }
}