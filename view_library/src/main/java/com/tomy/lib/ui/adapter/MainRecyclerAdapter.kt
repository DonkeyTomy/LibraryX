package com.tomy.lib.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.tomy.lib.ui.recycler.BaseViewHolder
import com.zzx.utils.rxjava.ObservableUtil
import com.zzx.utils.rxjava.toSubscribe
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable

/**
 * @property mLayoutId Item的布局Id
 * @param T Item数据类
 * @param DB: ViewDataBinding Item的ViewBinding类
 * @property mViewHolderClass Class<*>?
 * @property mDataBindingClass Class<*>?
 * @property mDataList ArrayList<T>
 * @property mItemClickListener OnItemClickListener<T>?
 */
class MainRecyclerAdapter<T, DB: ViewDataBinding>: RecyclerView.Adapter<BaseViewHolder<T, DB>> {

    private var mLayoutId = 0

    private var mViewHolderClass: Class<out BaseViewHolder<T, DB>>? = null

    private var mDataBindingClass: Class<out DB>? = null

    constructor(layoutId: Int, viewHolderClass: Class<out BaseViewHolder<T, DB>>? = null, dataBindingClass: Class<out DB>, listener: OnItemClickListener<T, DB>? = null) {
        mDataBindingClass = dataBindingClass
        mLayoutId = layoutId
        mViewHolderClass = viewHolderClass
        listener?.apply {
            mItemClickListener = this
        }
    }

    constructor(dataList: ArrayList<T>, listener: OnItemClickListener<T, DB>? = null) {
        listener?.apply {
            mItemClickListener = this
        }
        setDataList(dataList)
    }

    private var mDataList = ArrayList<T>()

    private var mItemClickListener: OnItemClickListener<T, DB>? = null

    private var mItemFocusListener: OnItemFocusListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener<T, DB>) {
        mItemClickListener = listener
    }

    fun setOnItemFocusListener(listener: OnItemFocusListener) {
        mItemFocusListener = listener
    }

    fun setViewHolder(layoutId: Int, viewHolderClassName: Class<out BaseViewHolder<T, DB>>) {
        mLayoutId = layoutId
        mViewHolderClass = viewHolderClassName
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
                    notifyItemRangeInserted(itemCount, dataList.size)
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
        return BaseViewHolder.instantiateDataBind(mLayoutId, parent.context, parent, mViewHolderClass!!, mDataBindingClass!!)

    }

    override fun onBindViewHolder(holder: BaseViewHolder<T, DB>, position: Int) {
//        Timber.d("onBindViewHolder()")
        mDataList.apply {
            if (size > position) {
                holder.apply {
                    val data = get(position)
                    setData(data, position)
                    itemView.setOnClickListener {
                        mItemClickListener?.onItemClick(it, position, data, this)
                    }
                    itemView.setOnFocusChangeListener { v, hasFocus ->
                        if (hasFocus) {
                            mItemFocusListener?.onItemFocus(v, position, itemCount)
                        }
                    }
                }
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
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


    interface OnItemClickListener<T, DB: ViewDataBinding> {
        fun onItemClick(view: View, position: Int, data: T, holder: BaseViewHolder<T, DB>)
    }

    interface OnItemFocusListener {
        fun onItemFocus(view: View, position: Int, totalCount: Int)
    }
}