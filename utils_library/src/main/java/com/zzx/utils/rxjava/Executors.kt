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

fun <T> Observable<T>.toSubscribe(observer: Consumer<in T>,
                                  onError: Consumer<in Throwable> = Consumer { it.printStackTrace() },
                                  lifecycle: LifecycleOwner? = null, onCompletion: Action = Action {}) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        if (lifecycle != null) {
            autoDispose(lifecycle)
                .subscribe(observer, onError, onCompletion)
//            Timber.d("autoDispose()")
            return
        }
    }
    subscribe(observer, onError, onCompletion)
}

fun <T> Observable<T>.toComposeSubscribe(observer: Consumer<in T>,
                                         onError: Consumer<in Throwable> = Consumer { it.printStackTrace() },
                                         lifecycle: LifecycleOwner? = null, onCompletion: Action = Action {}): Disposable {
//    Timber.v("compose()")
    if (Looper.myLooper() == Looper.getMainLooper()) {
        if (lifecycle != null) {
            return compose(RxThreadUtil.observableIoToMain())
                .autoDispose(lifecycle)
                .subscribe(observer, onError, onCompletion)
//            Timber.v("autoDispose()")
        }
    }
    return compose(RxThreadUtil.observableIoToMain())
        .subscribe(observer, onError, onCompletion)
}

fun <T> Single<T>.toSubscribe(observer: Consumer<in T>,
                                  onError: Consumer<in Throwable> = Consumer { it.printStackTrace() },
                                  lifecycle: LifecycleOwner? = null) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        if (lifecycle != null) {
            autoDispose(lifecycle)
                .subscribe(observer, onError)
            return
        }
    }
    subscribe(observer, onError)
}

fun <T> Single<T>.toComposeSubscribe(observer: Consumer<in T>,
                                         onError: Consumer<in Throwable> = Consumer { it.printStackTrace() },
                                         lifecycle: LifecycleOwner? = null): Disposable {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        if (lifecycle != null) {
            return compose(RxThreadUtil.singleIoToMain())
                .autoDispose(lifecycle)
                .subscribe(observer, onError)
        }
    }
    return compose(RxThreadUtil.singleIoToMain())
        .subscribe(observer, onError)
}