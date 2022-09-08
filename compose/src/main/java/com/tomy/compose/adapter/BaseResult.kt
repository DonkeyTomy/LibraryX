package com.tomy.compose.adapter

/**@author Tomy
 * Created by Tomy on 2022/9/8.
 */
abstract class BaseResult<T> {

    private var mResult: List<T>? = null

    fun getResult() = mResult

    fun setResult(result: List<T>) {
        mResult = result
    }

}