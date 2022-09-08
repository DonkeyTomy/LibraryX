package com.tomy.compose.adapter

import androidx.paging.PagingSource
import androidx.paging.PagingState

/**@author Tomy
 * Created by Tomy on 2022/9/7.
 */
abstract class IPageDataSource<Key: Any, T: Any, Result: BaseResult<T>>(val iDataRepository: IDataRepository<Key, T, Result>): PagingSource<Key, T>() {

    override suspend fun load(params: LoadParams<Key>): LoadResult<Key, T> {
        return try {
            val currentKey = params.key
            val resultData = iDataRepository.loadData(currentKey)
            if (resultData == null || resultData.getResult()?.isEmpty() == true) {
                LoadResult.Invalid()
            } else {
                LoadResult.Page(
                    data = resultData.getResult()!!,
                    nextKey = getNextKey(currentKey, resultData),
                    prevKey = getPreKey(currentKey)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }

    abstract fun getNextKey(
        currentKey: Key?,
        resultData: Result?
    ): Key

    abstract fun getPreKey(currentKey: Key?): Key?


}