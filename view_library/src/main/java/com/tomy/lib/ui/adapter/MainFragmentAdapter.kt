package com.tomy.lib.ui.adapter

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.zzx.utils.rxjava.ObservableUtil
import com.zzx.utils.rxjava.toSubscribe
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import timber.log.Timber


/**@author Tomy
 * Created by Tomy on 27/3/2021.
 */
class MainFragmentAdapter(var fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {

    constructor(fragment: Fragment): this(fragment.requireActivity())

    private val mFragmentList = HashMap<String, Fragment>()

    private var mDataList = ArrayList<String>()

    /*fun setDataList(dataList: List<String>?, needNotify: Boolean = true, finish: () -> Unit = {}) {
        Timber.v("setDataList(): size = ${dataList?.size}. oldSize = ${mDataList.size}")
        var diffResult: DiffUtil.DiffResult? = null
        ObservableUtil.changeIoToMainThread {
            if (!dataList.isNullOrEmpty()) {
                if (mDataList.isNotEmpty()) {
                    diffResult = DiffUtil.calculateDiff(BaseDiffCallback(dataList, mDataList), true)
                }
            }
        }.toSubscribe({
            if (needNotify) {
                mDataList.clear()
                if (!dataList.isNullOrEmpty()) {
                    mDataList.addAll(dataList)
                }
                if (diffResult == null) {
                    notifyDataSetChanged()
                } else {
                    diffResult?.dispatchUpdatesTo(this)
                    Timber.v("diffSize = ${mDataList.size}")
                }
            }
            finish.invoke()
        }, {
            finish.invoke()
        })
    }*/

    @SuppressLint("NotifyDataSetChanged")
    fun clearData(needNotify: Boolean = true) {
        ObservableUtil.changeIoToMainThread {
            mDataList.clear()
        }.toSubscribe({
            if (needNotify) {
                notifyDataSetChanged()
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addDataList(dataList: List<String>?, needNotify: Boolean = true, finish: () -> Unit = {}) {
        val index = itemCount
        if (!dataList.isNullOrEmpty()) {
            ObservableUtil.changeIoToMainThread {
                mDataList.addAll(dataList)
            }.toSubscribe({
                if (needNotify) {
                    Timber.v("addDataList(): index = $index, size = ${dataList.size}")
                    if (index == 0) {
                        notifyDataSetChanged()
                    } else {
                        notifyItemRangeInserted(index, dataList.size)
                    }
                }
                finish.invoke()
            }, { finish.invoke() })
        } else {
            finish.invoke()
        }
    }

    fun removeItem(position: Int, needNotify: Boolean = true) {
        mDataList.apply {
            if (position < size) {
                removeAt(position)
                if (needNotify) {
                    Observable.just(Unit)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            notifyItemRemoved(position)
                        }, {})
                }
            }
        }
    }

    fun getDataList() = mDataList

    override fun getItemCount(): Int {
        return mDataList.size
    }

    fun getItem(position: Int): String {
        return mDataList[position]
    }

    fun getFragmentItem(position: Int): Fragment {
        val name = getItem(position)
        return mFragmentList[name] ?: Fragment.instantiate(fragmentActivity, name).apply {
            Timber.v("instantiate: $name")
            mFragmentList[name] = this
        }
    }

    override fun createFragment(position: Int): Fragment {
        Timber.v("createFragment: ${getItem(position)}")
        return getFragmentItem(position)
    }
}