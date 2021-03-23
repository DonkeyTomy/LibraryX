package com.tomy.lib.ui.view.dialog

import com.coder.zzq.smartshow.dialog.ChooseListDialog

/**@author Tomy
 * Created by Tomy on 23/3/2021.
 */
class ModifyChooseListDialog: ChooseListDialog() {

    override fun onConfirmBtnClick() {
        if (mListView.checkedItemCount <= 0) {
            return
        }
        super.onConfirmBtnClick()
    }

    fun clearSelect() {
        mListView.clearChoices()
    }

}