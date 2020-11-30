package com.tomy.lib.ui.view.layout

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.google.android.material.tabs.TabLayout
import com.tomy.lib.ui.R

/**@author Tomy
 * Created by Tomy on 2018/11/29.
 * tabLayout.id = [android.R.id.tabhost]
 */
open class TabBarLayout(context: Context, attrs: AttributeSet): TabLayout(context, attrs) {
    private var mInfos: Array<CharSequence>? = null
    private var mIcons: Array<Drawable>? = null

    private var mTabLayoutId = R.layout.tab_item_m9

    init {
        context.obtainStyledAttributes(attrs, R.styleable.TabBarLayout).apply {
            mInfos = getTextArray(R.styleable.TabBarLayout_infos)
            mTabLayoutId = getResourceId(R.styleable.TabBarLayout_tabLayout, 0)
            recycle()
        }
        refreshView()
    }

    fun setInfoList(infos: Array<CharSequence>) {
        mInfos = infos
    }

    fun setInfoList(arrayId: Int) {
        val infos = resources.getStringArray(arrayId)
        mInfos = Array(infos.size) {
            return@Array ""
        }
        for (i in infos.indices) {
            mInfos?.set(i, infos[i])
        }
        refreshView()
    }

    fun refreshView() {
        mInfos?.forEach {
            addTab(newTab().apply {
                setCustomView(mTabLayoutId)
                text = it
            })
        }
    }

    //返回自定义的Tab的布局Id.
    fun setTabLayoutId(layoutId: Int) {
        mTabLayoutId = layoutId
    }

}