package com.zzx.utils.rxjava

import android.os.Looper
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.ExecutorService

/**@author Tomy
 * Created by Tomy on 14/5/2021.
 */
object RxJava {

    /**
     *
     * @param consumer Consumer<Unit>
     * @param onError Consumer<Throwable>
     * @param needPost Boolean 在当前是UI线程时是直接执行还是调用订阅发送信息
     * @return Disposable
     */
    fun sendMainSingle(consumer: Consumer<Unit>, onError: Consumer<Throwable> = Consumer { it.printStackTrace() }, needPost: Boolean = false): Disposable {
        if (!needPost && Looper.myLooper() == Looper.getMainLooper()) {
            consumer.accept(Unit)
            return Disposable.empty()
        }
        return Single.just(Unit)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(consumer, onError)
    }

    fun sendIoSingle(consumer: Consumer<Unit>, onError: Consumer<Throwable> = Consumer { it.printStackTrace() }): Disposable {
        return Single.just(Unit)
            .observeOn(Schedulers.io())
            .subscribe(consumer, onError)
    }

    fun sendBackgroundSingle(consumer: Consumer<Unit>, onError: Consumer<Throwable> = Consumer { it.printStackTrace() }): Disposable {
        return Single.just(Unit)
            .observeOn(Schedulers.newThread())
            .subscribe(consumer, onError)
    }

    fun sendSingle(consumer: Consumer<Unit>, exeService: ExecutorService, onError: Consumer<Throwable> = Consumer { it.printStackTrace() }): Disposable {
        return Single.just(Unit)
            .observeOn(Schedulers.from(exeService))
            .subscribe(consumer, onError)
    }

}