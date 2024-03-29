package com.zzx.utils.rxjava

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.functions.Function
import io.reactivex.rxjava3.schedulers.Schedulers

/**@author Tomy
 * Created by Tomy on 27/11/2020.
 */
object ObservableUtil {

    fun <out>setBackgroundThreadMapMain(map: Function<Unit, out>, mainThreadExec: Consumer<out>) {
        Observable.just(Unit)
            .observeOn(Schedulers.newThread())
            .map(map)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(mainThreadExec, { it.printStackTrace() })
    }

    fun <out>setMainThreadMapBackground(mainThread: Function<Unit, out>, newThreadExec: Consumer<out>) {
        Observable.just(Unit)
            .observeOn(AndroidSchedulers.mainThread())
            .map(mainThread)
            .observeOn(Schedulers.newThread())
            .subscribe(newThreadExec, { it.printStackTrace() })
    }

    fun <C>setBackgroundThread(map: Function<Unit, C>, onNext: Consumer<C>) {
        Observable.just(Unit)
            .observeOn(Schedulers.newThread())
            .map(map)
            .subscribe(onNext, { it.printStackTrace() })
    }

    fun setMainThread(mainThreadExec: Consumer<Unit>) {
        Observable.just(Unit)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(mainThreadExec, { it.printStackTrace() })
    }

    fun setBackgroundThread(onNext: Consumer<Unit>) {
        Single.just(Unit)
            .observeOn(Schedulers.newThread())
            .subscribe(onNext, { it.printStackTrace() })
    }

    inline fun <reified Out> changeIoToMainThread(crossinline function: () -> Out?): Observable<Out> {
        return Observable.create<Out> {
            val v = function()
            if (v == null) {
                it.onComplete()
            } else {
                it.onNext(v)
            }
        }.compose(RxThreadUtil.observableIoToMain())
    }

}

fun <T> Observable<T>.retryWithDelayMillis(maxRetries: Int = 2, retryDelayMills: Int = 500): Observable<T> {
    return this.retryWhen(ObservableRetry(maxRetries, retryDelayMills))
}