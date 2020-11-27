package com.tomy.lib.ui.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import autodispose2.autoDispose
import butterknife.BindView
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.tomy.lib.ui.R
import com.tomy.lib.ui.R2
import com.tomy.lib.ui.adapter.MainRecyclerAdapter
import com.tomy.lib.ui.recycler.layout.LinearItemDecoration
import com.yanzhenjie.recyclerview.OnItemMenuClickListener
import com.yanzhenjie.recyclerview.SwipeMenuBridge
import com.yanzhenjie.recyclerview.SwipeMenuCreator
import com.yanzhenjie.recyclerview.SwipeMenuItem
import io.reactivex.rxjava3.core.Observable
import kotlinx.android.synthetic.main.fragment_base_recycler_view.*
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**@author Tomy
 * Created by Tomy on 14/9/2020.
 */
abstract class BaseAdapterFragment<T, DB: ViewDataBinding>: BaseMsgFragment(), MainRecyclerAdapter.OnItemClickListener<T>, OnItemMenuClickListener,
    OnLoadMoreListener, OnRefreshListener {


    protected val mAdapter by lazy {
        MainRecyclerAdapter<T, DB>(getItemLayoutId(), getItemViewHolderName(), getDataBindingName(),this)
    }

    private val mItemDecoration by lazy { LinearItemDecoration(resources.getInteger(R.integer.space_item_decoration)) }

    protected var mAdapterDataList: List<T>? = null

    protected var mDataBaseList: List<T>? = null

    @BindView(R2.id.smartRefresh)
    lateinit var mStartRefreshLayout: SmartRefreshLayout

    /**
     * 获得AdapterView的Item使用的layoutID
     * @return Int
     */
    abstract fun getItemLayoutId(): Int

    /**
     * 获得每个Item使用的ViewHolder的类名
     * @return String
     */
    abstract fun getItemViewHolderName(): Class<*>

    abstract fun getDataBindingName(): Class<*>

    /**
     * 是否启动侧滑显示删除按钮功能
     * @return Boolean
     */
    open fun isSwipeMenuDeleteEnable(): Boolean {
        return false
    }

    override fun onItemClick(menuBridge: SwipeMenuBridge?, adapterPosition: Int) {
        Timber.d("onItemDelete.position = $adapterPosition")
        menuBridge?.closeMenu()
    }

    override fun onItemClick(view: View, position: Int, data: T) {
    }

    override fun bindLayout(): Int {
        return R.layout.fragment_base_recycler_view
    }

    private val mSwipeMenuCreator by lazy {
        SwipeMenuCreator {
            _, rightMenu, _ ->
            rightMenu.addMenuItem(SwipeMenuItem(mContext!!).apply {
                setImage(R.drawable.ic_delete)
                setBackground(R.drawable.bg_delete)
                setTextColor(R.color.white)
                setText(R.string.delete)
                height  = ViewGroup.LayoutParams.MATCH_PARENT
                width   = 100
            })
        }
    }

    override fun initView(root: View) {
        super.initView(root)
        if (isNeedRefreshOnResume() || isNeedRequestOnCreate()) {
            showProgressDialog(null)
        }
        mStartRefreshLayout.apply {
            setRefreshHeader(MaterialHeader(mContext!!))
            setRefreshFooter(ClassicsFooter(mContext!!))
            setOnRefreshListener(this@BaseAdapterFragment)
            setOnLoadMoreListener(this@BaseAdapterFragment)
        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(mItemDecoration)
            Timber.v("isSwipeMenuEnable = ${isSwipeMenuDeleteEnable()}")
            if (isSwipeMenuDeleteEnable()) {
                setSwipeMenuCreator(mSwipeMenuCreator)
                setOnItemMenuClickListener(this@BaseAdapterFragment)
            }
            adapter = mAdapter
        }
    }

    override fun modifyView(root: View) {
        super.modifyView(root)
        getBottomContainer()?.apply {
            Timber.d("${this@BaseAdapterFragment.javaClass.simpleName} addBottomContainer()")
            val bottomContainer = root.findViewById<FrameLayout>(R.id.bottomContainer)
            bottomContainer.visibility = View.VISIBLE
            val view = LayoutInflater.from(mContext).inflate(this, bottomContainer, false)
            bottomContainer.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
        getHeadContainer()?.apply {
            Timber.d("${this@BaseAdapterFragment.javaClass.simpleName} addBottomContainer()")
            val headContainer = root.findViewById<FrameLayout>(R.id.headContainer)
            headContainer.visibility = View.VISIBLE
            val view = LayoutInflater.from(mContext).inflate(this, headContainer, false)
            headContainer.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    override fun destroyView() {
        super.destroyView()
        getBottomContainer()?.apply {
            Timber.d("${this@BaseAdapterFragment.javaClass.simpleName} removeAllViews()")
            bottomContainer.removeAllViews()
        }
        getHeadContainer()?.apply {
            headContainer.removeAllViews()
        }
    }

    open fun getBottomContainer(): Int? = null

    open fun getHeadContainer(): Int? = null


    override fun initData() {
        super.initData()
        if (isNeedRequestOnCreate()) {
            refreshData()
        }
    }

    private fun refreshData() {
        getDataListByDataBase()
            .delay(250, TimeUnit.MILLISECONDS)
            .autoDispose(mScopeProvider)
            .subscribe({
                Timber.i("list.size ${it?.size}")
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
            }, this)
    }

    override fun resumeView() {
        super.resumeView()
        /*Observable.just(Unit)
                .observeOn(Schedulers.io())
                .map {
                    mDataBaseList = getDataListByDataBase()
                    Timber.d("mDataList = [${mDataBaseList.hashCode()}] ${mDataBaseList?.size}. isNeedRequestFromService() = ${isNeedRequestFromService()}")
                    mDataBaseList
                }*/
        if (isNeedRefreshOnResume()) {
            refreshData()
        }
    }

    /**
     * 从服务器上获取数据
     * @see resumeView 上若[getDataListByDataBase]从数据库上获取不到数据则从服务器获取.
     */
    open fun requestDataFromService() {

    }

    open fun isNeedRequestFromService() = true

    open fun isNeedRequestFromDatabase() = true

    /**
     * 每次onResume()是否需要重新请求数据刷新界面
     * @return Boolean
     */
    open fun isNeedRefreshOnResume() = false

    /**
     * 首次进入是否需要请求数据
     * @return Boolean
     */
    open fun isNeedRequestOnCreate() = true

    /**
     * 读取Adapter中当前的数据列表,但是由于异步处理可能导致跟即将刷新的数据不同步.
     * 所以可以通过[onListRefresh]回调中获得数据列表对象
     * @return ArrayList<T>?
     */
    fun getDataList() = mAdapter.getDataList()

    fun getItemInfo(adapterPosition: Int): T? {
        return mAdapter.getItemInfo(adapterPosition)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerView.removeItemDecoration(mItemDecoration)
        mAdapter.clearData(false)
    }

    /**
     * 从数据库中读取数据
     * @return List<T>?
     */
    abstract fun getDataListByDataBase(): Observable<List<T>>

//    abstract fun getDataListFromServer(): Observable<List<T>>

    /**
     * 当列表刷新时回调当前数据列表
     * @param list List<T>?
     */
    open fun onListRefresh(list: List<T>?) {

    }

    fun clearData(needNotify: Boolean = true) {
        mAdapter.clearData(needNotify)
    }

    /**
     * 使用新数据刷新UI,传入空则清除AdapterView
     * @param list List<T>?
     */
    fun refreshList(list: List<T>?) {
        Timber.i("refreshList.list = [${list.hashCode()}] ${list?.size}")
        mAdapterDataList = list
        if (mAdapterDataList == null) {
            mAdapter.setDataList(null)
        } else {
            mAdapterDataList?.apply {
                mAdapter.setDataList(if (this is ArrayList) this else ArrayList(this))
            }
        }
        onListRefresh(list)
        dismissProgressDialog()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
    }


}