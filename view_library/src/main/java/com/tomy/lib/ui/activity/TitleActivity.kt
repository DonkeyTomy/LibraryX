package com.tomy.lib.ui.activity

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.tomy.lib.ui.databinding.ActivityTitleBinding
import com.zzx.utils.context.getViewBinding

/**@author Tomy
 * Created by Tomy on 25/11/2020.
 */
open class TitleActivity: FragmentContainerBaseActivity<ActivityTitleBinding>() {

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        setSupportActionBar(mBinding.toolBar)
        val title = intent?.getStringExtra(TITLE)
        if (!title.isNullOrEmpty()) {
            mBinding.toolBar.visibility = View.VISIBLE
            mBinding.title.text = title
        }
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(isDisplayHomeAsUpEnabled())
            setDisplayShowTitleEnabled(false)
        }
    }

    /**
     * 标题栏左侧是否显示向左图标
     * @return Boolean
     */
    open fun isDisplayHomeAsUpEnabled() = false

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun getBinding(): ActivityTitleBinding {
        return getViewBinding<TitleActivity, BaseActivity, ActivityTitleBinding>(layoutInflater, null)!!
    }

    companion object {
        const val TITLE = "title"
    }

}