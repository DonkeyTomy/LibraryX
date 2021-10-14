package com.tomy.lib.ui.fragment

import android.annotation.SuppressLint
import android.os.Build
import android.view.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
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
import com.tomy.lib.ui.recycler.BaseViewHolder
import com.tomy.lib.ui.recycler.IDiffDataInterface
import com.tomy.lib.ui.recycler.grid.GridItemDecoration
import com.tomy.lib.ui.recycler.layout.LinearItemDecoration
import com.yanzhenjie.recyclerview.OnItemMenuClickListener
import com.yanzhenjie.recyclerview.SwipeMenuBridge
import com.yanzhenjie.recyclerview.SwipeMenuCreator
import com.yanzhenjie.recyclerview.SwipeMenuItem
import timber.log.Timber

/**
 * @author Tomy
 * Created by Tomy on 14/9/2020.
 * @param T Item里的数据
 * @param DB: ViewDataBinding Item的ViewDataBinding
 * @param HV: ViewBinding 头部控件的ViewBinding
 * @param BV: ViewBinding 底部控件的ViewBinding
 * @property mAdapter MainRecyclerAdapter<T, DB>
 * @property mItemDecoration LinearItemDecoration
 * @property mHeadBinding HV
 * @property mBottomBinding BV
 * @property mSwipeMenuCreator SwipeMenuCreator
 * @see isRefreshEnabled 是否启用下拉刷新.默认关闭
 * @see isLoadMoreEnabled 是否启用上拉加载更多.默认关闭
 */
