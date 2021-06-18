package com.tomy.lib.ui.view.dialog

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatDialog
import com.coder.zzq.smartshow.dialog.ChooseListDialog
import timber.log.Timber

/**@author Tomy
 * Created by Tomy on 23/3/2021.
 */
class ModifyChooseListDialog: ChooseListDialog() {

    private var mOnKeyListener: DialogInterface.OnKeyListener? = null

    private var mPositionCheck = 0
    private var mSelectCheck = true

    override fun onConfirmBtnClick() {
        if (mListView.checkedItemCount <= 0) {
            mOnConfirmClickListener.onBtnClick(this, 0, null)
            dismiss()
            return
        }
        super.onConfirmBtnClick()
    }

    override fun onCancelBtnClick() {
        if (mListView.checkedItemCount <= 0) {
            mOnCancelClickListener.onBtnClick(this, 1, null)
            dismiss()
            return
        }
        super.onCancelBtnClick()
    }

    fun clearSelect() {
        mListView.clearChoices()
    }

    fun setCheckPos(position: Int, check: Boolean): ChooseListDialog {
        Timber.v("position = $position")
        mPositionCheck  = position
        mSelectCheck    = check
        return this
    }

    fun setOnKeyListener(onKeyListener: DialogInterface.OnKeyListener?) {
        mOnKeyListener = onKeyListener
        applyOnKeyListener(null)
    }

    override fun applyBody(dialog: AppCompatDialog?) {
        super.applyBody(dialog)
        applyOnKeyListener(dialog)
        Timber.v("applyBody()")
    }

    override fun resetDialogWhenShowAgain(dialog: AppCompatDialog?) {
        super.resetDialogWhenShowAgain(dialog)
        if (mListView != null) {
            mListView.setItemChecked(mPositionCheck, mSelectCheck)
        }
    }

    fun applyOnKeyListener(dialog: AppCompatDialog?) {
        dialog?.setOnKeyListener(mOnKeyListener)
        mNestedDialog?.setOnKeyListener(mOnKeyListener)
    }
}