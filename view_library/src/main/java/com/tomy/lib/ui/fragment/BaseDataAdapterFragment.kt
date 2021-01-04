package com.tomy.lib.ui.fragment

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding
import com.zzx.utils.rxjava.toSubscribe
import io.reactivex.rxjava3.core.Observable
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**@author Tomy
 * Created by Tomy on 14/12/2020.
 * 带有数据库或者网络请求数据的AdapterFragment
 */
abstract class BaseDataAdapterFragment<T, DB: ViewDataBinding, HV: ViewBinding, BV: ViewBinding>
    : BaseAdapterFragment<T, DB, HV, BV>() {

    protected var mDataBaseList = ArrayList<T>()


    override fun initData() {
        super.initData()
        if (isNeedRequestOnCreate()) {
            refreshData()
        }
    }

    private fun refreshData() {
        getDataListByDataBase()?.apply {
            delay(250, TimeUnit.MILLISECONDS)
                .toSubscribe({
                    mDataBaseList.addAll(it)
                    Timber.i("${this.javaClass.name}: list.size ${it?.size}")
                    when {
                        it?.isNotEmpty() == true -> {
                            isNeedRequestFromService()
                            refreshList(it)
                        }
                        isNeedRequestFromService() -> {
                            requestDataFromService()
                        }
                        else -> {
                            dismissProgressDialog()
                        }
                    }
                }, this@BaseDataAdapterFragment)

        }
    }

    override fun initView(root: View) {
        if (isNeedShowDialog() && (isNeedRefreshOnResume() || isNeedRequestOnCreate())) {
            showProgressDialog(null)
        }
        super.initView(root)
    }

    override fun resumeView() {
        super.resumeView()
        if (isNeedRefreshOnResume()) {
            refreshData()
        }
    }

    /**
     * 从服务器上获取数据
     * @see resumeView 上若[getDataListByDataBase]从数据库上获取不到数据则从服务器获取.
     */
    open fun requestDataFromService() {
        refreshList(null)
    }

    open fun isNeedRequestFromService() = true

    open fun isNeedRequestFromDatabase() = true

    /**
     * 每次onResume()是否需要重新请求数据刷新界面
     * @return Boolean
     */
    open fun isNeedRefreshOnResume() = false

    open fun isNeedShowDialog() = false

    /**
     * 首次进入是否需要请求数据
     * @return Boolean
     */
    open fun isNeedRequestOnCreate() = true


    /**
     * 从数据库中读取数据
     * @return List<T>?
     */
    open fun getDataListByDataBase(): Observable<List<T>>? = null

    fun getDataBaseList() = mDataBaseList

}