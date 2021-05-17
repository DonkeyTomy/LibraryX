package com.zzx.utils.rxjava

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.ExecutorService

/**@author Tomy
 * Created by Tomy on 14/5/2021.
 */
object RxJava {

    fun sendMainSingle(consumer: Consumer<in Unit>) {
        Single.just(Unit)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(consumer)
    }

    fun sendSingle(consumer: Consumer<in Unit>) {
        Single.just(Unit)
            .observeOn(Schedulers.io())
            .subscribe(consumer)
    }

    fun sendSingle(consumer: Consumer<in Unit>, exeService: ExecutorService) {
        Single.just(Unit)
            .observeOn(Schedulers.from(exeService))
            .subscribe(consumer)
    }

}