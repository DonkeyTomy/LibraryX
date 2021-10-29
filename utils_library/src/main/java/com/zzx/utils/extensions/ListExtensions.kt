package com.zzx.utils.extensions

import android.os.Build
import androidx.annotation.RequiresApi
import java.util.concurrent.CompletableFuture

@RequiresApi(Build.VERSION_CODES.N)
fun <T> List<CompletableFuture<T>>.allOf(): CompletableFuture<List<T>> {
    return CompletableFuture.allOf(*this.toTypedArray())
            .thenApply {
                this.map {
                    it.get()
                }
            }
}