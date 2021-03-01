package com.tomy.lib.ui.fragment

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding
import com.tomy.lib.ui.recycler.IDiffDataInterface
import com.zzx.utils.rxjava.toSubscribe
import io.reactivex.rxjava3.core.Observable
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**@author Tomy
 * Created by Tomy on 14/12/2020.
 * 带有本地数据或者网络请求数据的AdapterFragment
 */
abstract class BaseDataAdapterFragment<D, T: IDiffDataInterface<D>, DB: ViewDataBinding, HV: ViewBinding, BV: ViewBinding>
    : BaseAdapterFragment<D, T, DB, HV, BV>() {

    /**
     * 本地数据列表
     */
    protected var mLocalDataList = ArrayList<T>()


    override fun initData() {
        super.initData()
        if (!isNeedRefreshOnResume() && isNeedRequestOnCreate()) {
            refreshData()
            Timber.d("${this.javaClass.simpleName}: refreshData()")
        }
    }

    private fun refreshData() {
        val observable = getDataListByLocal()?.apply {
            delay(250, TimeUnit.MILLISECONDS)
                .toSubscribe({
                    mLocalDataList.clear()
                    mLocalDataList.addAll(it)
                    Timber.i("${this.javaClass.simpleName}: list.size ${it?.size}")
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

        if (observable == null && isNeedRequestFromService()) {
            requestDataFromService()
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
     * @see resumeView 上若[getDataListByLocal]从数据库上获取不到数据则从服务器获取.
     */
    open fun requestDataFromService() {
        refreshList(null)
    }

    open fun isNeedRequestFromService() = false

    /**
     * 是否需要本地获取数据
     * @return Boolean
     */
    open fun isNeedRequestFromLocal() = true

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
     * 本地读取数据.包括数据库或扫描所得
     * @return List<T>?
     */
    open fun getDataListByLocal(): Observable<List<T>>? = null

    /**
     * 获取本地数据列表.
     * @return ArrayList<T>
     */
    fun getLocalDataList() = mLocalDataList

}