package com.tomy.lib.ui.activity

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
abstract class BaseViewHolder<T, DB: ViewDataBinding>(var dataBinding: DB): RecyclerView.ViewHolder(dataBinding.root) {

//    constructor(layoutId: Int, context: Context, viewGroup: ViewGroup): this(LayoutInflater.from(context).inflate(layoutId, viewGroup, false))

    init {
//        ButterKnife.bind(this, itemView)
    }

    abstract fun setData(data: T)

    abstract fun getLayoutId(): Int

    companion object {
        fun <T, DB: ViewDataBinding>instantiate(layoutId: Int, context: Context, viewGroup: ViewGroup, clazzName: String): BaseViewHolder<T, DB> {
            val clazz = Class.forName(clazzName)
            val view = LayoutInflater.from(context).inflate(layoutId, viewGroup, false)
            return clazz.getConstructor(Context::class.java, View::class.java).newInstance(context, view) as BaseViewHolder<T, DB>
        }

        fun <T, DB: ViewDataBinding>instantiateDataBind(layoutId: Int, context: Context, viewGroup: ViewGroup, clazz: Class<*>, dbClazz: Class<*>): BaseViewHolder<T, DB> {
            val dataBinding = DataBindingUtil.inflate<DB>(LayoutInflater.from(context), layoutId, viewGroup, false)
            return clazz.getConstructor(dbClazz).newInstance(dataBinding) as BaseViewHolder<T, DB>
        }

    }

}