package com.tomy.compose.activity

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.navigation.findNavController
import com.tomy.compose.databinding.MainActivityComposeBinding
import timber.log.Timber

abstract class ComposeScaffoldBaseNavActivity: ComposeScaffoldBaseActivity() {

    @Composable
    override fun CreateContent(paddingValues: PaddingValues) {
        Timber.d("paddingValues: $paddingValues")
        AndroidViewBinding(
            MainActivityComposeBinding::inflate,
            modifier = Modifier
                .padding(paddingValues),
            update = {
                mainNavHost.findNavController().setGraph(getNavResId())
            }
        )
    }

    abstract fun getNavResId(): Int

}