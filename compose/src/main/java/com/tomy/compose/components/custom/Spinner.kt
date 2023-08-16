package com.tomy.compose.components.custom

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

/**@author Tomy
 * Created by Tomy on 2023/7/12.
 */

@Composable
fun TextSpinner(
    modifier: Modifier = Modifier,
    dropdownModifier: Modifier = Modifier,
    itemList: List<String>,
    selectedItem: String,
    onItemSelectedListener: (Int, String) -> Unit
) {
    Spinner(
        modifier = modifier,
        dropdownModifier = dropdownModifier,
        itemList = itemList,
        selectedItem = selectedItem,
        onItemSelectedListener = onItemSelectedListener,
        selectedItemFactory = { selectModifier, msg ->
            Text(
                modifier = selectModifier,
                text = msg, style =
                MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    ) { msg, _ ->
        Text(
            text = msg,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}


/**
 * @param modifier Modifier 整个选择列的配置
 * @param dropdownModifier Modifier 弹出的选择项的配置
 * @param itemList List<T> 弹出的数据列表
 * @param selectedItem T 选中的数据
 * @param onItemSelectedListener Function2<Int, T, Unit>
 * @param selectedItemFactory [@androidx.compose.runtime.Composable] Function2<Modifier, T, Unit> 已选中的项
 * @param dropdownItemFactory [@androidx.compose.runtime.Composable] Function2<T, Int, Unit> 弹出的选择项
 */
@Composable
fun <T> Spinner(
    modifier: Modifier = Modifier,
    dropdownModifier: Modifier = Modifier,
    itemList: List<T>,
    selectedItem: T,
    onItemSelectedListener: (Int, T) -> Unit,
    selectedItemFactory: @Composable (Modifier, T) -> Unit,
    dropdownItemFactory: @Composable (T, Int) -> Unit,
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    Box(modifier = modifier) {
        selectedItemFactory(
            Modifier.clickable { expanded = true },
            selectedItem
        )

        DropdownMenu(
            modifier = dropdownModifier,
            expanded = expanded, onDismissRequest = { expanded = false }
        ) {
            itemList.forEachIndexed { index, element ->
                DropdownMenuItem(
                    text = {
                        dropdownItemFactory(element, index)
                    },
                    onClick = {
                        onItemSelectedListener(index, element)
                        expanded = false
                    }
                )
            }
        }
    }

}