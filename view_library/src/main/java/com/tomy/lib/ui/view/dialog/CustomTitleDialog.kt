package com.tomy.lib.ui.view.dialog

import com.tomy.lib.ui.databinding.ContainerMessageBinding

/**@author Tomy
 * Created by Tomy on 18/5/2021.
 */
class CustomTitleDialog: CustomConfirmDialog<ContainerMessageBinding>() {

    override fun getContentVB(): Class<out ContainerMessageBinding> {
        return ContainerMessageBinding::class.java
    }

    override fun bindView() {
    }

}