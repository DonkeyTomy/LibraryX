package com.tomy.compose.fragment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.tomy.compose.components.custom.VerticalFixResIntContent

abstract class BaseGridComposeFragment: BaseComposeFragment() {

    @Composable
    override fun CreateContent() {
        Surface(
            modifier = Modifier.background(Color.Gray)
        ) {
            VerticalFixResIntContent(
                resArrayId = getArrayResId(),
                columnCount = 2,
                onItemClick = ::onItemClick,
                verticalArrangement = Arrangement.Top
            ) { resId, modifier ->
                CreateItem(
                    resId = resId,
                    modifier = modifier
                )
            }
        }
    }

    abstract fun getArrayResId(): Int

    abstract fun onItemClick(index: Int, item: Int)

    @Composable
    abstract fun CreateItem(resId: Int, modifier: Modifier)

}