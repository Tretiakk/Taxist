package com.taxi.taxist

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Ease
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

class Animation {

    var screenHeight = 0.dp
    var screenWidth = 0.dp

    @Composable
    fun Realize(){
        screenWidth = LocalConfiguration.current.screenWidthDp.dp
        screenHeight = LocalConfiguration.current.screenHeightDp.dp
    }

    @Composable
    fun SelfFromBottom(
        modifier: Modifier,
        isVisible: Boolean,
        duration: Int = 400,
        content: @Composable () ->  Unit){
        var isObjectVisible by rememberSaveable { mutableStateOf(true) }
        val offsetY by animateDpAsState(
            targetValue = if (isVisible) 0.dp else screenHeight,
            animationSpec = tween(
                durationMillis = duration,
                easing = EaseInOutCubic
            )
        )

        if (isObjectVisible) {
            Box(
                modifier = modifier
                    .offset {
                        IntOffset(x = 0, y = offsetY.roundToPx())
                    }
            ) {
                content()
            }
        }

        LaunchedEffect(isVisible){
            isObjectVisible = true
        }

        LaunchedEffect(offsetY){
            if (offsetY == screenHeight) {
                isObjectVisible = false
             }
        }
    }

    @Composable
    fun SelfFromRight(
        modifier: Modifier = Modifier,
        isVisible: Boolean,
        duration: Int = 400,
        content: @Composable BoxScope.() ->  Unit){
        var isObjectVisible by rememberSaveable { mutableStateOf(true) }
        val offsetX by animateDpAsState(
            targetValue = if (isVisible) 0.dp else screenWidth,
            animationSpec = tween(
                durationMillis = duration,
                easing = Ease
            )
        )

        if (isObjectVisible) {
            Box(
                modifier = modifier
                    .offset{
                        IntOffset(x = offsetX.roundToPx(), y = 0)
                    }
            ) {
                content()
            }
        }

        LaunchedEffect(isVisible){
            isObjectVisible = true
        }

        LaunchedEffect(offsetX){
            if (offsetX == screenWidth) {
                isObjectVisible = false
            }
        }
    }

    @Composable
    fun SelfFromLeft(
        modifier: Modifier = Modifier,
        isVisible: Boolean,
        duration: Int = 400,
        content: @Composable () ->  Unit){
        var isObjectVisible by rememberSaveable { mutableStateOf(true) }
        val offsetX by animateDpAsState(
            targetValue = if (isVisible) 0.dp else -screenWidth,
            animationSpec = tween(
                durationMillis = duration,
                easing = Ease
            )
        )

        if (isObjectVisible) {
            Box(
                modifier = modifier
                    .offset {
                        IntOffset(x = offsetX.roundToPx(), y = 0)
                    }
            ) {
                content()
            }
        }

        LaunchedEffect(isVisible){
            isObjectVisible = true
        }

        LaunchedEffect(offsetX){
            if (offsetX == -screenWidth) {
                isObjectVisible = false
            }
        }
    }


    @Composable
    fun AnimateHeight(isVisible: Boolean, height: Dp, content: @Composable () -> Unit) {
        var isObjectVisible by rememberSaveable { mutableStateOf(false) }
        val curHeight by animateDpAsState(
            targetValue = if (isVisible) height else 0.dp,
            animationSpec = tween(500, easing = Ease)
        )

        if (isObjectVisible) {
            Box(
                modifier = Modifier.height(curHeight)
            ) {
                content()
            }
        }

        LaunchedEffect(isVisible){
            isObjectVisible = true
        }

        LaunchedEffect(curHeight){
            if (curHeight == 0.dp) {
                isObjectVisible = false
            }
        }
    }

    @Composable
    fun AppearView(
        isVisible: Boolean,
        modifier: Modifier = Modifier,
        content: @Composable BoxScope.() -> Unit = {}
    ) {
        var isObjectVisible by rememberSaveable { mutableStateOf(true) }
        val alphaAsset by animateFloatAsState(
            targetValue = if (isVisible) 1f else 0f,
            animationSpec = tween(
                durationMillis = 500,
                easing = Ease
            )
        )

        if (isObjectVisible) {
            Box(
                modifier = modifier
                    .graphicsLayer(alpha = alphaAsset)
            ) {
                content()
            }
        }

        LaunchedEffect(isVisible){
            isObjectVisible = true
        }

        LaunchedEffect(alphaAsset){
            if (alphaAsset == 0f) {
                isObjectVisible = false
            }
        }
    }
}