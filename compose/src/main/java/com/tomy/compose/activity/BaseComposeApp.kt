package com.tomy.compose.activity

import com.tomy.component.activity.BaseApplication
import com.tomy.compose.di.mainModel
import org.koin.android.logger.AndroidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**@author Tomy
 * Created by Tomy on 2022/10/25.
 */
open class BaseComposeApp: BaseApplication() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(mainModel)
            logger(AndroidLogger(Level.INFO))
        }
    }

}