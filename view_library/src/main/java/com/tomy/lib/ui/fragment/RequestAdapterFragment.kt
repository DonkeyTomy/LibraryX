package com.tomy.lib.ui.fragment

import androidx.databinding.ViewDataBinding
import com.tomy.lib.ui.R
import com.zzx.utils.network.NetworkUtil

/**@author Tomy
 * Created by Tomy on 14/9/2020.
 */
abstract class RequestAdapterFragment<T, DB: ViewDataBinding>: BaseAdapterFragment<T, DB>() {

    fun checkWifiNotConnect(): Boolean {
        return if (!NetworkUtil.isWifiConnected(mContext!!)) {
            showToast(R.string.no_wifi_connect)
            true
        } else {
            false
        }
    }

}