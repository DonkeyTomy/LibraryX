package com.zzx.utils.rxjava

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.functions.Function
import io.reactivex.rxjava3.schedulers.Schedulers


/**@author Tomy
 * Created by Tomy on 2018/6/18.
 */
object FlowableUtil {

    fun <out>setBackgroundThreadMapMain(map: Function<Unit, out>, mainThreadExec: Consumer<out>) {
        Flowable.just(Unit)
                .observeOn(Schedulers.newThread())
                .map(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mainThreadExec, { it.printStackTrace() })
    }

    fun <out>setMainThreadMapBackground(mainThread: Function<Unit, out>, newThreadExec: Consumer<out>) {
        Flowable.just(Unit)
                .observeOn(AndroidSchedulers.mainThread())
                .map(mainThread)
                .observeOn(Schedulers.newThread())
                .subscribe(newThreadExec, { it.printStackTrace() })
    }

    fun <C>setBackgroundThread(map: Function<Unit, C>, onNext: Consumer<C>) {
        Flowable.just(Unit)
                .observeOn(Schedulers.newThread())
                .map(map)
                .subscribe(onNext, { it.printStackTrace() })
    }

    fun setMainThread(mainThreadExec: Consumer<Unit>) {
        Flowable.just(Unit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mainThreadExec, { it.printStackTrace() })
    }

    fun setBackgroundThread(onNext: Consumer<Unit>) {
        Flowable.just(Unit)
                .observeOn(Schedulers.newThread())
                .subscribe(onNext, { it.printStackTrace() })
    }

}