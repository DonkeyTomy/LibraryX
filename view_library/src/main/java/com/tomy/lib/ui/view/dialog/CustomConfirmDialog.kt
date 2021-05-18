package com.tomy.lib.ui.view.dialog

import android.os.Bundle
import android.view.View
import androidx.viewbinding.ViewBinding
import com.tomy.lib.ui.R
import com.tomy.lib.ui.databinding.ContainerFooterConfirmBtnBinding
import com.tomy.lib.ui.databinding.ContainerTitleBinding

/**@author Tomy
 * Created by Tomy on 18/5/2021.
 */
abstract class CustomConfirmDialog<MB: ViewBinding>: CustomDialogFragment<MB, ContainerTitleBinding, ContainerFooterConfirmBtnBinding>(),
    View.OnClickListener {

    private var mOnDialogBtnListener:OnDialogBtnClickListener? = null

    private var mTitle: String? = null

    private var mTitleColor: Int? = null
    private var mTitleTextSizeSp: Float? = null
    private var mTitleBold = false

    protected var mConfirmLabel: String?    = null
    protected var mConfirmBtnColor: Int?    = null
    protected var mConfirmBtnBgId: Int?     = null
    protected var mConfirmBtnBgColor: Int?  = null

    protected var mCancelLabel: String?     = null
    protected var mCancelBtnColor: Int?     = null
    protected var mCancelBtnBgId: Int?      = null
    protected var mCancelBtnBgColor: Int?   = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFooterBinding?.listener = this
    }

    override fun applyFooterContainer() {
        applyConfirmBtn()
        applyCancelBtn()
    }

    override fun applyHeadContainer() {
        applyTitleStyle()
        applyTitle()
    }

    fun title(title: Int): CustomDialogFragment<MB, ContainerTitleBinding, ContainerFooterConfirmBtnBinding> {
        return title(getString(title))
    }

    fun title(title: String?): CustomDialogFragment<MB, ContainerTitleBinding, ContainerFooterConfirmBtnBinding> {
        mTitle = title
        applyTitle()
        return this
    }


    protected fun applyTitle() {
        mHeaderBinding?.run {
            root.visibility  = if (mTitle == null) View.GONE else View.VISIBLE
            title = mTitle
        }
    }

    fun titleStyle(color: Int? = null, textSizeSp: Float? = null, bold: Boolean = false): CustomDialogFragment<MB, ContainerTitleBinding, ContainerFooterConfirmBtnBinding> {
        mTitleColor = color
        mTitleTextSizeSp = textSizeSp
        mTitleBold = bold
        applyTitleStyle()
        return this
    }

    protected open fun applyTitleStyle() {
        mHeaderBinding?.tvTitle?.run {
            mTitleColor?.let {
                setTextColor(resources.getColor(it))
            }
            mTitleTextSizeSp?.let {
                textSize = it
            }
            paint.isFakeBoldText = mTitleBold
        }
    }

    fun confirmBtn(btnLabel: String? = null, color: Int? = null, backgroundId: Int? = null, backgroundColorId: Int? = null) {
        mConfirmLabel       = btnLabel
        mConfirmBtnColor    = color
        mConfirmBtnBgId     = backgroundId
        mConfirmBtnBgColor  = backgroundColorId
        applyConfirmBtn()
    }

    fun confirmBtn(btnLabel: Int, color: Int? = null, backgroundId: Int? = null, backgroundColorId: Int? = null) {
        confirmBtn(getString(btnLabel), color, backgroundId, backgroundColorId)
    }

    protected open fun applyConfirmBtn() {
        mFooterBinding?.let { binding ->
            binding.btnPositive.run {
                if (mConfirmLabel?.apply {
                        text    = mConfirmLabel
                        mConfirmBtnBgColor?.let {
                            setBackgroundColor(resources.getColor(it))
                        }
                        mConfirmBtnColor?.let {
                            setTextColor(resources.getColor(it))
                        }
                        mConfirmBtnBgId?.let {
                            setBackgroundResource(it)
                        }
                    } == null) {
                    visibility  = View.GONE
                }
            }
        }
        checkFooterLayout()
    }

    fun cancelBtn(cancelLabel: String? = null, color: Int? = null, backgroundId: Int? = null, backgroundColorId: Int? = null) {
        mCancelLabel    = cancelLabel
        mCancelBtnColor = color
        mCancelBtnBgId  = backgroundId
        mCancelBtnBgColor = backgroundColorId
        applyCancelBtn()
    }

    fun cancelBtn(cancelLabel: Int, color: Int? = null, backgroundId: Int? = null, backgroundColorId: Int? = null) {
        cancelBtn(getString(cancelLabel), color, backgroundId, backgroundColorId)
    }

    protected open fun applyCancelBtn() {
        mFooterBinding?.let { binding ->
            binding.btnNegative.run {
                if (mCancelLabel?.apply {
                        text    = mCancelLabel
                        mCancelBtnBgColor?.let {
                            setBackgroundColor(resources.getColor(it))
                        }
                        mCancelBtnColor?.let {
                            setTextColor(resources.getColor(it))
                        }
                        mCancelBtnBgId?.let {
                            setBackgroundResource(it)
                        }
                    } == null) {
                    visibility  = View.GONE
                }
            }
        }
        checkFooterLayout()
    }

    /**
     * 判断底部是否需要隐藏.当两个按键都未设置文字时就隐藏.
     */
    protected open fun checkFooterLayout() {
        mFooterVisible  = mCancelLabel != null || mConfirmLabel != null
        applyFooterVisible()
    }

    fun setDialogBtnListener(listener: OnDialogBtnClickListener?): CustomDialogFragment<MB, ContainerTitleBinding, ContainerFooterConfirmBtnBinding> {
        mOnDialogBtnListener = listener
        return this
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnPositive    -> {
                mOnDialogBtnListener?.onClick(true)
            }
            R.id.btnNegative    -> {
                mOnDialogBtnListener?.onClick(false)
            }
        }
    }

    interface OnDialogBtnClickListener {
        fun onClick(positive: Boolean)
    }

}