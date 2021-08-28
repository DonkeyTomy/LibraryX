package com.zzx.utils.rxjava

import android.os.Looper
import androidx.lifecycle.LifecycleOwner
import autodispose2.androidx.lifecycle.autoDispose
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Action
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.functions.Function
import io.reactivex.rxjava3.schedulers.Schedulers
import org.reactivestreams.Subscriber


/**@author Tomy
 * Created by Tomy on 2018/6/18.
 */
object FlowableUtil {

    fun <out>setBackgroundThreadMapMain(map: Function<Unit, out>, mainThreadExec: Consumer<out>) {
        Single.just(Unit)
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
        Single.just(Unit)
                .observeOn(Schedulers.newThread())
                .subscribe(onNext, { it.printStackTrace() })
    }

    fun <T> Flowable<T>.toSubscribe(observer: Consumer<in T>,
        onError: Consumer<in Throwable> = Consumer { it.printStackTrace() },
        lifecycle: LifecycleOwner? = null, onCompletion: Action = Action {}) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (lifecycle != null) {
                autoDispose(lifecycle)
                    .subscribe(observer, onError, onCompletion)
                return
            }
        }
        subscribe(observer, onError, onCompletion)
    }

    fun <T> Flowable<T>.toComposeSubscribe(observer: Consumer<in T>,
        onError: Consumer<in Throwable> = Consumer { it.printStackTrace() },
        lifecycle: LifecycleOwner? = null, onCompletion: Action = Action {}): Disposable {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (lifecycle != null) {
                return compose(RxThreadUtil.flowableIoToMain())
                    .autoDispose(lifecycle)
                    .subscribe(observer, onError, onCompletion)
            }
        }
        return compose(RxThreadUtil.flowableIoToMain())
            .subscribe(observer, onError, onCompletion)
    }

    fun <T> Flowable<T>.toSubscribe(observer: Subscriber<in T>, lifecycle: LifecycleOwner? = null) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (lifecycle != null) {
                autoDispose(lifecycle)
                    .subscribe(observer)
                return
            }
        }
        subscribe(observer)
    }

    fun <T> Flowable<T>.toComposeSubscribe(observer: Subscriber<in T>,
        lifecycle: LifecycleOwner? = null) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (lifecycle != null) {
                compose(RxThreadUtil.flowableIoToMain())
                    .autoDispose(lifecycle)
                    .subscribe(observer)
            }
        }
        compose(RxThreadUtil.flowableIoToMain())
            .subscribe(observer)
    }

}