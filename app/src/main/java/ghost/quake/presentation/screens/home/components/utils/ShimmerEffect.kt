package ghost.quake.presentation.screens.home.components.utils

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

fun Modifier.shimmerEffect(isDarkMode: Boolean = true): Modifier = composed {
    val shimmerColors = if (isDarkMode) {
        listOf(
            Color(0xFF3C3C3C),
            Color(0xFF4C4C4C),
            Color(0xFF3C3C3C)
        )
    } else {
        listOf(
            Color(0xFFE6E8EB),
            Color(0xFFD4D7DC),
            Color(0xFFE6E8EB)
        )
    }

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    background(
        brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(x = translateAnimation.value - 1000f, y = 0f),
            end = Offset(x = translateAnimation.value, y = 0f)
        )
    )
}
