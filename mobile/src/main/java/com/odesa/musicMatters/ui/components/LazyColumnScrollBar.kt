package com.odesa.musicMatters.ui.components

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import com.odesa.musicMatters.utils.toSafeFinite

fun Modifier.drawScrollBar( state: LazyListState ): Modifier = composed {
    val scrollPointerColor = MaterialTheme.colorScheme.surfaceTint
    val isLastItemVisible by remember {
        mutableStateOf(
            state.layoutInfo.visibleItemsInfo.lastOrNull()?.index == state.layoutInfo.totalItemsCount - 1
        )
    }
    val showScrollPointer by remember {
        mutableStateOf(
            !( state.firstVisibleItemIndex == 0 && isLastItemVisible )
        )
    }
    var scrollPointerOffsetY by remember { mutableFloatStateOf( 0f ) }
    val scrollPointerOffsetYAnimated = animateFloatAsState(
        targetValue = scrollPointerOffsetY,
        animationSpec = tween( durationMillis = 50, easing = EaseInOut ),
        label = "c-lazy-column-scroll-pointer-offset-y"
    )
    val showScrollPointerAnimated = animateFloatAsState(
        if ( showScrollPointer ) 1f else 0f,
        animationSpec = tween( durationMillis = 500 ),
        label = "c-lazy-column-scroll-pointer"
    )

    drawWithContent {
        drawContent()
        val newScrollPointerOffsetY =
            if ( isLastItemVisible ) size.height - ContentDrawScopeScrollBarDefaults.scrollPointerHeight.toPx()
            else ( size.height / state.layoutInfo.totalItemsCount ) * state.firstVisibleItemIndex
        scrollPointerOffsetY = newScrollPointerOffsetY.toSafeFinite()
        drawScrollBar(
            scrollPointerColor = scrollPointerColor,
            scrollPointerOffsetY = scrollPointerOffsetYAnimated.value,
            scrollPointerAlpha = showScrollPointerAnimated.value
        )
    }
}