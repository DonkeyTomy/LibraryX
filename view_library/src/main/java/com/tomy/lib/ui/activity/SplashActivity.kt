package com.tomy.lib.ui.activity

import android.content.Intent
import android.os.Bundle
import com.tomy.lib.ui.databinding.ActivitySplashBinding
import com.zzx.utils.context.getViewBinding


/**@author Tomy
 * Created by Tomy on 19/11/2020.
 */
abstract class SplashActivity: FragmentContainerBaseActivity<ActivitySplashBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!isTaskRoot) {
            intent?.apply {
                if (hasCategory(Intent.CATEGORY_LAUNCHER) && action == Intent.ACTION_MAIN) {
                    finish()
                    return
                }
            }
        }
        super.onCreate(savedInstanceState)
    }

    override fun getBinding(): ActivitySplashBinding {
        return getViewBinding<SplashActivity, BaseActivity, ActivitySplashBinding>(layoutInflater, null)!!
    }

}