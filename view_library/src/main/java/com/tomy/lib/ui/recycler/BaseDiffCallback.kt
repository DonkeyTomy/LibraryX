package com.tomy.lib.ui.recycler

import androidx.recyclerview.widget.DiffUtil
import timber.log.Timber

/**@author Tomy
 * Created by Tomy on 1/3/2021.
 */
/**
 * @param T: IDiffDataInterface<D> 作比较的数据类型
 * @param D 数据[T]中用来判断是否内容相同的数据类型
 * @property mNewDataList List<T>?
 * @property mOldDataList List<T>?
 * @constructor
 */
class BaseDiffCallback<D, T: IDiffDataInterface<D>>(var mNewDataList: List<T>?, var mOldDataList: List<T>?): DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return mOldDataList?.size ?: 0
    }

    override fun getNewListSize(): Int {
        return mNewDataList?.size ?: 0
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldFlag = mOldDataList?.get(oldItemPosition)?.getCheckFlag()
        val newFlag = mNewDataList?.get(newItemPosition)?.getCheckFlag()
        Timber.v("areItemsTheSame(): [$oldFlag] -- [$newFlag]: $oldItemPosition - $newItemPosition")
        return oldFlag == newFlag
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val contentSame = mOldDataList?.get(oldItemPosition)?.equals(mNewDataList?.get(newItemPosition)) ?: false
        Timber.v("contentSame = $contentSame: $oldItemPosition - $newItemPosition")
        return contentSame
    }

}