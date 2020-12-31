package com.tomy.lib.ui.adapter

import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.tomy.lib.ui.recycler.BaseViewHolder
import com.zzx.utils.rxjava.ObservableUtil
import com.zzx.utils.rxjava.toSubscribe
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import timber.log.Timber

/**@author Tomy
 * Created by Tomy on 12/9/2020.
 */
class MainRecyclerAdapter<T, DB: ViewDataBinding>: RecyclerView.Adapter<BaseViewHolder<T, DB>> {

    private var mLayoutId = 0

    private var mViewHolderName: Class<*>? = null

    private var mDataBindingName: Class<*>? = null

    constructor(layoutId: Int, viewHolderClassName: Class<*>? = null, dataBindingName: Class<*>, listener: OnItemClickListener<T>? = null) {
        mDataBindingName = dataBindingName
        mLayoutId = layoutId
        mViewHolderName = viewHolderClassName
        listener?.apply {
            mItemClickListener = this
        }
    }

    constructor(dataList: ArrayList<T>, listener: OnItemClickListener<T>? = null) {
        listener?.apply {
            mItemClickListener = this
        }
        setDataList(dataList)
    }

    private var mDataList = ArrayList<T>()

    private var mItemClickListener: OnItemClickListener<T>? = null

    fun setOnItemClickListener(listener: OnItemClickListener<T>) {
        mItemClickListener = listener
    }

    fun setViewHolder(layoutId: Int, viewHolderClassName: Class<*>) {
        mLayoutId = layoutId
        mViewHolderName = viewHolderClassName
    }

    fun setDataList(dataList: List<T>?, needNotify: Boolean = true) {
        ObservableUtil.changeIoToMainThread {
            mDataList.clear()
            if (!dataList.isNullOrEmpty()) {
                mDataList.addAll(dataList)
            }
        }.toSubscribe({
            if (needNotify) {
                notifyDataSetChanged()
            }
        })
    }

    fun clearData(needNotify: Boolean = true) {
        ObservableUtil.changeIoToMainThread {
            mDataList.clear()
        }.toSubscribe({
            if (needNotify) {
                notifyDataSetChanged()
            }
        })
        /*mDataList.clear()
        if (needNotify) {
            Observable.just(Unit)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        notifyDataSetChanged()
                    }
        }*/
    }

    fun addDataList(dataList: List<T>?, needNotify: Boolean = true) {
        if (!dataList.isNullOrEmpty()) {
            ObservableUtil.changeIoToMainThread {
                mDataList.addAll(dataList)
            }.toSubscribe({
                if (needNotify) {
                    notifyDataSetChanged()
                }
            })
            /*mDataList.addAll(dataList)
            Observable.just(Unit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    notifyDataSetChanged()
                }*/
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

    fun getItemInfo(adapterPosition: Int): T? {
        return if (adapterPosition >= itemCount) {
            null
        } else {
            mDataList[adapterPosition]
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T, DB> {
//        Timber.d("onCreateViewHolder()")
        return BaseViewHolder.instantiateDataBind(mLayoutId, parent.context, parent, mViewHolderName!!, mDataBindingName!!)

    }

    override fun onBindViewHolder(holder: BaseViewHolder<T, DB>, position: Int) {
//        Timber.d("onBindViewHolder()")
        mDataList.apply {
            if (size > position) {
                holder.apply {
                    val data = get(position)
                    setData(data, position)
                    itemView.setOnClickListener {
                        mItemClickListener?.onItemClick(it, position, data)
                    }
                }
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        recyclerView.setOnKeyListener { _, keyCode, event ->
            Timber.d("keyCode = $keyCode")
            if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN || keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
    }


    interface OnItemClickListener<T> {
        fun onItemClick(view: View, position: Int, data: T)
    }
}