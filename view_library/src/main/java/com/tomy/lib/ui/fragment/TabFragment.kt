package com.tomy.lib.ui.fragment

import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
import com.tomy.lib.ui.R
import com.tomy.lib.ui.databinding.MainTabBinding
import timber.log.Timber

/**@author Tomy
 * Created by Tomy on 2018/11/29.
 */
abstract class TabFragment: BaseFragmentViewBind<MainTabBinding>() {


    override fun initView(root: View) {
        initTabLayout()
        initWidgetFrame()
    }

    private fun initTabLayout() {
        mBinding!!.toolBar.apply {
            Timber.e("$this")
            layoutParams = layoutParams.apply {
                this as LayoutParams
                if (isTabLayoutTop()) {
                    topToTop = LayoutParams.PARENT_ID
                } else {
                    bottomToBottom = LayoutParams.PARENT_ID
                }
            }
        }
        mBinding!!.tabBarLayout.setInfoList(getInfoArrayId())
    }

    private fun initWidgetFrame() {
        mBinding!!.widgetFrame.apply {
            layoutParams = layoutParams.apply {
                this as LayoutParams
                if (isTabLayoutTop()) {
                    topToBottom     = R.id.toolBar
                    bottomToBottom  = LayoutParams.PARENT_ID
                } else {
                    bottomToTop     = R.id.toolBar
                    topToTop        = LayoutParams.PARENT_ID
                }
            }
        }
        val widgetLayoutId = getWidgetFrameLayoutId()
        if (widgetLayoutId > 0) {
            mBinding!!.widgetFrame.addView(LayoutInflater.from(mContext).inflate(widgetLayoutId, mBinding!!.widgetFrame, false))
        }
    }

    /*override fun getLayoutId(): Int {
        return R.layout.main_tab
    }*/

    override fun isBindView(): Boolean {
        return false
    }

    /**
     * @return TabItem显示信息队列的id R.array.*
     */
    abstract fun getInfoArrayId(): Int

    /**
     * @return WidgetFrame的布局Id.
     */
    open fun getWidgetFrameLayoutId() = 0

    /**
     * @return 表明TabLayout是否在顶部.默认为true.
     */
    open fun isTabLayoutTop() = true
}