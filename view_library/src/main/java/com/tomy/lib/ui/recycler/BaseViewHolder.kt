package com.tomy.lib.ui.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**@author Tomy
 * Created by Tomy on 12/9/2020.
 */
@Suppress("UNCHECKED_CAST")
abstract class BaseViewHolder<T, DB: ViewDataBinding>(var context: Context, var dataBinding: DB): RecyclerView.ViewHolder(dataBinding.root) {

//    constructor(layoutId: Int, context: Context, viewGroup: ViewGroup): this(LayoutInflater.from(context).inflate(layoutId, viewGroup, false))

    init {
//        ButterKnife.bind(this, itemView)
    }

    open fun setData(data: T, position: Int) {
//        Timber.v("setDate(): $position")
    }

    open fun setData(data: T, position: Int, payloads: MutableList<Any>?) {
        if (payloads == null) {
            setData(data, position)
        }
    }

    companion object {
        fun <T, DB: ViewDataBinding>instantiate(layoutId: Int, context: Context, viewGroup: ViewGroup, clazzName: String): BaseViewHolder<T, DB> {
            val clazz = Class.forName(clazzName)
            val view = LayoutInflater.from(context).inflate(layoutId, viewGroup, false)
            return clazz.getConstructor(Context::class.java, View::class.java).newInstance(context, view) as BaseViewHolder<T, DB>
        }

        /**
         * 此处若出现传入的类不正常,可能是由于AdapterFragment里的getItemLayoutId()跟指定的DataBinding类不匹配
         */
        fun <T, DB: ViewDataBinding> instantiateDataBind(layoutId: Int, context: Context, viewGroup: ViewGroup,
                                                        viewHolderClass: Class<out BaseViewHolder<T, DB>>,
                                                        dbClazz: Class<out DB>): BaseViewHolder<T, DB> {
            val dataBinding = DataBindingUtil.inflate<DB>(LayoutInflater.from(context), layoutId, viewGroup, false)
//            Timber.v("MainRecyclerAdapter:dbClazz: ${dbClazz.simpleName}; dataBinding: ${dataBinding.javaClass.simpleName}")
            return viewHolderClass.getConstructor(Context::class.java, dbClazz).newInstance(context, dataBinding)
        }

    }

}