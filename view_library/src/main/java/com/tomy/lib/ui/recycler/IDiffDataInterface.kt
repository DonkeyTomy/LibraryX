package com.tomy.lib.ui.recycler

import com.tomy.lib.ui.recycler.data.ItemSelectConfig

/**@author Tomy
 * Created by Tomy on 1/3/2021.
 */
interface IDiffDataInterface<T> {

    /**
     * 用来判断是否是同一个Item
     * @return T
     */
    fun getCheckFlag(): T

    fun getItemSelectConfig(): ItemSelectConfig

}