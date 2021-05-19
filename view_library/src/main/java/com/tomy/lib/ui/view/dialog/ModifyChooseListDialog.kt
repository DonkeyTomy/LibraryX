package com.tomy.lib.ui.view.dialog

import android.content.DialogInterface
import com.coder.zzq.smartshow.dialog.ChooseListDialog

/**@author Tomy
 * Created by Tomy on 23/3/2021.
 */
class ModifyChooseListDialog: ChooseListDialog() {

    override fun onConfirmBtnClick() {
        if (mListView.checkedItemCount <= 0) {
            dismiss()
            return
        }
        super.onConfirmBtnClick()
    }

    override fun onCancelBtnClick() {
        if (mListView.checkedItemCount <= 0) {
            dismiss()
            return
        }
        super.onCancelBtnClick()
    }

    fun clearSelect() {
        mListView.clearChoices()
    }

    fun setCheckPos(position: Int, check: Boolean) {
        mListView.setItemChecked(position, check)
    }

    fun setOnKeyListener(onKeyListener: DialogInterface.OnKeyListener?) {
        mNestedDialog.setOnKeyListener(onKeyListener)
    }
}