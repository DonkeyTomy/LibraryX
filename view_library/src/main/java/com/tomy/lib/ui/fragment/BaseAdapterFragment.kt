package com.tomy.lib.ui.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.tomy.lib.ui.R
import com.tomy.lib.ui.adapter.MainRecyclerAdapter
import com.tomy.lib.ui.databinding.FragmentBaseRecyclerViewBinding
import com.tomy.lib.ui.recycler.layout.LinearItemDecoration
import com.yanzhenjie.recyclerview.OnItemMenuClickListener
import com.yanzhenjie.recyclerview.SwipeMenuBridge
import com.yanzhenjie.recyclerview.SwipeMenuCreator
import com.yanzhenjie.recyclerview.SwipeMenuItem
import com.zzx.utils.rxjava.toSubscribe
import io.reactivex.rxjava3.core.Observable
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**@author Tomy
 * Created by Tomy on 14/9/2020.
 */
abstract class BaseAdapterFragment<T, DB: ViewDataBinding, HV: ViewBinding, BV: ViewBinding>: BaseMsgFragment<FragmentBaseRecyclerViewBinding>(), MainRecyclerAdapter.OnItemClickListener<T>, OnItemMenuClickListener,
    OnLoadMoreListener, OnRefreshListener {


    protected val mAdapter by lazy {
        MainRecyclerAdapter<T, DB>(getItemLayoutId(), getItemViewHolderName(), getDataBindingName(),this)
    }

    protected open val mItemDecoration by lazy { LinearItemDecoration(resources.getInteger(R.integer.space_item_decoration)) }

    protected var mDataBaseList = ArrayList<T>()

    protected var mHeadBinding: HV? = null

    protected var mBottomBinding: BV? = null

    /*@BindView(R2.id.smartRefresh)
    lateinit var mStartRefreshLayout: SmartRefreshLayout*/

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

    override fun getViewBindingClass(): Class<out ViewBinding> {
        return FragmentBaseRecyclerViewBinding::class.java
    }

    /*override fun bindLayout(): Int {
        return R.layout.fragment_base_recycler_view
    }*/

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
        mBinding!!.smartRefresh.apply {
            setRefreshHeader(MaterialHeader(mContext!!))
            setRefreshFooter(ClassicsFooter(mContext!!))
            setOnRefreshListener(this@BaseAdapterFragment)
            setOnLoadMoreListener(this@BaseAdapterFragment)
        }
        mBinding!!.recyclerView.apply {
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
        addHeadContainer()
        addBottomContainer()
        /*getBottomContainer()?.apply {
            Timber.d("${this@BaseAdapterFragment.javaClass.simpleName} addBottomContainer()")
            val bottomContainer = mBinding!!.bottomContainer
            getBottomHeightPercent()?.let {
                Timber.d("${this@BaseAdapterFragment.javaClass.simpleName} bottomHeightPercent = $it")
                val parameter = bottomContainer.layoutParams as ConstraintLayout.LayoutParams
                parameter.matchConstraintPercentHeight = it
                bottomContainer.layoutParams = parameter
            }
            bottomContainer.visibility = View.VISIBLE
            val view = LayoutInflater.from(mContext).inflate(this, bottomContainer, false)
            bottomContainer.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }*/
    }

    private fun addHeadContainer() {
        val headLayoutId = getHeadContainer()
        val headViewBinding = getHeadContainerVB()
        if (headLayoutId != null || headViewBinding != null) {
            Timber.d("${this@BaseAdapterFragment.javaClass.simpleName} addHeadContainer()")
            val headContainer = mBinding!!.headContainer
            getHeadHeightPercent()?.let {
                Timber.d("${this@BaseAdapterFragment.javaClass.simpleName} headHeightPercent = $it")
                val parameter = headContainer.layoutParams as ConstraintLayout.LayoutParams
                parameter.matchConstraintPercentHeight = it
                headContainer.layoutParams = parameter
            }
            headContainer.visibility = View.VISIBLE
            val view: View? = when {
                headLayoutId != null -> {
                    LayoutInflater.from(mContext).inflate(headLayoutId, headContainer, false)
                }
                headViewBinding != null -> {
                    val method = headViewBinding.getDeclaredMethod(
                        "inflate",
                        LayoutInflater::class.java,
                        ViewGroup::class.java,
                        Boolean::class.java
                    )
                    mHeadBinding = method.invoke(null, LayoutInflater.from(mContext), headContainer, false) as HV
                    mHeadBinding!!.root
                }
                else -> {
                    null
                }
            }
            view?.apply {
                mBinding!!.headContainer.addView(this,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT)
            }

        }
    }

    private fun addBottomContainer() {
        val bottomLayoutId = getBottomContainer()
        val bottomViewBinding = getBottomContainerVB()
        if (bottomLayoutId != null || bottomViewBinding != null) {
            Timber.d("${this@BaseAdapterFragment.javaClass.simpleName} addBottomContainer()")
            val bottomContainer = mBinding!!.bottomContainer
            getBottomHeightPercent()?.let {
                Timber.d("${this@BaseAdapterFragment.javaClass.simpleName} headHeightPercent = $it")
                val parameter = bottomContainer.layoutParams as ConstraintLayout.LayoutParams
                parameter.matchConstraintPercentHeight = it
                bottomContainer.layoutParams = parameter
            }
            bottomContainer.visibility = View.VISIBLE
            val view: View? = when {
                bottomLayoutId != null -> {
                    LayoutInflater.from(mContext).inflate(bottomLayoutId, bottomContainer, false)
                }
                bottomViewBinding != null -> {
                    val method = bottomViewBinding.getDeclaredMethod(
                        "inflate",
                        LayoutInflater::class.java,
                        ViewGroup::class.java,
                        Boolean::class.java
                    )
                    mBottomBinding = method.invoke(null, LayoutInflater.from(mContext), bottomContainer, false) as BV
                    mBottomBinding!!.root
                }
                else -> {
                    null
                }
            }
            view?.apply {
                mBinding!!.bottomContainer.addView(this,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT)
            }

        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun createViewBinding(aClass: Class<ViewBinding>, inflater: LayoutInflater, container: ViewGroup): ViewBinding {
        val method = aClass.getDeclaredMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
        return method.invoke(null, inflater, container, false) as ViewBinding
    }

    open fun getHeadHeightPercent(): Float? = null

    open fun getBottomHeightPercent(): Float? = null

    override fun destroyView() {
        super.destroyView()
        getBottomContainer()?.apply {
            Timber.d("${this@BaseAdapterFragment.javaClass.simpleName} removeAllViews()")
            mBinding!!.bottomContainer.removeAllViews()
        }
        getHeadContainer()?.apply {
            mBinding!!.headContainer.removeAllViews()
        }
    }

    open fun getBottomContainer(): Int? = null

    open fun getHeadContainer(): Int? = null

    open fun getBottomContainerVB(): Class<BV>? = null

    open fun getHeadContainerVB(): Class<HV>? = null


    override fun initData() {
        super.initData()
        if (isNeedRequestOnCreate()) {
            refreshData()
        }
    }

    private fun refreshData() {
        getDataListByDataBase()
            .delay(250, TimeUnit.MILLISECONDS)
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
        refreshList(null)
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
        mBinding!!.recyclerView.removeItemDecoration(mItemDecoration)
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
        mAdapter.setDataList(list)
        onListRefresh(mAdapter.getDataList())
        dismissProgressDialog()
    }

    fun addList(list: List<T>?) {
        mAdapter.addDataList(list)
    }

    fun getAdapterDataList() = mAdapter.getDataList()

    fun getDataBaseList() = mDataBaseList

    override fun onLoadMore(refreshLayout: RefreshLayout) {
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
    }


}