package com.coder.vincent.smart_toast.compact

import android.view.View
import com.coder.vincent.smart_toast.factory.ToastConfig

class ToastWindowStrategySelector {
    fun select(
        toastView: View,
        config: ToastConfig,
        configApplyCallback: (View, ToastConfig) -> Unit
    ): CompactToast =
        /*when {
            Toolkit.isSystemAlertWindowEnabled() -> {
                Log.i("Toolkit", "isSystemAlertWindowEnabled")
                SystemWindowToast(
                    toastView,
                    config,
                    configApplyCallback
                )
            }

            Toolkit.isNotificationPermitted() -> {
                Log.i("Toolkit", "isNotificationPermitted")

                OriginalToast(
                    toastView,
                    config,
                    configApplyCallback
                )
            }

            else -> {
                Log.i("Toolkit", "DialogWindowToast")
*/
                DialogWindowToast(
                    toastView,
                    config,
                    configApplyCallback
                )
//            }
//        }
}