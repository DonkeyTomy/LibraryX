package com.tomy.lib.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tomy.lib.ui.recycler.BaseDiffCallback
import com.tomy.lib.ui.recycler.BaseViewHolder
import com.tomy.lib.ui.recycler.IDiffDataInterface
import com.zzx.utils.rxjava.ObservableUtil
import com.zzx.utils.rxjava.toSubscribe
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import timber.log.Timber

/**
 * @property mLayoutId Item的布局Id
 * @param T Item数据类
 * @param DB: ViewDataBinding Item的ViewBinding类
 * @property mViewHolderClass Class<*>?
 * @property mDataBindingClass Class<*>?
 * @property mDataList ArrayList<T>
 * @property mItemClickListener OnItemClickListener<T>?
 */
class MainRecyclerAdapter<D, T: IDiffDataInterface<D>, DB: ViewDataBinding>: RecyclerView.Adapter<BaseViewHolder<T, DB>> {

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

    fun setDataList(dataList: List<T>?, needNotify: Boolean = true, finish: () -> Unit = {}) {
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
    }

    fun clearData(needNotify: Boolean = true) {
        ObservableUtil.changeIoToMainThread {
            mDataList.clear()
        }.toSubscribe({
            if (needNotify) {
                notifyDataSetChanged()
            }
        })
    }

    fun addDataList(dataList: List<T>?, needNotify: Boolean = true, finish: () -> Unit = {}) {
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

    fun addItem(data: T, position: Int = 0, needNotify: Boolean = true, finish: () -> Unit = {}) {
        mDataList.apply {
            if (position <= size) {
                add(position, data)
            } else {
                add(data)
            }
            if (needNotify) {
                Observable.just(Unit)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        notifyItemInserted(position)
                        finish.invoke()
                    }, {})
            }
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
//        Timber.v("onCreateViewHolder()")
        return BaseViewHolder.instantiateDataBind(mLayoutId, parent.context, parent, mViewHolderClass!!, mDataBindingClass!!)

    }

    override fun onBindViewHolder(holder: BaseViewHolder<T, DB>, position: Int) {
//        Timber.v("onBindViewHolder()")
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