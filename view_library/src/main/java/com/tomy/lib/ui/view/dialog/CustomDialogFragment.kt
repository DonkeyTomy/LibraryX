package com.tomy.lib.ui.view.dialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewbinding.ViewBinding
import com.tomy.lib.ui.databinding.FragmentCustomDialogBinding
import com.tomy.lib.ui.fragment.BaseDialogFragment
import timber.log.Timber

/**@author Tomy
 * Created by Tomy on 12/9/2020.
 * @property MB MainViewBinding 内容主体VB
 * @property HB HeaderViewBinding HeaderVB
 * @property FB FooterViewBinding FooterVB
 */
abstract class CustomDialogFragment<MB: ViewBinding, HB: ViewBinding, FB: ViewBinding>: BaseDialogFragment<FragmentCustomDialogBinding>() {

    protected var mFooterBinding: FB? = null

    protected var mHeaderBinding: HB? = null

    protected var mContentBinding: MB? = null

    protected var mHeaderVisible    = true

    protected var mContentVisible   = true

    protected var mFooterVisible    = true

    private val mInflater by lazy {
        LayoutInflater.from(context)
    }

    private val mHeaderContainerHeight by lazy {
        if (getHeaderHeightPercent() != null) ViewGroup.LayoutParams.MATCH_PARENT else ViewGroup.LayoutParams.WRAP_CONTENT
    }

    override fun modifyView(view: View) {
        addHeadContainer()
        applyHeadContainer()
        applyHeaderVisible()

        addFooterContainer()
        applyFooterContainer()
        applyFooterVisible()

        addContentContainer()
        applyContent()
        applyContentVisible()
    }

    private val mContentContainerHeight by lazy {
        if (getContentHeightPercent() != null) ViewGroup.LayoutParams.MATCH_PARENT else ViewGroup.LayoutParams.WRAP_CONTENT
    }

    open fun addContentContainer() {
        if (mContentBinding != null) {
            Timber.v("addLastContentContainer()")
            mBinding!!.containerContent.addView(mContentBinding!!.root,
                mContentContainerHeight)
            return
        }
        mContentBinding = createViewBinding(getContentVB(), mInflater, mBinding!!.containerContent)
        Timber.v("addContentContainer()")
        mBinding!!.containerContent.addView(mContentBinding!!.root,
            mContentContainerHeight)
    }

    open fun applyContent() {}

    fun footerVisible(visible: Boolean): CustomDialogFragment<MB, HB, FB> {
        mFooterVisible = visible
        applyFooterVisible()
        return this
    }

    fun applyFooterVisible() {
        mBinding?.containerFooter?.visibility = if (mFooterVisible) View.VISIBLE else View.GONE
    }

    fun headerVisible(visible: Boolean): CustomDialogFragment<MB, HB, FB> {
        mHeaderVisible = visible
        applyHeaderVisible()
        return this
    }

    fun applyHeaderVisible() {
        mBinding?.containerHeader?.visibility = if (mHeaderVisible) View.VISIBLE else View.GONE
    }

    fun contentVisible(visible: Boolean): CustomDialogFragment<MB, HB, FB> {
        mContentVisible = visible
        applyContentVisible()
        return this
    }

    fun applyContentVisible() {
        mBinding?.containerContent?.visibility = if (mContentVisible) View.VISIBLE else View.GONE
    }

    open fun applyHeadContainer() {
        Timber.v("applyHeadContainer()")
    }

    open fun applyFooterContainer() {
        Timber.v("applyFooterContainer()")
    }


