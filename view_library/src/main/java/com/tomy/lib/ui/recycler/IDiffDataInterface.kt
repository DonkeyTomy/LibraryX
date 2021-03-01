package com.tomy.lib.ui.recycler

/**@author Tomy
 * Created by Tomy on 1/3/2021.
 */
interface IDiffDataInterface<T> {

    /**
     * 用来判断是否是同一个Item
     * @return T
     */
    fun getCheckFlag(): T

}