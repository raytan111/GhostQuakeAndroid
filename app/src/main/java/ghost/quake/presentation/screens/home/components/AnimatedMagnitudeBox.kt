package ghost.quake.presentation.screens.home.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Shadow
import ghost.quake.presentation.theme.getMagnitudeColor

@Composable
fun AnimatedMagnitudeBox(
    magnitude: Double,
    large: Boolean,
    isLastEarthquake: Boolean
) {
    val size = if (large) 80.dp else 60.dp
    val fontSize = if (large) 32.sp else 24.sp

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isLastEarthquake) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val magnitudeColor = getMagnitudeColor(magnitude)
    val shadowColor = remember(magnitudeColor) {
        magnitudeColor.copy(alpha = 0.3f)
    }

    Box(
        modifier = Modifier
            .size(size)
            .scale(if (isLastEarthquake) scale else 1f)
            .shadow(
                elevation = if (isLastEarthquake) 8.dp else 4.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = shadowColor
            )
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        magnitudeColor,
                        magnitudeColor.copy(alpha = 0.8f)
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = if (isLastEarthquake) 2.dp else 0.dp,
                color = Color(0xFF81C784).copy(alpha = if (isLastEarthquake) 1f else 0f),
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "%.1f".format(magnitude),
            color = Color.White,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            style = LocalTextStyle.current.copy(
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.5f),
                    offset = Offset(1f, 1f),
                    blurRadius = 2f
                )
            )
        )
    }
}