@RequiresApi(Build.VERSION_CODES.M)
abstract class BaseAdapterFragment<D, T: IDiffDataInterface<D>, DB: ViewDataBinding, HV: ViewBinding, BV: ViewBinding>
    : BaseMsgFragment<FragmentBaseRecyclerViewBinding>(), OnItemMenuClickListener,
    MainRecyclerAdapter.OnItemClickListener<T, DB>, MainRecyclerAdapter.OnItemLongClickListener<T, DB>, MainRecyclerAdapter.OnItemSelectCountListener,
    OnLoadMoreListener, OnRefreshListener, ActionMode.Callback {


    val mAdapter by lazy {
        MainRecyclerAdapter(getItemLayoutId(), getItemViewHolderClass(), getDataBindingClass(),this)
    }

    protected open val mItemDecoration by lazy {
        if (mLayoutManagerType == LAYOUT_MANAGER_TYPE_LINEAR) {
            LinearItemDecoration(getItemDecorationSpace(), isItemDecorationSpaceSame())
        } else {
            GridItemDecoration(getItemDecorationSpace())
        }
    }

    protected val mLayoutManagerType by lazy {
        getLayoutManagerType()
    }

    protected var mHeadBinding: HV? = null

    protected var mBottomBinding: BV? = null

    private val mInflater by lazy { LayoutInflater.from(mContext!!) }

    private var mActionModeCallback: ActionMode.Callback? = null

    private var mActionMode: ActionMode? = null

    /**
     * 顶部控件高度.若[getHeadHeightPercent]已指定高度占比,则使用MATCH_PARENT,反之则默认使用WRAP_CONTENT
     */
    private val mHeaderContainerHeight by lazy {
        if (getHeadHeightPercent() != null) ViewGroup.LayoutParams.MATCH_PARENT else ViewGroup.LayoutParams.WRAP_CONTENT
    }

    /**
     * 底部控件高度.若[getBottomHeightPercent]已指定高度占比,则使用MATCH_PARENT,反之则默认使用WRAP_CONTENT
     */
    private val mBottomContainerHeight by lazy {
        if (getBottomHeightPercent() != null) ViewGroup.LayoutParams.MATCH_PARENT else ViewGroup.LayoutParams.WRAP_CONTENT
    }

    open fun getLayoutManagerType(): Int {
        return LAYOUT_MANAGER_TYPE_LINEAR
    }

    open fun getGridAdapterConfig(): GridAdapterConfig {
        return GridAdapterConfig()

    }

    /**
     * 指定ItemDecoration.
     * PS: 这里指定的包括全方向的间距
     * @return Int
     */
    open fun getItemDecorationSpace(): Int {
        return resources.getInteger(R.integer.space_item_decoration)
    }

    /**
     * 指定ItemDecoration总体宽高间距是否相等.
     * 只对LinearManager有效
     * @return Boolean
     */
    open fun isItemDecorationSpaceSame(): Boolean = true

    /**
     * 获得AdapterView的Item使用的layoutID
     * 必须跟[getDataBindingClass]同步!
     * @return Int
     */
    abstract fun getItemLayoutId(): Int

    /**
     * 获得每个Item使用的ViewHolder的类名
     * @return String
     */
    abstract fun getItemViewHolderClass(): Class<out BaseViewHolder<T, DB>>

    abstract fun getDataBindingClass(): Class<out DB>

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

    override fun onItemClick(view: View, position: Int, data: T, viewHolder: BaseViewHolder<T, DB>) {
        Timber.v("onItemClick(). position = $position; data = $data")
    }

    override fun onItemLongClick(view: View, position: Int, data: T, viewHolder: BaseViewHolder<T, DB>): Boolean {
        Timber.v("onItemLongClick(). position = $position; data = $data")
        if (isSelectModeEnabled()) {
            toggleSelectMode()
        }
        return true
    }

    override fun onItemSelectCount(count: Int) {
        Timber.v("onItemSelectCount(): $count;")
        mActionMode?.title = getString(R.string.select_count, count)
    }

    override fun getViewBindingClass(): Class<out FragmentBaseRecyclerViewBinding> {
        return FragmentBaseRecyclerViewBinding::class.java
    }

    override fun getFatherClass(): Class<out Any>? {
        return BaseAdapterFragment::class.java
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
                setTextColor(resources.getColor(R.color.white, null))
                setText(R.string.delete)
                height  = ViewGroup.LayoutParams.MATCH_PARENT
                width   = 100
            })
        }
    }

    @SuppressLint("WrongConstant")
    override fun initView(root: View) {
        super.initView(root)
        mBinding!!.smartRefresh.apply {
            if (isLoadMoreEnabled()) {
                setEnableLoadMore(true)
                setEnableAutoLoadMore(false)
                setRefreshFooter(ClassicsFooter(mContext!!))
                setOnLoadMoreListener(this@BaseAdapterFragment)
            }
            if (isRefreshEnabled()) {
                setEnableRefresh(true)
                setRefreshHeader(MaterialHeader(mContext!!))
                setOnRefreshListener(this@BaseAdapterFragment)
            }
        }
        mBinding!!.recyclerView.apply {
            layoutManager = if (mLayoutManagerType == LAYOUT_MANAGER_TYPE_LINEAR) {
                LinearLayoutManager(context)
            } else {
                val config = getGridAdapterConfig()
                Timber.v("gridConfig = $config")
                GridLayoutManager(context, config.spanCount, config.orientation, config.reverseLayout)
            }
            addItemDecoration(mItemDecoration)
            Timber.v("isSwipeMenuEnable = ${isSwipeMenuDeleteEnable()}")
            if (isSwipeMenuDeleteEnable()) {
                setSwipeMenuCreator(mSwipeMenuCreator)
                setOnItemMenuClickListener(this@BaseAdapterFragment)
            }
            getRecyclerViewHeadLayoutId()?.let {
                addHeaderView(LayoutInflater.from(mContext!!).inflate(it, mBinding!!.recyclerViewContainer, false))
            }
            getRecyclerViewFootLayoutId()?.let {
                addFooterView(LayoutInflater.from(mContext!!).inflate(it, mBinding!!.recyclerViewContainer, false))
            }
            /**
             * 解决选中刷新焦点时Item会闪烁问题
             */
            mAdapter.setHasStableIds(true)
            /**
             * 解决调用[androidx.recyclerview.widget.RecyclerView.Adapter.notifyItemChanged]等刷新方法时会将当前刷新项滚动到顶部
             */
            setHasFixedSize(true)
            itemAnimator = null
            mAdapter.setOnItemLongClickListener(this@BaseAdapterFragment)
            mAdapter.setOnItemSelectCountListener(this@BaseAdapterFragment)
            adapter = mAdapter
        }
    }

    /**
     * 是否启动滑动删除菜单功能
     */
    fun setSwipeItemMenuEnabled(enable: Boolean) {
        mBinding!!.recyclerView.isSwipeItemMenuEnabled = enable
    }

    fun checkList(noMoreData: Boolean, isLoadMore: Boolean = false, allMode: Boolean = true, delay: Int = 300) {
        mBinding?.smartRefresh?.apply {
            when {
                allMode -> {
                    finishRefresh(delay, true, noMoreData)
                    finishLoadMore(delay, true, noMoreData)
                }
                isLoadMore -> {
                    finishLoadMore(delay, true, noMoreData)
                }
                else -> {
                    finishRefresh(delay, true, noMoreData)
                }
            }
            if (noMoreData) {
                setNoMoreData(false)
            }
        }
    }

    fun finishSmartRefresh() {
        mBinding?.smartRefresh?.apply {
            finishRefresh()
            finishLoadMore()
        }
    }

    fun isInSelectMode(): Boolean {
        return mAdapter.isInSelectMode()
    }

    fun setSelectModeEnable(enable: Boolean) {
        mAdapter.setSelectModeEnable(enable)
    }

    fun isSelectModeEnabled() = mAdapter.isSelectModeEnabled()

    fun getSelectMode() = mAdapter.getSelectMode()

    fun setSelectMode(@MainRecyclerAdapter.SelectMode selectMode: Int) {
        mActionMode = when (selectMode) {
            MainRecyclerAdapter.SELECT_MODE_NONE    -> {
                if (isSwipeMenuDeleteEnable()) {
                    setSwipeItemMenuEnabled(true)
                }
                mActionMode?.finish()
                null
            }
            else -> {
                if (isSwipeMenuDeleteEnable()) {
                    setSwipeItemMenuEnabled(false)
                }
                mContext?.startActionMode(this, ActionMode.TYPE_PRIMARY)
            }
        }
        mAdapter.setSelectMode(selectMode)
    }

    fun setActionModeCallback(callback: ActionMode.Callback?) {
        mActionModeCallback = callback
    }

    fun toggleSelectMode() {
        when (getSelectMode()) {
            MainRecyclerAdapter.SELECT_MODE_NONE -> {
                setSelectMode(MainRecyclerAdapter.SELECT_MODE_MULTIPLE)
            }
            else -> {
                quitSelectMode()
            }
        }
    }

    fun quitSelectMode() {
        if (isInSelectMode()) {
            setSelectMode(MainRecyclerAdapter.SELECT_MODE_NONE)
        }
    }

    fun selectAllItem() {
        if (getSelectMode() == MainRecyclerAdapter.SELECT_MODE_MULTIPLE) {
            mAdapter.selectAllItem()
        }
    }

    fun cleanSelectItem() {
        if (isInSelectMode()) {
            mAdapter.cleanSelect()
        }
    }

    override fun onBackPressed(): Boolean {
        if (isInSelectMode()) {
            quitSelectMode()
            return true
        }
        return super.onBackPressed()
    }

    override fun resumeView() {
        super.resumeView()
        finishSmartRefresh()
    }

    fun getFocusChildPosition(): Int {
        mBinding!!.recyclerView.apply {
            val focusChild: View? = focusedChild
            return if (focusChild == null) -1 else getChildAdapterPosition(focusedChild)
        }
    }

    override fun modifyView(root: View) {
        super.modifyView(root)
        addHeadContainer()
        addBottomContainer()
        getRecyclerViewContainerWidthPercent()?.let {
            mBinding?.recyclerViewContainer?.apply {
                val params = layoutParams as ConstraintLayout.LayoutParams
                params.matchConstraintPercentWidth = it
                layoutParams = params
            }
        }
    }

    /**
     * 是否启用下拉刷新.默认关闭
     * @return Boolean
     */
    open fun isRefreshEnabled(): Boolean    = false

    /**
     * 是否启用上拉加载更多.默认关闭
     * @return Boolean
     */
    open fun isLoadMoreEnabled(): Boolean   = false

    /**
     * 添加顶部部布局.
     * 以[getHeadContainerLayoutId]为主,若有则优先加载LayoutId.
     * 否则加载[getHeadContainerVB]布局ViewBinding
     */
    private fun addHeadContainer() {
//        Timber.v("${this.javaClass.simpleName} addHeadContainer(). Have head: ${mHeadBinding != null}")
        if (mHeadBinding != null) {
            mBinding!!.headContainer.addView(mHeadBinding!!.root,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    mHeaderContainerHeight)
            return
        }
        val headLayoutId = getHeadContainerLayoutId()
        val headViewBinding = getHeadContainerVB()
        if (headLayoutId != null || headViewBinding != null) {
//            Timber.v("${javaClass.simpleName} addHeadContainer()")
            val headContainer = mBinding!!.headContainer
            val percent = getHeadHeightPercent()?.also {
                Timber.d("${javaClass.simpleName} headHeightPercent = $it")
                headContainer.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    height  = 0
                    matchConstraintPercentHeight    = it
                }
            }
            if (percent != null && percent == 0f) {
                return
            }
            headContainer.visibility = View.VISIBLE
            val view: View? = when {
                headLayoutId != null -> {
                    mInflater.inflate(headLayoutId, headContainer, false)
                }
                headViewBinding != null -> {
                    mHeadBinding = createViewBinding(headViewBinding, mInflater, headContainer)
                    mHeadBinding!!.root
                }
                else -> {
                    null
                }
            }
            view?.apply {
                mBinding!!.headContainer.addView(this,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    mHeaderContainerHeight)
            }

        }
    }

    /**
     * 添加底部布局.
     * 以[getBottomContainerLayoutId]为主,若有则优先加载LayoutId.
     * 否则加载[getBottomContainerVB]布局ViewBinding
     */
    private fun addBottomContainer() {
//        Timber.v("${javaClass.simpleName} addBottomContainer(). Have bottom: ${mBottomBinding != null}")
        if (mBottomBinding != null) {
            mBinding!!.bottomContainer.addView(mBottomBinding!!.root,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    mBottomContainerHeight)
            return
        }
        val bottomLayoutId = getBottomContainerLayoutId()
        val bottomViewBinding = getBottomContainerVB()
        if (bottomLayoutId != null || bottomViewBinding != null) {
//            Timber.v("${javaClass.simpleName} addBottomContainer()")
            val bottomContainer = mBinding!!.bottomContainer
            val percent = getBottomHeightPercent()?.also {
                Timber.d("${javaClass.simpleName} bottomHeightPercent = $it")
                bottomContainer.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    height  = 0
                    matchConstraintPercentHeight    = it
                }
            }
            if (percent != null && percent == 0f) {
                return
            }
            bottomContainer.visibility = View.VISIBLE
            val view: View? = when {
                bottomLayoutId != null -> {
                    mInflater.inflate(bottomLayoutId, bottomContainer, false)
                }
                bottomViewBinding != null -> {
                    mBottomBinding = createViewBinding(bottomViewBinding, mInflater, bottomContainer)
                    mBottomBinding!!.root
                }
                else -> {
                    null
                }
            }
            view?.apply {
                mBinding!!.bottomContainer.addView(this,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    mBottomContainerHeight)
            }

        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <V: ViewBinding>createViewBinding(aClass: Class<V>, inflater: LayoutInflater, container: ViewGroup, attachToRoot: Boolean = false): V {
        val method = aClass.getDeclaredMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
        return method.invoke(null, inflater, container, attachToRoot) as V
    }

    /**
     * 指定顶部控件占的高度比
     * @return Float?
     */
    open fun getHeadHeightPercent(): Float? = null

    /**
     * 指定底部控件占的高度比
     * @return Float?
     */
    open fun getBottomHeightPercent(): Float? = null

    /**
     * 指定RecyclerView的宽度百分比
     * @return Float?
     */
    open fun getRecyclerViewContainerWidthPercent(): Float? = null

    override fun destroyView() {
        super.destroyView()
        quitSelectMode()
        if (getBottomContainerLayoutId() != null || getBottomContainerVB() != null) {
            mBinding!!.bottomContainer.removeAllViews()
        }
        if(getHeadContainerLayoutId() != null || getHeadContainerVB() != null) {
            mBinding!!.headContainer.removeAllViews()
        }
        mBinding?.recyclerView?.apply {
            removeItemDecoration(mItemDecoration)
            adapter = null
        }
        mAdapter.clearData(false)
    }

    override fun onDestroy() {
        mHeadBinding    = null
        mBottomBinding  = null
        super.onDestroy()
    }

    /**
     * 返回底部布局Id.用于[addBottomContainer],优先级高于[getBottomContainerVB]
     * @return Int?
     */
    open fun getBottomContainerLayoutId(): Int? = null

    /**
     * 返回头部布局Id.用于[addHeadContainer],优先级高于[getHeadContainerVB]
     * @return Int?
     */
    open fun getHeadContainerLayoutId(): Int? = null

    /**
     * 返回底部布局ViewBinding.用于[addBottomContainer],优先级低于[getBottomContainerLayoutId]
     * @return Class<BV>?
     */
    open fun getBottomContainerVB(): Class<BV>? = null

    /**
     * 返回头部布局ViewBinding.用于[addHeadContainer],优先级低于[getHeadContainerLayoutId]
     * @return Class<HV>?
     */
    open fun getHeadContainerVB(): Class<HV>? = null

    open fun getRecyclerViewHeadLayoutId(): Int? = null

    open fun getRecyclerViewFootLayoutId(): Int? = null


    fun getItemInfo(adapterPosition: Int): T? {
        return mAdapter.getItemInfo(adapterPosition)
    }


    /**
     * 当列表刷新时回调当前数据列表
     * @param list List<T>?
     */
    open fun onListRefresh(list: List<T>?, modifyListSize: Int = 0) {
    }

    fun clearData(needNotify: Boolean = true, finish: () -> Unit = {}) {
        mAdapter.clearData(needNotify) {
            finish()
            onListRefresh(getAdapterDataList(), 0)
        }
    }

    fun clearDataImmediate() {
        mAdapter.clearDataImmediate()
    }

    /**
     * 使用新数据刷新UI,传入空则清除AdapterView
     * @param list List<T>?
     */
    fun refreshList(list: List<T>?, needNotify: Boolean = true, finish: () -> Unit = {}) {
        Timber.d("refreshList.list = ${list?.size}")
        mAdapter.setDataList(list, needNotify) {
            finish()
            onListRefresh(getAdapterDataList(), list?.size ?: 0)
            dismissProgressDialog()
        }
    }

    /**
     * 使用新数据刷新UI,传入空则清除AdapterView
     * @param list List<T>?
     */
    fun replaceList(list: List<T>?, needNotify: Boolean = true, finish: () -> Unit = {}) {
        Timber.d("replaceList.list = ${list?.size}")
        mAdapter.replaceDataList(list, needNotify) {
            finish()
            onListRefresh(getAdapterDataList(), list?.size ?: 0)
            dismissProgressDialog()
        }
    }

    fun addList(list: List<T>?, needNotify: Boolean = true, finish: () -> Unit = {}) {
        mAdapter.addDataList(list, needNotify) {
            finish.invoke()
            onListRefresh(getAdapterDataList(), list?.size ?: 0)
            dismissProgressDialog()
        }
    }

    fun addData(data: T, position: Int? = null, needNotify: Boolean = true, finish: () -> Unit = {}) {
        mAdapter.addItem(data, position, needNotify) {
            finish.invoke()
            onListRefresh(getAdapterDataList(), 1)
            dismissProgressDialog()
        }
    }

    /**
     * 读取Adapter中当前的数据列表,但是由于异步处理可能导致跟即将刷新的数据不同步.
     * 所以可以通过[onListRefresh]回调中获得数据列表对象
     * @return ArrayList<T>
     */
    fun getAdapterDataList() = mAdapter.getDataList()

    fun getSelectedPositionSet() = mAdapter.getSelectedPositionSet()

    fun getDataSize() = mAdapter.itemCount

    override fun onLoadMore(refreshLayout: RefreshLayout) {
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
    }

    companion object {
        const val LAYOUT_MANAGER_TYPE_LINEAR    = 1

        const val LAYOUT_MANAGER_TYPE_GRID      = 2
    }

    data class GridAdapterConfig(
        val spanCount: Int = 0,
        val orientation: Int = GridLayoutManager.VERTICAL,
        val reverseLayout: Boolean = false
    )

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        mActionModeCallback?.onCreateActionMode(mode, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        mActionModeCallback?.onPrepareActionMode(mode, menu)
        return true
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        mActionModeCallback?.onActionItemClicked(mode, item)
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode) {
        quitSelectMode()
    }

}