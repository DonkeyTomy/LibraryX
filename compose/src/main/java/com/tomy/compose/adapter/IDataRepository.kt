package com.tomy.compose.adapter

/**@author Tomy
 * Created by Tomy on 2022/9/7.
 */
/**
 * @param Key 获取数据传入的参数
 * @param Result 获取到的数据类型
 */
interface IDataRepository<Key, Result> {

    suspend fun loadData(key: Key?): Result?

}