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
import com.tomy.lib.ui.recycler.BaseViewHolder
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
abstract class BaseAdapterFragment<T, DB: ViewDataBinding, HV: ViewBinding, BV: ViewBinding>: BaseMsgFragment<FragmentBaseRecyclerViewBinding>(), MainRecyclerAdapter.OnItemClickListener<T, DB>, OnItemMenuClickListener,
    OnLoadMoreListener, OnRefreshListener {


    val mAdapter by lazy {
        MainRecyclerAdapter(getItemLayoutId(), getItemViewHolderName(), getDataBindingName(),this)
    }

    protected open val mItemDecoration by lazy { LinearItemDecoration(resources.getInteger(R.integer.space_item_decoration)) }

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
    abstract fun getItemViewHolderName(): Class<out BaseViewHolder<T, DB>>

    abstract fun getDataBindingName(): Class<out DB>

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
        mBinding!!.smartRefresh.apply {
            if (isLoadMoreEnabled()) {
                setEnableLoadMore(true)
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
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(mItemDecoration)
            Timber.v("isSwipeMenuEnable = ${isSwipeMenuDeleteEnable()}")
            if (isSwipeMenuDeleteEnable()) {
                setSwipeMenuCreator(mSwipeMenuCreator)
                setOnItemMenuClickListener(this@BaseAdapterFragment)
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
            adapter = mAdapter
        }
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
        if (mHeadBinding != null) {
            mBinding!!.headContainer.addView(mHeadBinding!!.root,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT)
            return
        }
        val headLayoutId = getHeadContainerLayoutId()
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

    /**
     * 添加底部布局.
     * 以[getBottomContainerLayoutId]为主,若有则优先加载LayoutId.
     * 否则加载[getBottomContainerVB]布局ViewBinding
     */
    private fun addBottomContainer() {
        if (mBottomBinding != null) {
            mBinding!!.bottomContainer.addView(mBottomBinding!!.root,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT)
            return
        }
        val bottomLayoutId = getBottomContainerLayoutId()
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
        if (getBottomContainerLayoutId() != null || getBottomContainerVB() != null) {
            mBinding!!.bottomContainer.removeAllViews()
        }
        if(getHeadContainerLayoutId() != null || getHeadContainerVB() != null) {
            mBinding!!.headContainer.removeAllViews()
        }
        mBinding?.recyclerView?.removeItemDecoration(mItemDecoration)
        mBinding?.recyclerView?.adapter = null
        mAdapter.clearData(false)
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


    fun getItemInfo(adapterPosition: Int): T? {
        return mAdapter.getItemInfo(adapterPosition)
    }


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

    /**
     * 读取Adapter中当前的数据列表,但是由于异步处理可能导致跟即将刷新的数据不同步.
     * 所以可以通过[onListRefresh]回调中获得数据列表对象
     * @return ArrayList<T>
     */
    fun getAdapterDataList() = mAdapter.getDataList()


    override fun onLoadMore(refreshLayout: RefreshLayout) {
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
    }


}