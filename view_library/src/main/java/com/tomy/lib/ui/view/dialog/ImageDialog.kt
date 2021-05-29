package com.tomy.lib.ui.view.dialog

import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.tomy.lib.ui.databinding.FragmentImageDialogBinding
import com.tomy.lib.ui.view.SmoothImageView
import timber.log.Timber

/**@author Tomy
 * Created by Tomy on 28/5/2021.
 */
class ImageDialog: CustomDialogFragment<FragmentImageDialogBinding, ViewBinding, ViewBinding>() {

    private var mImageConfig: SmoothImageView.ImageConfig? = null


    override fun applyContent() {
        mImageConfig = arguments?.getParcelable(IMAGE_CONFIG)
        Timber.v("applyContent(): $mImageConfig")
        mContentBinding?.smoothImageView?.apply {
            mImageConfig?.let {
                Glide.with(this)
                    .load(it.imagePath)
                    .priority(Priority.IMMEDIATE)
                    .into(this@apply)
                setOriginalInfo(it.width, it.height, it.locationX, it.locationY)
                transformIn()
            }
        }
    }

    override fun getContentVB(): Class<out FragmentImageDialogBinding> {
        return FragmentImageDialogBinding::class.java
    }

    companion object {
        const val IMAGE_CONFIG  = "imageConfig"
    }

}