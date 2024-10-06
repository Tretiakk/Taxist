package com.taxi.taxist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension

class Style {

    @Composable
    fun DecorationBackground(
        modifier: Modifier = Modifier,
        color: Color,
        content: @Composable BoxScope.() -> Unit
    ) {
        ConstraintLayout  (
            modifier = modifier
                .height(IntrinsicSize.Min)
        ){
            val (icon1,content,icon2) = createRefs()
            Image(
                modifier = Modifier
                    .aspectRatio(18f / 56f)
                    .constrainAs(icon1){
                        end.linkTo(content.start)
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(content.bottom)

                        height = Dimension.fillToConstraints
                    },
                painter = painterResource(R.drawable.box_left_corner),
                colorFilter = ColorFilter.tint(color),
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )

            Image(
                modifier = Modifier
                    .aspectRatio(18f / 55f)
                    .constrainAs(icon2){
                        end.linkTo(parent.end)
                        start.linkTo(content.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(content.bottom)

                        height = Dimension.fillToConstraints
                    },
                painter = painterResource(R.drawable.box_right_corner),
                colorFilter = ColorFilter.tint(color),
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )

            Box(
                modifier = Modifier
                    .constrainAs(content){
                        start.linkTo(icon1.end)
                        end.linkTo(icon2.start)
                        top.linkTo(parent.top)
                    }
                    .background(color),
            ){
                content()
            }
        }
    }

    @Composable
    fun DecorationBackgroundWithFixedCorners(
        modifier: Modifier = Modifier,
        color: Color,
        widthOfCorners: Dp,
        content: @Composable BoxScope.() -> Unit
    ) {
        ConstraintLayout  (
            modifier = modifier
                .height(IntrinsicSize.Min)
        ){
            val (icon1,content,icon2) = createRefs()
            Image(
                modifier = Modifier
                    .width(widthOfCorners)
                    .constrainAs(icon1){
                        end.linkTo(content.start)
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(content.bottom)

                        height = Dimension.fillToConstraints
                    },
                painter = painterResource(R.drawable.box_left_corner),
                colorFilter = ColorFilter.tint(color),
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )

            Image(
                modifier = Modifier
                    .width(widthOfCorners)
                    .constrainAs(icon2){
                        end.linkTo(parent.end)
                        start.linkTo(content.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(content.bottom)

                        height = Dimension.fillToConstraints
                    },
                painter = painterResource(R.drawable.box_right_corner),
                colorFilter = ColorFilter.tint(color),
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )

            Box(
                modifier = Modifier
                    .constrainAs(content){
                        start.linkTo(icon1.end)
                        end.linkTo(icon2.start)
                        top.linkTo(parent.top)
                    }
                    .background(color),
            ){
                content()
            }

        }
    }
}