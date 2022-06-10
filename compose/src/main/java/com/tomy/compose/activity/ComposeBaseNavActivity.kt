package com.tomy.compose.activity

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.navigation.findNavController
import com.tomy.compose.databinding.MainActivityComposeBinding
import timber.log.Timber

abstract class ComposeBaseNavActivity: ComposeBaseActivity() {

    @Composable
    override fun CreateContent(paddingValues: PaddingValues) {
        Timber.d("paddingValues: $paddingValues")
        AndroidViewBinding(
            MainActivityComposeBinding::inflate,
            modifier = Modifier
                .padding(paddingValues)
                .statusBarsPadding()
                .navigationBarsPadding(),
            update = {
                mainNavHost.findNavController().setGraph(getNavResId())
            }
        )
    }

    abstract fun getNavResId(): Int

}