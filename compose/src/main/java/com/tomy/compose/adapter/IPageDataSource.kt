package com.tomy.compose.adapter

import androidx.paging.PagingSource

/**@author Tomy
 * Created by Tomy on 2022/9/7.
 */
abstract class IPageDataSource<Key: Any, Result: Any>(val iDataRepository: IDataRepository<Key, Result>): PagingSource<Key, Result>() {

    override suspend fun load(params: LoadParams<Key>): LoadResult<Key, Result> {
        val resultData = iDataRepository.loadData(params.key)

    }

}