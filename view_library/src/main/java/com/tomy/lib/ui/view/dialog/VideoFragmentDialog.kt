package com.tomy.lib.ui.view.dialog

import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.tomy.lib.ui.activity.removeFragment
import com.tomy.lib.ui.databinding.FragmentDialogVideoBinding
import com.tomy.lib.ui.fragment.VideoFragment

/**@author Tomy
 * Created by Tomy on 29/5/2021.
 */
class VideoFragmentDialog: CustomDialogFragment<FragmentDialogVideoBinding, ViewBinding, ViewBinding>() {

    private var mFilePath: String = ""

    fun setFilePath(filePath: String) {
        mFilePath   = filePath
    }

    override fun applyContent() {
        super.applyContent()
        lifecycleScope.launchWhenResumed {
            (parentFragmentManager.findFragmentByTag(VideoFragment::class.java.name) as VideoFragment).setFilePath(mFilePath)
        }
    }

    override fun onDestroyView() {
        removeFragment<VideoFragment>()
        super.onDestroyView()
    }

    override fun getContentVB(): Class<out FragmentDialogVideoBinding> {
        return FragmentDialogVideoBinding::class.java
    }
}