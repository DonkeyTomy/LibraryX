package com.tomy.lib.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewbinding.ViewBinding
import com.tomy.lib.ui.databinding.ActivityBaseFragmentBinding
import com.zzx.utils.context.getViewBinding
import timber.log.Timber


/**@author Tomy
 * Created by Tomy on 3/12/2020.
 */
open class FragmentHeadBottomActivity<HV: ViewBinding, BV: ViewBinding>: FragmentContainerBaseActivity<ActivityBaseFragmentBinding>() {

    var mHeadBinding: HV? = null

    var mBottomBinding: BV? = null

    private val mHeaderContainerHeight by lazy {
        if (getHeadHeightPercent() != null) ViewGroup.LayoutParams.MATCH_PARENT else ViewGroup.LayoutParams.WRAP_CONTENT
    }

    private val mBottomContainerHeight by lazy {
        if (getBottomHeightPercent() != null) ViewGroup.LayoutParams.MATCH_PARENT else ViewGroup.LayoutParams.WRAP_CONTENT
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addHeadContainer()
        addBottomContainer()
    }

    override fun onDestroy() {
        if (getBottomContainerLayoutId() != null || getBottomContainerVB() != null) {
            mBinding.bottomContainer.removeAllViews()
        }
        if(getHeadContainerLayoutId() != null || getHeadContainerVB() != null) {
            mBinding.headContainer.removeAllViews()
        }
        super.onDestroy()
    }

    /**
     * 添加顶部部布局.
     * 以[getHeadContainerLayoutId]为主,若有则优先加载LayoutId.
     * 否则加载[getHeadContainerVB]布局ViewBinding
     */
    private fun addHeadContainer() {
        if (mHeadBinding != null) {
            mBinding.headContainer.addView(mHeadBinding!!.root,
                ViewGroup.LayoutParams.MATCH_PARENT,
                mHeaderContainerHeight)
            return
        }
        val headLayoutId = getHeadContainerLayoutId()
        val headViewBinding = getHeadContainerVB()
        if (headLayoutId != null || headViewBinding != null) {
            Timber.d("${this.javaClass.simpleName} addHeadContainer()")
            val headContainer = mBinding.headContainer
            getHeadHeightPercent()?.let {
                Timber.d("${this.javaClass.simpleName} headHeightPercent = $it")
                val parameter = headContainer.layoutParams as ConstraintLayout.LayoutParams
                parameter.matchConstraintPercentHeight = it
                headContainer.layoutParams = parameter
            }
            headContainer.visibility = View.VISIBLE
            val view: View? = when {
                headLayoutId != null -> {
                    LayoutInflater.from(this).inflate(headLayoutId, headContainer, false)
                }
                headViewBinding != null -> {
                    val method = headViewBinding.getDeclaredMethod(
                            "inflate",
                            LayoutInflater::class.java,
                            ViewGroup::class.java,
                            Boolean::class.java
                    )
                    mHeadBinding = method.invoke(null, LayoutInflater.from(this), headContainer, false) as HV
                    mHeadBinding!!.root
                }
                else -> {
                    null
                }
            }
            view?.apply {
                mBinding.headContainer.addView(this,
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
        if (mBottomBinding != null) {
            mBinding.bottomContainer.addView(mBottomBinding!!.root,
                ViewGroup.LayoutParams.MATCH_PARENT,
                mBottomContainerHeight)
            return
        }
        val bottomLayoutId = getBottomContainerLayoutId()
        val bottomViewBinding = getBottomContainerVB()
        if (bottomLayoutId != null || bottomViewBinding != null) {
            Timber.d("${this.javaClass.simpleName} addBottomContainer()")
            val bottomContainer = mBinding.bottomContainer
            getBottomHeightPercent()?.let {
                Timber.d("${this.javaClass.simpleName} headHeightPercent = $it")
                val parameter = bottomContainer.layoutParams as ConstraintLayout.LayoutParams
                parameter.matchConstraintPercentHeight = it
                bottomContainer.layoutParams = parameter
            }
            bottomContainer.visibility = View.VISIBLE
            val view: View? = when {
                bottomLayoutId != null -> {
                    LayoutInflater.from(this).inflate(bottomLayoutId, bottomContainer, false)
                }
                bottomViewBinding != null -> {
                    val method = bottomViewBinding.getDeclaredMethod(
                            "inflate",
                            LayoutInflater::class.java,
                            ViewGroup::class.java,
                            Boolean::class.java
                    )
                    mBottomBinding = method.invoke(null, LayoutInflater.from(this), bottomContainer, false) as BV
                    mBottomBinding!!.root
                }
                else -> {
                    null
                }
            }
            view?.apply {
                mBinding.bottomContainer.addView(this,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    mHeaderContainerHeight)
            }

        }
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

    fun hideBottomContainer() {
        mBinding.bottomContainer.visibility = View.GONE
    }

    open fun showBottomContainer() {
        mBinding.bottomContainer.visibility = View.VISIBLE
    }

    fun hideHeadContainer() {
        mBinding.headContainer.visibility = View.GONE
    }

    open fun showHeadContainer() {
        mBinding.headContainer.visibility = View.VISIBLE
    }

    override fun getBinding(): ActivityBaseFragmentBinding {
        return getViewBinding<FragmentHeadBottomActivity<HV, BV>, BaseActivity, ActivityBaseFragmentBinding>(layoutInflater, null)!!
    }

}