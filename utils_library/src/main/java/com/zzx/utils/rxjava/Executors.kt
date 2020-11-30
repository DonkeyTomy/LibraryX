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
import io.reactivex.rxjava3.functions.Consumer
import timber.log.Timber
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

fun <T> Observable<T>.toSubscribe(observer: Consumer<in T>, onError: Consumer<in Throwable>? = null, lifecycle: LifecycleOwner? = null) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        if (lifecycle != null) {
            autoDispose(lifecycle)
                .subscribe(observer, onError)
            Timber.d("autoDispose()")
            return
        }
    }
    subscribe(observer, onError)
}

fun <T> Observable<T>.toComposeSubscribe(observer: Consumer<in T>, onError: Consumer<in Throwable>? = null, lifecycle: LifecycleOwner? = null) {
    Timber.d("compose()")
    if (Looper.myLooper() == Looper.getMainLooper()) {
        if (lifecycle != null) {
            compose(RxThreadUtil.observableIoToMain())
                .autoDispose(lifecycle)
                .subscribe(observer, onError)
            Timber.d("autoDispose()")
            return
        }
    }
    compose(RxThreadUtil.observableIoToMain())
        .subscribe(observer, onError)
}