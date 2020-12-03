package com.tomy.lib.ui.view.dialog

import android.view.View
import android.widget.CompoundButton
import com.tomy.lib.ui.R
import com.tomy.lib.ui.databinding.DialogLayoutBinding
import com.tomy.lib.ui.fragment.BaseDialogFragment

/**@author Tomy
 * Created by Tomy on 15/9/2020.
 */
class ConfirmDialog: BaseDialogFragment<DialogLayoutBinding>() {

    var content: String? = null
    var contentId: Int? = null
    var contentBackground: Int? = null
    var title: String? = null
    var titleId: Int? = null
    var cbContent: String? = null
    var btnNegativeTitle: Int? = R.string.cancel
    var btnNegativeClickable = true
    var btnPositiveTitle: Int?    = R.string.positive
    var btnPositiveClickable = true
    var checkChangedListener: CompoundButton.OnCheckedChangeListener? = null
    var initChecked = false

    /*override fun getLayoutId(): Int {
        return R.layout.dialog_layout
    }*/

    override fun bindView() {
        mBinding?.apply {
            if (contentId == null) {
                content?.let {
                    tvContent.text = it
                }
            }
            contentId?.let { tvContent.setText(it) }
            contentBackground?.let { tvContent.setBackgroundResource(it) }
            if (title == null && titleId == null) {
                tvTitle.visibility = View.GONE
            } else if (titleId != null) {
                tvTitle.setText(titleId!!)
            } else if (title != null) {
                tvTitle.text = title!!
            }
            cbContent?.let {
                checkBox.apply {
                    text = it
                    isChecked = initChecked
                    visibility = View.VISIBLE
                    setOnCheckedChangeListener(checkChangedListener)
                }
            }
            if (btnNegativeTitle == null) {
                btnNegative.visibility = View.GONE
            } else {
                btnNegative.setText(btnNegativeTitle!!)
            }
            btnNegative.isClickable = btnNegativeClickable
            if (btnPositiveTitle == null) {
                btnPositive.visibility = View.GONE
            } else {
                btnPositive.setText(btnPositiveTitle!!)
            }
            btnPositive.isClickable = btnPositiveClickable

            if (btnNegativeClickable) {
                btnNegative.setOnClickListener {
                    negativeCallback?.invoke()
                    dismissDialog()
                }
            }
            if (btnPositiveClickable) {
                btnPositive.setOnClickListener {
                    dismissDialog()
                    positiveCallback?.invoke()
                }
            }
        }
    }


}