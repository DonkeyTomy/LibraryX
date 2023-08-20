package com.tomy.compose.animation

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.unit.IntOffset

object NavigationTransition {
    private val offsetAnimation: FiniteAnimationSpec<IntOffset> = tween(
        easing = LinearOutSlowInEasing,
        durationMillis = 700,
    )

    private val floatAnimation: FiniteAnimationSpec<Float> = tween(
        easing = LinearOutSlowInEasing,
        durationMillis = 700
    )


    var slideInBottomAnimation =
        slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight },

            animationSpec = offsetAnimation
        )

    var slideOutBottomAnimation =
        slideOutVertically(targetOffsetY = { fullHeight -> fullHeight }, animationSpec = offsetAnimation)

    var slideInTopAnimation =
        slideInVertically(
            initialOffsetY = { fullHeight -> -fullHeight },

            animationSpec = offsetAnimation
        )

    var slideOutTopAnimation =
        slideOutVertically(targetOffsetY = { fullHeight -> -fullHeight}, animationSpec = offsetAnimation)

    var slideInRightAnimation =
        slideInHorizontally(
            initialOffsetX = {fullWidth -> fullWidth },
            animationSpec = offsetAnimation
        )

    var slideOutRightAnimation =
        slideOutHorizontally(
            targetOffsetX = {fullWidth -> fullWidth },
            animationSpec = offsetAnimation
        )

    var slideInLeftAnimation =
        slideInHorizontally(
            initialOffsetX = {fullWidth -> -fullWidth},
            animationSpec = offsetAnimation
        )

    var slideOutLeftAnimation =
        slideOutHorizontally(
            targetOffsetX = {fullWidth -> -fullWidth },
            animationSpec = offsetAnimation
        )

    var fadeInAnimation = fadeIn(animationSpec = floatAnimation)

    var fadeOutAnimation = fadeOut(animationSpec = floatAnimation)
}
