package com.tomy.lib.ui.view.dialog

import android.view.View
import androidx.annotation.ColorInt
import com.tomy.lib.ui.databinding.ContainerMessageBinding
import timber.log.Timber

/**@author Tomy
 * Created by Tomy on 18/5/2021.
 */
class CustomTitleDialog: CustomConfirmDialog<ContainerMessageBinding>() {

    private var mMsg: String? = null
    @ColorInt
    private var mMsgColor: Int? = null
    private var mMsgTextSizeSp: Float? = null
    private var mMsgTextBold = false

    fun msgStyle(color: Int? = null, textSizeSp: Float? = null, bold: Boolean = false): CustomTitleDialog {
        mMsgColor = color
        mMsgTextSizeSp = textSizeSp
        mMsgTextBold = bold
        applyMsgStyle()
        return this
    }

    fun applyMsgStyle() {
        mContentBinding?.tvContent?.run {
            mMsgColor?.let {
                setTextColor(resources.getColor(it))
            }
            mMsgTextSizeSp?.let {
                textSize = it
            }
            paint.isFakeBoldText = mMsgTextBold
        }
    }

    fun message(msg: String? = null): CustomTitleDialog {
        mMsg = msg
        applyMessage()
        return this
    }

    fun applyMessage() {
        mContentBinding?.run {
            root.visibility  = if (mMsg == null) View.GONE else View.VISIBLE
            msg = mMsg
            Timber.v("applyMessage(): $mMsg")
        }
    }

    override fun applyContent() {
        applyMessage()
        applyMsgStyle()
    }

    override fun getContentVB(): Class<out ContainerMessageBinding> {
        return ContainerMessageBinding::class.java
    }

}