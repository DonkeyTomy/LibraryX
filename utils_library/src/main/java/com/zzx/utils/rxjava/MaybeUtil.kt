package com.zzx.utils.rxjava

import io.reactivex.rxjava3.core.Maybe

/**@author Tomy
 * Created by Tomy on 19/8/2021.
 */
object MaybeUtil

fun <T> Maybe<T>.retryWithDelayMillis(maxRetries: Int = 2, retryDelayMills: Int = 500): Maybe<T> {
    return this.retryWhen(RetryWithDelay(maxRetries, retryDelayMills))
}