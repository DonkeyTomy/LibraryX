package com.tomy.lib.ui.view.dialog

import android.os.Bundle
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.viewbinding.ViewBinding
import com.tomy.lib.ui.R
import com.tomy.lib.ui.databinding.ContainerFooterConfirmBtnBinding
import com.tomy.lib.ui.databinding.ContainerTitleBinding
import timber.log.Timber

/**@author Tomy
 * Created by Tomy on 18/5/2021.
 */
abstract class CustomConfirmDialog<MB: ViewBinding>: CustomDialogFragment<MB, ContainerTitleBinding, ContainerFooterConfirmBtnBinding>(),
    View.OnClickListener {

    private var mOnDialogBtnListener:OnDialogBtnClickListener? = null

    private var mTitle: String? = null
    @StringRes
    private var mTitleId: Int? = null
    @ColorInt
    private var mTitleColor: Int? = null
    private var mTitleTextSizeSp: Float? = null
    private var mTitleBold = false

    private var mConfirmLabel: String?    = null
    @StringRes
    private var mConfirmLabelId: Int?     = null
    @ColorInt
    private var mConfirmBtnColor: Int?    = null
    @DrawableRes
    private var mConfirmBtnBgId: Int?     = null
    @ColorInt
    private var mConfirmBtnBgColor: Int?  = null

    private var mCancelLabel: String?     = null
    @StringRes
    private var mCancelLabelId: Int?      = null
    @ColorInt
    private var mCancelBtnColor: Int?     = null
    @DrawableRes
    private var mCancelBtnBgId: Int?      = null
    @ColorInt
    private var mCancelBtnBgColor: Int?   = null

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

    fun title(title: Int): CustomConfirmDialog<MB> {
        mTitleId = title
        applyTitle()
        return this
    }

    fun title(title: String?): CustomConfirmDialog<MB> {
        mTitle = title
        applyTitle()
        return this
    }


    protected fun applyTitle() {
        mHeaderBinding?.run {
            root.visibility  = if (mTitle == null) View.GONE else View.VISIBLE
            title = mTitle
            Timber.v("applyTitle(): $mTitle")
        }
    }

    fun titleStyle(color: Int? = null, textSizeSp: Float? = null, bold: Boolean = false): CustomConfirmDialog<MB> {
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

    fun confirmBtn(btnLabel: String? = null, color: Int? = null, backgroundId: Int? = null, backgroundColorId: Int? = null): CustomConfirmDialog<MB> {
        mConfirmLabel       = btnLabel
        mConfirmBtnColor    = color
        mConfirmBtnBgId     = backgroundId
        mConfirmBtnBgColor  = backgroundColorId
        applyConfirmBtn()
        return this
    }

    fun confirmBtn(btnLabel: Int, color: Int? = null, backgroundId: Int? = null, backgroundColorId: Int? = null): CustomConfirmDialog<MB> {
        mConfirmLabelId     = btnLabel
        mConfirmBtnColor    = color
        mConfirmBtnBgId     = backgroundId
        mConfirmBtnBgColor  = backgroundColorId
        applyConfirmBtn()
        return this
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

    fun cancelBtn(cancelLabel: String? = null, color: Int? = null, backgroundId: Int? = null, backgroundColorId: Int? = null): CustomConfirmDialog<MB> {
        mCancelLabel    = cancelLabel
        mCancelBtnColor = color
        mCancelBtnBgId  = backgroundId
        mCancelBtnBgColor = backgroundColorId
        applyCancelBtn()
        return this
    }

    fun cancelBtn(cancelLabel: Int, color: Int? = null, backgroundId: Int? = null, backgroundColorId: Int? = null): CustomConfirmDialog<MB> {
        mCancelLabelId  = cancelLabel
        mCancelBtnColor = color
        mCancelBtnBgId  = backgroundId
        mCancelBtnBgColor = backgroundColorId
        applyCancelBtn()
        return this
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

    fun setDialogBtnListener(listener: OnDialogBtnClickListener?): CustomConfirmDialog<MB> {
        mOnDialogBtnListener = listener
        return this
    }

    override fun getFooterContainerVB(): Class<out ContainerFooterConfirmBtnBinding>? {
        return ContainerFooterConfirmBtnBinding::class.java
    }

    override fun getHeaderContainerVB(): Class<out ContainerTitleBinding>? {
        return ContainerTitleBinding::class.java
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