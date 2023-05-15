/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zzx.utils.rxjava

import android.os.Looper
import androidx.lifecycle.LifecycleOwner
import autodispose2.androidx.lifecycle.autoDispose
import com.zzx.utils.ExceptionHandler
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Action
import io.reactivex.rxjava3.functions.Consumer
import java.util.concurrent.Executors

private val IO_EXECUTOR = Executors.newSingleThreadExecutor()
private val FIXED_EXECUTOR = Executors.newFixedThreadPool(3)

/**
 * Utility method to run blocks on a dedicated background thread, used for io/database work.
 */
fun singleThread(f : () -> Unit) {
    IO_EXECUTOR.execute(f)
}

fun fixedThread(f: () -> Unit) {
    FIXED_EXECUTOR.execute(f)
}

fun <T: Any> Observable<T>.toSubscribe(observer: Consumer<in T> = Consumer {  },
                                  onError: Consumer<in Throwable> = Consumer { it.printStackTrace() },
                                  lifecycle: LifecycleOwner? = null, onCompletion: Action = Action {},
    needRetry: Boolean = false) {
    if (needRetry) {
        retryWithDelayMillis()
    }
    if (Looper.myLooper() == Looper.getMainLooper()) {
        if (lifecycle != null) {
            autoDispose(lifecycle)
                .subscribe(observer, HttpRxJava(onError), onCompletion)
//            Timber.d("autoDispose()")
            return
        }
    }
    subscribe(observer, HttpRxJava(onError), onCompletion)
}

fun <T: Any> Observable<T>.toComposeSubscribe(observer: Consumer<in T> = Consumer {  },
                                         onError: Consumer<in Throwable> = Consumer { it.printStackTrace() },
                                         lifecycle: LifecycleOwner? = null, onCompletion: Action = Action {},
    needRetry: Boolean = false): Disposable {
    if (needRetry) {
        retryWithDelayMillis()
    }
//    Timber.v("compose()")
    if (Looper.myLooper() == Looper.getMainLooper()) {
        if (lifecycle != null) {
            return compose(RxThreadUtil.observableIoToMain())
                .autoDispose(lifecycle)
                .subscribe(observer, HttpRxJava(onError), onCompletion)
//            Timber.v("autoDispose()")
        }
    }
    return compose(RxThreadUtil.observableIoToMain())
        .subscribe(observer, HttpRxJava(onError), onCompletion)
}

fun <T: Any> Single<T>.toSubscribe(observer: Consumer<in T> = Consumer {  },
                                  onError: Consumer<in Throwable> = Consumer { it.printStackTrace() },
                                  lifecycle: LifecycleOwner? = null) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        if (lifecycle != null) {
            autoDispose(lifecycle)
                .subscribe(observer, HttpRxJava(onError))
            return
        }
    }
    subscribe(observer, HttpRxJava(onError))
}

fun <T: Any> Single<T>.toComposeSubscribe(observer: Consumer<in T> = Consumer {  },
                                         onError: Consumer<in Throwable> = Consumer { it.printStackTrace() },
                                         lifecycle: LifecycleOwner? = null): Disposable {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        if (lifecycle != null) {
            return compose(RxThreadUtil.singleIoToMain())
                .autoDispose(lifecycle)
                .subscribe(observer, HttpRxJava(onError))
        }
    }
    return compose(RxThreadUtil.singleIoToMain())
        .subscribe(observer, HttpRxJava(onError))
}

fun <T: Any> Maybe<T>.toSubscribe(observer: Consumer<in T> = Consumer {  },
    onError: Consumer<in Throwable> = Consumer { it.printStackTrace() },
    lifecycle: LifecycleOwner? = null, onCompletion: Action = Action {}, needRetry: Boolean = false) {
    if (needRetry) {
        retryWithDelayMillis()
    }
    if (Looper.myLooper() == Looper.getMainLooper()) {
        if (lifecycle != null) {
            autoDispose(lifecycle)
                .subscribe(observer, HttpRxJava(onError), onCompletion)
            return
        }
    }
    subscribe(observer, HttpRxJava(onError), onCompletion)
}

fun <T : Any> Maybe<T>.toComposeSubscribe(observer: Consumer<in T> = Consumer {  },
    onError: Consumer<in Throwable> = Consumer { it.printStackTrace() },
    lifecycle: LifecycleOwner? = null, onCompletion: Action = Action {}, needRetry: Boolean = false): Disposable {
    if (needRetry) {
        retryWithDelayMillis()
    }
    if (Looper.myLooper() == Looper.getMainLooper()) {
        if (lifecycle != null) {
            return compose(RxThreadUtil.maybeIoToMain())
                .autoDispose(lifecycle)
                .subscribe(observer, HttpRxJava(onError), onCompletion)
        }
    }
    return compose(RxThreadUtil.maybeIoToMain())
        .subscribe(observer, HttpRxJava(onError), onCompletion)
}

class HttpRxJava(val consumer: Consumer<in Throwable>? = null): Consumer<Throwable> {

    override fun accept(t: Throwable) {
        if (NEED_SAVE_LOG) {
            ExceptionHandler.getInstance().saveException2File(t)
        }
        consumer?.accept(t)
    }
}

const val NEED_SAVE_LOG = true