    /**
     * 添加顶部部布局.
     * 以[getHeaderContainerLayoutId]为主,若有则优先加载LayoutId.
     * 否则加载[getHeaderContainerVB]布局ViewBinding
     */
    private fun addHeadContainer() {
        if (mHeaderBinding != null) {
            Timber.v("addLastHeadContainer()")
            mBinding!!.containerHeader.addView(mHeaderBinding!!.root,
                ViewGroup.LayoutParams.MATCH_PARENT,
                mHeaderContainerHeight)
            return
        }
        val headLayoutId = getHeaderContainerLayoutId()
        val headViewBinding = getHeaderContainerVB()
        if (headLayoutId != null || headViewBinding != null) {
            val headContainer = mBinding!!.containerHeader
            getHeaderHeightPercent()?.let {
                Timber.d("${javaClass.simpleName} headHeightPercent = $it")
                val parameter = headContainer.layoutParams as ConstraintLayout.LayoutParams
                parameter.matchConstraintPercentHeight = it
                headContainer.layoutParams = parameter
            }
            headContainer.visibility = View.VISIBLE
            val view: View? = when {
                headLayoutId != null -> {
                    mInflater.inflate(headLayoutId, headContainer, false)
                }
                headViewBinding != null -> {
                    mHeaderBinding = createViewBinding(headViewBinding, mInflater, headContainer)
                    mHeaderBinding!!.root
                }
                else -> {
                    null
                }
            }
            view?.apply {
                Timber.v("addHeadContainer()")
                mBinding!!.containerHeader.addView(this,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    mHeaderContainerHeight)
            }

        }
    }

    open fun getHeaderHeightPercent(): Float? = null

    open fun getFooterHeightPercent(): Float? = null

    open fun getContentHeightPercent(): Float? = null

    private val mFooterContainerHeight by lazy {
        if (getFooterHeightPercent() != null) ViewGroup.LayoutParams.MATCH_PARENT else ViewGroup.LayoutParams.WRAP_CONTENT
    }

    /**
     * 添加底部布局.
     * 以[getFooterContainerLayoutId]为主,若有则优先加载LayoutId.
     * 否则加载[getFooterContainerVB]布局ViewBinding
     */
    private fun addFooterContainer() {
        if (mFooterBinding != null) {
            Timber.v("addLastFooterContainer()")
            mBinding!!.containerFooter.addView(mFooterBinding!!.root,
                ViewGroup.LayoutParams.MATCH_PARENT,
                mFooterContainerHeight)
            return
        }
        val footerLayoutId = getFooterContainerLayoutId()
        val footerViewBinding = getFooterContainerVB()
        if (footerLayoutId != null || footerViewBinding != null) {
            val footerContainer = mBinding!!.containerFooter
            getFooterHeightPercent()?.let {
                Timber.d("${javaClass.simpleName} footerHeightPercent = $it")
                val parameter = footerContainer.layoutParams as ConstraintLayout.LayoutParams
                parameter.matchConstraintPercentHeight = it
                footerContainer.layoutParams = parameter
            }
            footerContainer.visibility = View.VISIBLE
            val view: View? = when {
                footerLayoutId != null -> {
                    mInflater.inflate(footerLayoutId, footerContainer, false)
                }
                footerViewBinding != null -> {
                    mFooterBinding = createViewBinding(footerViewBinding, mInflater, footerContainer)
                    mFooterBinding!!.root
                }
                else -> {
                    null
                }
            }
            view?.apply {
                Timber.v("addFooterContainer()")
                mBinding!!.containerFooter.addView(this,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    mFooterContainerHeight)
            }

        }
    }


    @Suppress("UNCHECKED_CAST")
    private fun <VB: ViewBinding>createViewBinding(aClass: Class<out VB>, inflater: LayoutInflater, container: ViewGroup, attachToRoot: Boolean = false): VB {
        val method = aClass.getDeclaredMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
        return method.invoke(null, inflater, container, attachToRoot) as VB
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (getFooterContainerLayoutId() != null || getFooterContainerVB() != null) {
            mBinding!!.containerFooter.removeAllViews()
        }
        if(getHeaderContainerLayoutId() != null || getHeaderContainerVB() != null) {
            mBinding!!.containerHeader.removeAllViews()
        }
        mBinding?.containerContent?.removeAllViews()
    }

    abstract fun getContentVB(): Class<out MB>

    open fun getHeaderContainerVB(): Class<out HB>? = null

    open fun getHeaderContainerLayoutId(): Int? = null

    open fun getFooterContainerVB(): Class<out FB>? = null

    open fun getFooterContainerLayoutId(): Int? = null

    override fun getViewBindingClass(): Class<out FragmentCustomDialogBinding> {
        return FragmentCustomDialogBinding::class.java
    }

    override fun getFatherClass(): Class<out Any> {
        return CustomDialogFragment::class.java
    }
}