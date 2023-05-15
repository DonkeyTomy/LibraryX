package com.tomy.lib.ui.utils

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.FlowableTransformer
import io.reactivex.rxjava3.core.MaybeTransformer
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.schedulers.Schedulers


/**@author Tomy
 * Created by Tomy on 10/9/2020.
 */
object RxThreadUtils {

    /**
     * Observable 切换到主线程
     * @return ObservableTransformer<T, T>
     */
    @JvmStatic
    fun <T : Any> observableToMain(): ObservableTransformer<T, T> {
        return ObservableTransformer {
            upstream ->
                upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
        }
    }

    @JvmStatic
    fun <T : Any> flowableToMain(): FlowableTransformer<T, T> {
        return FlowableTransformer {
            it.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        }
    }

    @JvmStatic
    fun <T : Any> maybeToMain(): MaybeTransformer<T, T> {
        return MaybeTransformer {
            it.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        }
    }


}