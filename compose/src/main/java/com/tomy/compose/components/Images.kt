package com.tomy.compose.components

import androidx.annotation.DrawableRes
import androidx.annotation.IntDef
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet

@Composable
fun TextWithIcon(
    modifier: Modifier = Modifier,
    @DrawableRes
    imageRes: Int,
    text: String,
    @DrawDirection
    slideDirection: Int,
    textStyle: TextStyle = LocalTextStyle.current,
    margin: Dp = 5.dp
) {
    val constraintSet = ConstraintSet {
        val image   = createRefFor("icon")
        val text    = createRefFor("text")
        when (slideDirection) {
            DRAW_TOP -> {
                createVerticalChain(image, text)
            }
            DRAW_BOTTOM -> {
                createVerticalChain(text, image)
            }
            DRAW_START -> {
                createHorizontalChain(image, text)
            }
            DRAW_END -> {
                createHorizontalChain(text, image)
            }
        }

        constrain(image) {
            when (slideDirection) {
                DRAW_TOP -> {
                    top.linkTo(parent.top)
                    centerHorizontallyTo(parent)
                }
                DRAW_BOTTOM -> {
                    bottom.linkTo(parent.bottom)
                    centerHorizontallyTo(parent)
                }
                DRAW_START -> {
                    start.linkTo(parent.start)
                    centerVerticallyTo(parent)
                }
                DRAW_END -> {
                    end.linkTo(parent.end)
                    centerVerticallyTo(parent)
                }
            }
        }
        constrain(text) {
            when (slideDirection) {
                DRAW_BOTTOM -> {
                    top.linkTo(parent.top)
                    centerHorizontallyTo(parent)
                }
                DRAW_TOP -> {
                    bottom.linkTo(parent.bottom)
                    centerHorizontallyTo(parent)
                }
                DRAW_END -> {
                    start.linkTo(parent.start)
                    centerVerticallyTo(parent)
                }
                DRAW_START -> {
                    end.linkTo(parent.end)
                    centerVerticallyTo(parent)
                }
            }
        }
    }

    ConstraintLayout(
        modifier = modifier.wrapContentSize(),
        constraintSet = constraintSet
    ) {
        Image(
            modifier = Modifier.layoutId("icon"),
            painter = painterResource(id = imageRes),
            contentDescription = ""
        )
        Text(
            modifier = Modifier.layoutId("text"),
            text = text,
            style = textStyle,
            color = LocalContentColor.current
        )
    }
}

const val DRAW_TOP      = 0
const val DRAW_BOTTOM   = 1
const val DRAW_START    = 2
const val DRAW_END      = 3

@IntDef(DRAW_TOP, DRAW_BOTTOM, DRAW_START, DRAW_END)
@Retention(AnnotationRetention.SOURCE)
annotation class DrawDirection
