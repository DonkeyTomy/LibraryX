package com.tomy.lib.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.tomy.lib.ui.recycler.BaseViewBindHolder
import com.zzx.utils.rxjava.ObservableUtil
import com.zzx.utils.rxjava.toSubscribe
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable

/**@author Tomy
 * Created by Tomy on 11/1/2021.
 */
class RecyclerViewBindAdapter<T, VB: ViewBinding>: RecyclerView.Adapter<BaseViewBindHolder<T, VB>> {


    private var mViewHolderClass: Class<*>? = null

    private var mViewBindingClass: Class<*>? = null

    constructor(viewHolderClass: Class<*>? = null, viewBindingClass: Class<*>? = null, listener: OnItemClickListener<T>? = null) {
        mViewBindingClass = viewBindingClass
        mViewHolderClass = viewHolderClass
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

    fun setViewHolder(viewBindingClass: Class<VB>, viewHolderClassName: Class<out BaseViewBindHolder<T, VB>>) {
        mViewHolderClass = viewHolderClassName
        mViewBindingClass = viewBindingClass
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewBindHolder<T, VB> {
//        Timber.d("onCreateViewHolder()")
        return BaseViewBindHolder.instantiateViewBind(parent.context, parent, mViewHolderClass!!, mViewBindingClass!!)

    }

    override fun onBindViewHolder(holder: BaseViewBindHolder<T, VB>, position: Int) {
//        Timber.d("onBindViewHolder()")
        mDataList.apply {
            if (size > position) {
                holder.apply {
                    val data = get(position)
                    setData(data)
                    itemView.setOnClickListener {
                        mItemClickListener?.onItemClick(it, position, data)
                    }
                }
            }
        }
    }

    /*override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        recyclerView.setOnKeyListener { _, keyCode, event ->
            Timber.d("keyCode = $keyCode")
            if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN || keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
    }*/


    interface OnItemClickListener<T> {
        fun onItemClick(view: View, position: Int, data: T)
    }
}