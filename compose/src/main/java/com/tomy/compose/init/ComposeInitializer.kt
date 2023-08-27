package com.tomy.compose.init

import android.content.Context
import androidx.startup.Initializer
import com.tomy.compose.di.mainModel
import org.koin.android.logger.AndroidLogger
import org.koin.core.context.GlobalContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.koinApplication
import timber.log.Timber

class ComposeInitializer: Initializer<Unit> {
    override fun create(context: Context) {
        if (Timber.treeCount <= 0) {
            Timber.plant(Timber.DebugTree())
        }
        if (GlobalContext.getOrNull() == null) {
            startKoin {
                modules(mainModel)
            }
        } else {
            koinApplication {
                loadKoinModules(mainModel)
                logger(AndroidLogger(Level.INFO))
            }
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}